/**
 * Created on Apr 29, 2009
 */
package sg.edu.nus.ui.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author David Jiang
 * 
 */
@SuppressWarnings("serial")
public class BestPeerServlet extends HttpServlet {
	private static JSONParser parser = new JSONParser();
	private static String SERVICE_PKG = "sg.edu.nus.ui.server.services";
	private static String SERVICE_METHOD_NAME = "doService";

	/**
	 * Set PROXY = false in production Set PROXY = true in develop phase
	 */
	private static boolean PROXY = false;
	private static String REAL_SERVERLET = "http://localhost:8088/bestpeerservlet";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(req
				.getInputStream()));
		StringBuffer data = new StringBuffer();
		String buf = input.readLine();
		while (buf != null) {
			data.append(buf);
			buf = input.readLine();
		}
		if (PROXY == false) {
			try {
				JSONObject request;
				synchronized (parser) {
					request = (JSONObject) parser.parse(data.toString());
				}
				String servName = (String) request.get("class");
				JSONObject params = (JSONObject) request.get("params");
				Class<?> c = Class.forName(SERVICE_PKG + "." + servName);
				Object servObj = c.newInstance();
				Method m = c.getMethod(SERVICE_METHOD_NAME, JSONObject.class);
				JSONObject result = (JSONObject) m.invoke(servObj, params);
				if (result != null)
					resp.getWriter().print(result.toString());
			} catch (ParseException e) {
				System.err.println(e);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else { // Forward the RPC call to target servlet
			HttpClient client = new DefaultHttpClient();
			HttpPost postMessage = new HttpPost(REAL_SERVERLET);
			postMessage.setEntity(new StringEntity(data.toString()));
			System.out.println("Forward: " + data.toString());
			HttpResponse response = client.execute(postMessage);
			System.out.println("RECV: " + response.getStatusLine());
			if (response.getStatusLine().getStatusCode() == 200) {
				BufferedReader output = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				StringBuffer outData = new StringBuffer();
				String tmp = output.readLine();
				while (tmp != null) {
					outData.append(tmp);
					tmp = output.readLine();
				}
				if (outData.toString().length() > 0) {
					System.out.println("RETURN: " + outData.toString());
					resp.getWriter().print(outData.toString());
				}
			} else {
				resp.setStatus(404);
			}
		}
	}
}
