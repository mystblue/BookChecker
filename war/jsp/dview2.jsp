<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.Query" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="project.blue.data.LinkData" %>
<%@ page import="project.blue.data.PMF" %>

<html>
<head>
<meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=2">
<title>Dawnfun</title>
</head>
  <body>

<%
	PersistenceManager pm = PMF.get().getPersistenceManager();
	Query query = pm.newQuery(LinkData.class);
	query.setOrdering("date desc");
	query.setFilter("siteName == dawnfun");
	query.declareParameters("String dawnfun");
	String queryStr = "select from " + LinkData.class.getName();
	query.setRange(0, 300);
	List<LinkData> dataList = (List<LinkData>) query.execute("Dawnfun");
    if (dataList.isEmpty()) {
%>
<p>No data.</p>
<%
    } else {
        for (LinkData g : dataList) {
            if (g.getUrl() != null) {
%>
<p><a href="<%= g.getUrl() %>"><%= g.getTitle() %></a>：<%= g.getDate() %>　： <%= g.getSiteName() %></p>
<p><img src="<%= g.getImagePath() %>"></p>
<%
            }
%>
<%
        }
    }
    pm.close();
%>

  </body>
</html>