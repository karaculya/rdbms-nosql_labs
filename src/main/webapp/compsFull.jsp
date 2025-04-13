<%@ page import="ssau.nosql_1.dao.util.DaoFactory" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Full join</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/js/bootstrap.bundle.min.js">
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.14.1/themes/base/jquery-ui.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<section class="container m-5">
    <%@ include file="header.jsp" %>
    <div class="container">
        <table class="table">
            <thead>
            <tr>
                <th scope="col">#</th>
                <th scope="col">Name</th>
                <th scope="col">Duration</th>
                <th scope="col">Album</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Object[]> objects = DaoFactory.getCompositionDao().getFullTable();
                if (objects == null || objects.isEmpty()) {
                    out.print("<h2>No compositions</h2>");
                } else {
                    for (Object[] object : objects) {
            %>
            <tr>
                <th scope="row"><%=object[0]%></th>
                <td><%=object[1]%></td>
                <td><%=object[2]%></td>
                <td><%=object[3]%></td>
            </tr>
            <%
                    }
                }
            %>
            </tbody>
        </table>
    </div>
</section>
</body>
</html>