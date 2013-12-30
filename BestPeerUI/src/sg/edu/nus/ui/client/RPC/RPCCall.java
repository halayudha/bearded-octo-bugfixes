/**
 * Created on Apr 29, 2009
 */
package sg.edu.nus.ui.client.RPC;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * @author David Jiang
 *
 */
public class RPCCall {
	private String url;
	private RequestBuilder builder;
	
	private static RPCCall callee = new RPCCall();
	public RPCCall() {
		url = GWT.getModuleBaseURL() + "bestpeerservlet";
		builder = new RequestBuilder(RequestBuilder.POST, url);
		System.err.println(url);
	}
	
	public static RPCCall get() {
		return callee;
	}
	
	public void invoke(String className, JSONObject params, Callback caller) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("class", new JSONString(className));
		jsonObj.put("params", params);
		System.err.println(jsonObj.toString());
		try {
			builder.sendRequest(jsonObj.toString(), new AsyncResult(caller));
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
			e.printStackTrace();
		}
	}
}
