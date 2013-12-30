package sg.edu.nus.accesscontrol;

import java.io.Serializable;
import java.sql.Connection;

import sg.edu.nus.util.MetaDataAccess;

public class RoleManagement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7779687649337741061L;

	String[][] privileges;
	String[][] roleHierchies;
	String[][] rolePermissions;
	String[][] roles;

	public RoleManagement() {

	}

	public RoleManagement(Connection conn) {
		readFromDB(conn);
	}

	public void readFromDB(Connection conn) {

		privileges = MetaDataAccess.metaGetFullPrivileges(conn);

		roleHierchies = MetaDataAccess.metaGetFullRoleHierachy(conn);

		rolePermissions = MetaDataAccess.metaGetFullRolePermission(conn);

		roles = MetaDataAccess.metaGetFullRole(conn);
	}

	public void storeToDB(Connection conn) {

		MetaDataAccess.metaStorePrivileges(conn, privileges);

		MetaDataAccess.metaStoreRoleHierachy(conn, roleHierchies);

		MetaDataAccess.metaStoreRole(conn, roles);

		MetaDataAccess.metaStoreRolePermission(conn, rolePermissions);

	}
}
