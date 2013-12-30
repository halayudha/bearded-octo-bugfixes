<%@ page import="sg.edu.nus.peer.ServerPeer" %>
<%@ page import="sg.edu.nus.gui.server.ServerGUI" %>
<%@ page import="sg.edu.nus.util.MetaDataAccess" %>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%
	//get table names from server's db
	ServerPeer server = ServerGUI.instance.peer();
	String[] table = MetaDataAccess.metaGetTables(server.conn_metabestpeerdb);
	out.print("<table>");
	for(int i=0; i<table.length; i++)
	{
		out.print("<name>"+table[i]+"</name>");
	}	
	out.print("</table>");
%>
