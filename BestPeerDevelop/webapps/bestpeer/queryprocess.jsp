<%@ page import="sg.edu.nus.bestpeer.queryprocessing.SelectExecutor" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Please Wait...</title>   

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
	String sql;
	String result = "null";
	sql = (String)session.getAttribute("sql"); 	
	out.print("sql:" + sql);
	if(sql==null || sql.equals("null"))
	{
		result = "null";
	}
	else
	{
		
		SelectExecutor selectExecutor = new SelectExecutor(sql);
		if (!selectExecutor.isLegalQuery())
			result = "null";
		else
		{		
			out.print("<br/> start to process ");
			if (selectExecutor.isSingleTableQuery()) {
				result = selectExecutor.singleTableWebSearch();
			} 
			else {
				//to do
				result = "null";
			}
			out.print("<br/> end process ");
			out.print("<br/>"+result);
		}	
	}

	if(result==null || result.equals("null"))
	{
%>
		<script type="text/javascript">
		<!--
			alert("cannot process the query, please check the syntax");
			window.location = "interface.jsp"			
		-->
		</script>
<%
	}
	else
	{			
		out.print("<br>here it" + result);
		session.setAttribute("sql", result);
		response.sendRedirect("queryresult.jsp");		
    }    
%>

</body>
</html>