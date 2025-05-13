<%@ page import="ssau.nosql_1.model.Artist" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit artist</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="container">
    <div class="row" style="height: 100vh;">
        <div class="col d-flex justify-content-center align-items-center">
            <%
                Artist artist = (Artist) request.getAttribute("artist");
                if (artist == null) {
                    return;
                }
            %>
            <form class="editForm" action="artists" method="post">
                <h3 class="text-center my-3">Edit artist</h3>
                <input type="hidden" name="id" value="<%= artist.getId() %>">
                <input type="hidden" name="action" value="edit">
                <label for="name" class="form-label text-start mb-2">Name</label>
                <input type="text" name="name" id="name" class="form-control" value="<%= artist.getName() %>" placeholder="Name" required>
                <div class="d-flex justify-content-center align-items-center">
                    <button type="submit" class="btn btn-primary my-4">Save</button>
                </div>
            </form>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/webjars/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>
