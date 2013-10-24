<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.Query" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="project.blue.data.URLData" %>
<%@ page import="project.blue.data.PMF" %>

<html>
<head>
<meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=2">
</head>
  <body>

<%
    PersistenceManager pm = PMF.get().getPersistenceManager();
    String queryStr = "select from " + URLData.class.getName();
    queryStr += " order by date desc";
    Query query = pm.newQuery(queryStr);
    query.setRange(0, 300);
    List<URLData> greetings = (List<URLData>) query.execute();
    if (greetings.isEmpty()) {
%>
<p>The guestbook has no messages.</p>
<%
    } else {
        for (URLData g : greetings) {
            if (g.getUrl() == null) {
%>
<p>An anonymous person wrote:</p>
<%
            } else {
%>
<p><a href="<%= g.getUrl() %>"><%= g.getTitle() %></a>：<%= g.getDate() %></p>
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