/**
 * Created on Apr 28, 2009
 */
package sg.edu.nus.ui.client.RPC;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author David Jiang
 *
 */
@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	LoginInfo login(String username, String password);
}
