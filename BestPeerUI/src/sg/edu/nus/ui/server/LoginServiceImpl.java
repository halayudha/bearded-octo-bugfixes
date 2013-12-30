/**
 * Created on Apr 28, 2009
 */
package sg.edu.nus.ui.server;

import sg.edu.nus.ui.client.RPC.LoginInfo;
import sg.edu.nus.ui.client.RPC.LoginService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author David Jiang
 *
 */
@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	/* (non-Javadoc)
	 * @see sg.edu.nus.ui.client.RPCInterface.LoginService#login(java.lang.String, java.lang.String)
	 */
	@Override
	public LoginInfo login(String username, String password) {
		System.err.println("In Service user:" + username + " pass: " + password);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
