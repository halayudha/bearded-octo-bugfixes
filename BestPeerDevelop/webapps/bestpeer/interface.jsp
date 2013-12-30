<%@ page import="java.util.*" %>
<%@ page import="sg.edu.nus.peer.ServerPeer" %>
<%@ page import="sg.edu.nus.gui.server.ServerGUI" %>
<%@ page import="sg.edu.nus.util.MetaDataAccess" %>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Query Form</title>
<style type="text/css">
<!--
.style2 {
	font-size: 30px;
	color: #0066FF;
}
-->
</style>

<script language="javascript">

	var XMLHttpReq;	
    //XMLHttpRequest          
    function removeNL(s) {
 	 	r = "";
  		for (i=0; i < s.length; i++) {
    	if (s.charAt(i) != '\n' &&
        	s.charAt(i) != '\r' &&
        	s.charAt(i) != '\t') {
	      	r += s.charAt(i);
    	  }
    	}
  		return r;
  	}

   
    
    function createXMLHttpRequest() {
		if(window.XMLHttpRequest) { //Mozilla 
		  XMLHttpReq = new XMLHttpRequest();
		}
		else if (window.ActiveXObject) { // IE
		  try {
			XMLHttpReq = new ActiveXObject(" Msxml2.XMLHTTP" );
		  } catch (e) {
			try {
			  XMLHttpReq = new ActiveXObject(" Microsoft.XMLHTTP" );
			} catch (e) { }
		  }
		}
	}
	//
	function sendRequest() {
		createXMLHttpRequest();		
		var TableName = document.getElementById("tname").value;
		//alert(TableName);
		var url;
		if(TableName!='null')
		   url = "/bestpeer/getcolumn.jsp?tablename=" +TableName;
		else url = "/bestpeer/getcolumn.jsp?tablename=" +document.getElementById("tableName").options[document.getElementById("tableName").selectedIndex].value;
		XMLHttpReq.open("GET" , url, true);
		XMLHttpReq.onreadystatechange = processResponse; //
		XMLHttpReq.send(null);   // 
	}
  // 
	function processResponse() {
		if (XMLHttpReq.readyState == 4) { // 
		if (XMLHttpReq.status == 200) { // 
			showOptions();
			} else { //
				window.alert("invalid web page" );
			}
		}
	}
// 
	function showOptions() {		
		var xmlobj = XMLHttpReq.responseText;
		xmlobj = removeNL(xmlobj);
		var optvalues = xmlobj.split(",");		
		for(i=0;i<optvalues.length-1;i++) {
    		option1 = new Option(optvalues[i], optvalues[i]);
			document.form1.Attribute.options[i] = option1;	
		}
	}
	
</script> 

</head>

<body>
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

<table width="679"  border="0" align="center">
<tr><td>
<table  align="right" >
<tr> <td> 
<%
	ServerPeer server = ServerGUI.instance.peer();
	String user = server.getServerPeerAdminName();
	Date date = new Date();
	out.println(date.toString() + ", ");
    out.println(user);
%>
</td> </tr>
</table>
</td></tr>
</table>

<%

  String[] table = MetaDataAccess.metaGetTables(server.conn_metabestpeerdb);
  //Hashtable<String, String[]> map = new Hashtable<String, String[]>();
  String[] attribute = {"id", "name"};
  String[] operations = {"=", ">", "<"};
  
   
  //restore the input expression 
  int expressionNumber = 0;
  boolean flag = false;
  String tableName = "";
  ArrayList<String> attName = new ArrayList<String>();
  ArrayList<String> ops = new ArrayList<String>();
  ArrayList<String> values = new ArrayList<String>();
  ArrayList<Boolean> stringFlag = new ArrayList<Boolean>();
  
  if(request.getParameter("Submit") != null) 
  {
  		String sql = "";
		if(session.getAttribute("exp_no")!=null)
		{
			expressionNumber = ((Integer)session.getAttribute("exp_no")).intValue();
			tableName = (String)session.getAttribute("table_name");
			
			//generate the sql query
			sql = "select * from " + tableName + " where ";
			for(int i=0; i<expressionNumber; i++)
	  	  	{
	      		 String name = (String)session.getAttribute("att_name"+i);
			     String op = (String)session.getAttribute("op"+i);
			  	 String value = (String)session.getAttribute("value"+i);
			   	 Boolean sf = (Boolean)session.getAttribute("isstring"+i);
				 if(sf==true)
				 {
				 	sql += name + op + 	"'" + value + "'";	
				 }	  	    	 
				 else sql += name + op +  value;	
				 sql += " and ";
		    }
			
			String newatt = request.getParameter("Attribute");
			String newop = request.getParameter("OP");
			String value = request.getParameter("Value");
			Boolean isString = false;
			
			if(newatt!=null && newop!=null && value!=null && value.length()>0)
			{
				//Boolean isString = false;
				if(request.getParameter("isString")!=null)
				{
					isString = true;				
				}
				if(isString)
				{
					sql += newatt + newop + "'" + value + "'";
				}
				else sql += newatt + newop + value;
			}
			else
			{
				sql = sql.substring(0, sql.length()-5);
			}
				
		}
		else
		{			
			String newatt = request.getParameter("Attribute");
			String newop = request.getParameter("OP");
			String value = request.getParameter("Value");
			tableName = request.getParameter("tableName");
			sql = "select * from " + tableName; 
			if(newatt!=null && newop!=null && value!=null && value.length()>0)
			{
				sql += " where ";
				Boolean isString = false;
				if(request.getParameter("isString")!=null)
				{
					isString = true;				
				}
				if(isString)
				{
					sql += newatt + newop + "'" + value + "'";
				}
				else sql += newatt + newop + value;
			}
		}
		
		//clear the session
		for(int i=0; i<expressionNumber; i++)
		{
			session.removeAttribute("att_name"+i);
			session.removeAttribute("op"+i);
			session.removeAttribute("value"+i);
			session.removeAttribute("isstring"+i);
		}
		session.removeAttribute("exp_no");	
		session.removeAttribute("table_name");
		//out.print("sql query is : " + sql);
		session.setAttribute("sql", sql);		
		//response.sendRedirect("wait.html");	
		response.sendRedirect("queryprocess.jsp");
  }
  else 
  {
  	  if(session.getAttribute("exp_no")!=null)
	  {
  		  flag = true;
	  	  expressionNumber = ((Integer)session.getAttribute("exp_no")).intValue();
		  tableName = (String)session.getAttribute("table_name");
		  for(int i=0; i<expressionNumber; i++)
	  	  {
	      	 String name = (String)session.getAttribute("att_name"+i);
		     String op = (String)session.getAttribute("op"+i);
		  	 String value = (String)session.getAttribute("value"+i);
		   	 Boolean sf = (Boolean)session.getAttribute("isstring"+i);
	  	     attName.add(name);
	  	     ops.add(op);
		     values.add(value);
		     stringFlag.add(sf);
		  }  	  
  	  }
  
	  //get new expression
	  if(expressionNumber>0 || request.getParameter("Attribute")!=null)
      {
  			flag = true;
			if(expressionNumber == 0)
			{
				tableName = request.getParameter("tableName");
				session.setAttribute("table_name", tableName);
			}
	  		
  			String newatt = request.getParameter("Attribute");
			String newop = request.getParameter("OP");
			String value = request.getParameter("Value");
			Boolean isString = false;
			
			if(request.getParameter("isString")!=null)
			{
				isString = true;
			}						
			
			//out.println("####:" + tableName + " " + newatt + " " + newop + " " + value + " " + isString);			
			
			session.setAttribute("exp_no", expressionNumber+1);
			session.setAttribute("att_name"+expressionNumber, newatt);
			session.setAttribute("op"+expressionNumber, newop);
			session.setAttribute("value"+expressionNumber, value);
			session.setAttribute("isstring"+expressionNumber, isString);
			attName.add(newatt);
 		    ops.add(newop);
	   		values.add(value);
		    stringFlag.add(isString);
  	  }
  }
  
  
  
%>
<div align="center">Select Table For Searching:</div>
<form id="form1" name="form1" method="post" action="interface.jsp">
<table width="699" border="1" align="center">
  <tr>
    <td width="185">
      <label>Table Name : 
      <%
	  	  if(flag == true)
		  {
		  		out.print("<input type=\"hidden\" id=\"tname\" name=\"tname\" value=\"" + tableName + "\"/>");
	  			out.print("<select disabled=\"disabled\" name=\"tableName\" id=\"tableName\">");
				out.print("<option value=\""+ tableName + "\" selected=\"selected\" >" + tableName + "</option>");   			   				
		  }		
		  else
		  {
		  	   out.print("<input type=\"hidden\" id=\"tname\" name=\"tname\" value=\"null\"/>");
		  	   out.print("<select name=\"tableName\" id=\"tableName\" >");
		  	   for(int i=0; i<table.length; i++)
 			   {
				  out.print("<option value=\""+ table[i] + "\">" + table[i] + "</option>");			
			   }
		  }		 
	  %>
      </select>
      </label>
      <br /></td>
    <td width="498">
    <table>  
    <% 
	  int next = 0;
	  if(flag==true)
	   	next = expressionNumber + 1;
	     for(int i=0; i<next; i++)
	     {
	  	   String name =  attName.get(i);
		   String op = ops.get(i);
		   String value = values.get(i);
		   Boolean sf = stringFlag.get(i);
	%>
      <tr> 
    <td width="468"> 
    <label>Attribute: <select  disabled="disabled" name="Attribute<%=i%>" id="Attribute<%=i%>">      
      <option value="0"><%=name%></option></select></label>
        
        <label><select disabled="disabled" name="OP<%=i%>" id="OP<%=i%>">
           <option value="0"><%=op%></option> </select></label>
           
         <label>
         <input disabled="disabled" type="text" name="Value<%=i%>" id="Value<%=i%>" value="<%=value%>" />
         </label>
         <% 
		    if(sf.booleanValue()==true)
			{
		 %>
         <label>
         <input disabled="disabled" type="checkbox" name="isString<%=i%>" id="isString<%=i%>" checked="checked" />
         is string?</label>    
         <%
		   }
		   else{
		 %>
         <label>
         <input disabled="disabled" type="checkbox" name="isString<%=i%>" id="isString<%=i%>"/>
         is string?</label> 
         <% } %>
         </td>
	    </tr>
          <%		  
		  }
         %>
         <tr> <td>         
           <label>Attribute: <select  name="Attribute" id="Attribute" ONFOCUS='sendRequest()'>
          </select>
          </label> 
          
          <label>
           <select name="OP" id="OP">
          <%
		  	 for(int i=0; i<operations.length; i++)
			 {
			 	out.print("<option value=\""+operations[i]+"\">" + operations[i]  +"</option>");
			 }
		  %>
          </select></label>         
          
          
          <label>
         <input type="text" name="Value" id="Value" />
         </label>
         
          <label>
         <input type="checkbox" name="isString" id="isString" />
         is string?</label>  
          
          </td></tr>
          
          
    <tr> <td align="right"> <input type="submit" name="Add" id="Add" value="Add New Expression" /></td></tr>
    </table>
    </td>
  </tr>
</table>
  <div width="679" align="center">
  <label>
  <input align="right" type="submit" name="Submit" id="Submit" value="Submit Query" />
  </label>
  </div>
</form>

</body>
</html>
