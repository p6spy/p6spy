<%@ page import="com.p6spy.engine.spy.*"%>
<%@ page import="com.p6spy.engine.common.*"%>
<%@ page import="com.p6spy.engine.outage.*"%>
<%
    // what actions can we do (currently just add a line to the log)
	String actionParam = "doAction";
	String action_LogLine = "logLine";
	String action_ShowOptions = "showOptions";
	
	// status string for completed actions
	String actionStatus = null;
	
	// line templates for creating a line in the log file
	String lineTemplateParam = "lineTemplate";
	String customLineParam = "customLine";
	String lineTemplate_Dashes = "LT_Dashes";
	String lineTemplate_Dashes2 = "LT_Dashes2";
	String lineTemplate_Custom = "LT_Custom";
	String customLineStr = "";

	Object actionFlag = request.getParameter(actionParam);
	if (actionFlag == null) actionFlag = action_ShowOptions;
	
	// did the user submit a request to enter a line in the P6 log?
	if (action_LogLine.equals(actionFlag.toString()))
	{
		// figure out what template the user wants
		Object lineTemplateObj = request.getParameter(lineTemplateParam);
		String lineTemplateStr = lineTemplate_Dashes;
		if (lineTemplateObj != null) lineTemplateStr = lineTemplateObj.toString();
		
		// get the custom string part if available
		Object customLineObj = request.getParameter(customLineParam);
		if (customLineObj != null) customLineStr = customLineObj.toString();
		
		// make entry in the log using the right template
		if (lineTemplateStr.equals(lineTemplate_Dashes))
		{
			java.util.Date now = new java.util.Date();
	        java.text.SimpleDateFormat sdf = P6SpyOptions.getDateformatter();
	        String dateString;
	        if (sdf == null) {
	            dateString = Long.toString(now.getTime());
	        } else {
	            dateString = sdf.format(new java.util.Date(now.getTime())).trim();
	        }
 			
						
			P6LogQuery.logText("\n----------------------- "+dateString+
				" ----------------------\n");
		}
		else if (lineTemplateStr.equals(lineTemplate_Dashes2))
		{
			P6LogQuery.logText("\n----------------------- "+customLineStr+
				" ----------------------\n");
		}
		else if (lineTemplateStr.equals(lineTemplate_Custom))
		{
			P6LogQuery.logText("\n"+customLineStr+"\n");
		}
		actionStatus = "added a line to the P6Spy log.";
	}
%>
	
<p><h2>P6Spy Activity Page</h2></p>

<%
    if (actionStatus != null)
	{
%>
	<p><b><%=actionStatus%> </b></p>
<%
    }
%>	

<p><h3>P6Spy Configuration Information:</h3></p>
<p>
Driver: <%=P6SpyOptions.getRealdriver()%> <br>
Stack Trace Enabled: <%=P6SpyOptions.getStackTrace()%> <br>	
Reload Properties Enabled: <%=P6SpyOptions.getReloadProperties()%> <br>	
Outage Detection Enabled: <%= P6OutageOptions.getOutageDetection() %> <br>	
</p>

<p><h3>Create a demarcation in the P6Spy Log file:</h3></p>
<p>
This feature is useful if you wish to associate log entries with a specific
application action. For instance, if you want to see the queries generated
by looking at the first page of a website, you will want to make a mark in 
the log before and after your browser hits the page.
</p>
<p>
There are 3 options for the type of line you can add:<br>
1) a line with all dashes, broken in the middle by a timestamp<br>
2) a line with all dashes, broken in the middle by a custom string<br>
3) a line of your own design
</p>
	
<p>
<form action="p6spy.jsp" method="GET">
	<input type=hidden name="<%= actionParam %>" value="<%= action_LogLine %>" />
	
	<p>
	<select multiple name="<%= lineTemplateParam %>" size=3>
		<option value="<%= lineTemplate_Dashes %>">Dashed line with Timestamp</option>
		<option value="<%= lineTemplate_Dashes2 %>">Dashed line with Custom String</option>
		<option value="<%= lineTemplate_Custom %>">Custom line</option>
	</select>
	</p>
	<p>
	Custom string (if needed): <br>
	<input type=text name="<%= customLineParam %>" value="<%= customLineStr %>" size=40>
	</p>		
	<p>
	<input type="submit" value="logLine">
	</p>		
</form>					
</p>
