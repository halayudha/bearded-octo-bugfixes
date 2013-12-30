<%@ page import="sg.edu.nus.peer.ServerPeer" %>
<%@ page import="sg.edu.nus.gui.server.ServerGUI" %>
<%@ page import="sg.edu.nus.util.MetaDataAccess" %>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%
	//get table names from server's db
	ServerPeer server = ServerGUI.instance.peer();
	String table = request.getParameter("tablename");
	String[][] columns = MetaDataAccess.metaGetColumns(server.conn_metabestpeerdb, table);
	//out.print("<column>");	
	for(int j=0; j<columns.length; j++)
	{
//		out.print("<option value=\"" + columns[j][0] + "\">"+columns[j][0]+"</option>");
		out.print(columns[j][0] + ",");
	}
	//out.print("</column>");
%>
