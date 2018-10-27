<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
    String path = request.getContextPath();
%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>Auto技术周报</title>
</head>
<body>
<p style="text-align:left;">
    ##行业新闻
</p>
<p style="text-align:left;">
    <c:forEach items="${newspost}" var="postnew">
        ${postnew.title}<br>
        ${postnew.digest}<br>
        <br>
    </c:forEach>
</p>
<p style="text-align:left;">
    ##Android开发
</p>
<p style="text-align:left;">
    <c:forEach items="${androidspost}" var="androidpost">
        ${androidpost.title}<br>
        ${androidpost.digest}<br>
        <br>
    </c:forEach>
</p>
<p style="text-align:left;">
    ##技术纵横
</p>
<p style="text-align:left;">
    <c:forEach items="${extendspost}" var="extendpost">
        ${extendpost.title}<br>
        ${extendpost.digest}<br>
        <br>
    </c:forEach>
</p>

</body>
</html>