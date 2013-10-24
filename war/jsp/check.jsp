<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="project.blue.util.HttpUtil" %>

<html>
<head>
<meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=2">
<meta charset="utf-8">
</head>
<body>

<%
  String val = "";
  String param = request.getParameter("url");
  val = HttpUtil.download(param);
%>
<form action="/jsp/check.jsp" method="post">
<input type="text" name="url" size="200" />
<textarea name="value" rows="10" cols="80"><%=val %></textarea>
<input type="submit" value="送信">
</form>
</body>
</html>