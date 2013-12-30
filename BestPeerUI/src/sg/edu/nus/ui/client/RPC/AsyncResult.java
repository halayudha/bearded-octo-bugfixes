/**
 * Created on Apr 29, 2009
 */
package sg.edu.nus.ui.client.RPC;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * @author David Jiang
 *
 */
public class AsyncResult implements RequestCallback {
	Callback caller;
	
	public AsyncResult(Callback caller) {
		this.caller = caller;
	}
	
	public AsyncResult setCaller(Callback caller) {
		this.caller = caller;
		return this;
	}
	
	@Override
	public void onError(Request request, Throwable exception) {
		caller.onFailure();
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		if(response.getStatusCode() != 200) {
			caller.onFailure();
		}
		String jsonResult = response.getText();
		if(jsonResult == null || jsonResult.length() == 0) {
			caller.onReady(null);
			return;
		}
		
		JSONObject result = (JSONObject)(JSONParser.parse(response.getText()));
		caller.onReady(result);
	}

}
