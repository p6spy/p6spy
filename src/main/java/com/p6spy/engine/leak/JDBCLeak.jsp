<%@ page language="java" %>
<%@ page import = "java.util.*" %>
<%@ page import = "org.apache.commons.lang.*" %>
<%@ page import = "com.p6spy.engine.leak.*" %>

<html>

<head>
  <title>JDBC Leak Detecter</title>
  <meta name="robots" content="none" />
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
</head>

<body>

<h1>JDBC Leak Detecter</h1>

<table class="bodyfont" width="99%" border="1">
<%
  Map map = P6Objects.getOpenObjects();

  if (map.isEmpty()) {
    out.println("<td><tr>No JDBC leaks detected</td></tr>");
  } else {
    Iterator iterator = map.entrySet().iterator();
    while (iterator.hasNext()) {
      out.println("<tr>");
      Map.Entry entry = (Map.Entry)iterator.next();
      Object object = entry.getKey();
      String stackTrace = (String)entry.getValue();
      String clazz = object.getClass().getName();
      out.println("<td nowrap>");
      out.println(clazz);
      out.println("</td>");
      out.println("<td nowrap>");
      out.println(StringUtils.replace(stackTrace,"\n", "<br />"));
      out.println("</td>");
      out.println("</tr>");
    }
  }
%>
</table>
</body>
</html>
