/**
 * Created on Apr 28, 2009
 */
package sg.edu.nus.ui.client.RPC;



import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author David Jiang
 *
 */
public interface LoginServiceAsync {
	void login(String username, String password, AsyncCallback<LoginInfo> callback);
}
