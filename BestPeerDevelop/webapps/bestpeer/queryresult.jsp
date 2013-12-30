<%@ page import="sg.edu.nus.peer.ServerPeer" %>
<%@ page import="java.sql.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Query Result</title>   

<style type="text/css">
<!--
.style2 {
	font-size: 30px;
	color: #0066FF;
}
-->
</style>

</head>

<body>

<%
  int intPageSize;			//record in one page
  int intRowCount;			//total row number
  int intPageCount;			//total page number
  int intPage;             //page to be displayed  
  String strPage;
  String[] attName;        //attributes in the result

  int i;

  //get sql query
  String strSQL = (String)session.getAttribute("sql");
  
  //set the number of records in one page
  intPageSize = 20;

  //get page no
  strPage = request.getParameter("page");
  if(strPage==null){//if null, this is the first page
    intPage = 1;    
  }
  else{
    intPage = Integer.parseInt(strPage);
 	 if(intPage<1) 
        intPage = 1;
  }

  Connection sqlCon = ServerPeer.conn_exportDatabase; 
  Statement sqlStmt = sqlCon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

  
  
  //out.println("get:" + strSQL);
  
  if(strSQL != null)
  {
  
  		//need to sort?
  		String sort_att = request.getParameter("sort");
  		if(sort_att!=null)
  			strSQL = strSQL + " ORDER BY " + sort_att;
  		
  		ResultSet sqlRst = sqlStmt.executeQuery(strSQL);
  		//get total number of result
  		sqlRst.last();
		intRowCount = sqlRst.getRow();
		intPageCount = (intRowCount+intPageSize-1) / intPageSize;
		
		//compute the number of total page
		intPageCount = (intRowCount+intPageSize-1) / intPageSize;
		
		//current page no
		if(intPage>intPageCount) 
			intPage = intPageCount;
		
		//get column name
		int cnumber = sqlRst.getMetaData().getColumnCount();
		attName = new String[cnumber];
		for(i=1; i<=attName.length; i++)
			attName[i-1] = sqlRst.getMetaData().getColumnName(i);
   
%>

<table width="679" border="0" align="center">
  <tr>
    <td width="871" height="72"><img src="images/logo.gif" alt="" width="134" height="61" /></td>
    <td width="602" rowspan="2"><div align="center"><img src="images/bestpeer.gif" alt="" width="494" height="164" /></div></td>
  </tr>
  <tr>
    <td height="81"><span class="style2">Welcome to BestPeer</span></td>
  </tr>
  <tr><td colspan="2"><img src="images/spacer2.gif" width="700" height="10" /> </td> 
  </tr>
</table>
<div align="center"><a href="interface.jsp">input new queries</a></div>
<br/>
<div align="center">Query Result:</div>
<br/>
<table width="700" border="1" cellspacing="0" cellpadding="0" align="center">
<tr>
<%
 		for(i=0; i<attName.length; i++)
 			out.print("<td>" + attName[i] + "</td>");
%>
</tr>

<%
		if(intPageCount>0)
		{
 			//set pointer to the first record of the corresponding page
 			sqlRst.absolute((intPage-1) * intPageSize + 1);

 			//show records
 			i = 0;
 			while(i<intPageSize && !sqlRst.isAfterLast())
			{
				out.print("<tr>");
				for(int j=0; j<attName.length; j++)
				{
					out.print("<td>" + sqlRst.getString(attName[j]) + "</td>");
				}
				sqlRst.next();
				i++;
		    }
		 }
%>

</table>

<div align=center> 
<%
   		if(intPage>1)
   		{
%>
			<a href="queryresult.jsp?page=<%=intPage-1%>">Last Page</a>&nbsp; 
<% 
        }
        int window = Math.min(6, intPageCount);
        int start = 1;
        if(intPage - window/2 >0)
        {
        	start = intPage - window/2;
        }
        
        for(i=start; i<start + window; i++)
        {
        	if(i!=intPage)
        	{
        		out.print("<a href=\"queryresult.jsp?page="+i+"\">" + i + "</a>&nbsp;");
        	}
        	else
        		out.print(i+"&nbsp;");
        }
        
        if(intPage<intPageCount)
   		{
%>
			<a href="queryresult.jsp?page=<%=intPage+1%>">Next Page</a>&nbsp;&nbsp;
<%
        }
		//close resultset
		sqlRst.close();
%>				
		<form id="form1" name="form1" method="post" action="queryresult.jsp">
		<label>Sort by: <select  name="sort" id="sort">    
<%
		for(i=1; i<=attName.length; i++)
		{
			out.print("<option value=\"" + attName[i-1] + "\">" + attName[i-1] + "</option>");
		}
%>		
		</select></label>	
		<input type="submit" name="OK" id="OK" value="submit" />
<%
	}
	else
	{//get no result
%>
		<div align="center">No result is found. Please check the query.</div>		
<%
	}
%>
</div>
</body>
</html>