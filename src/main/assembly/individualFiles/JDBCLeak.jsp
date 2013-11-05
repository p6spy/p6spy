<%--
  #%L
  P6Spy
  %%
  Copyright (C) 2013 P6Spy
  %%
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  #L%
  --%>
<%@ page language="java" %>
<%@ page import = "java.util.*" %>
<%@ page import = "com.p6spy.engine.leak.*" %>

<html>

<head>
  <title>JDBC Leak Detecter</title>
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
      out.println(stackTrace.replaceAll("\n", "<br />"));
      out.println("</td>");
      out.println("</tr>");
    }
  }
%>
</table>
</body>
</html>
