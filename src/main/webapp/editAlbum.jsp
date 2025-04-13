<%@ page import="ssau.nosql_1.model.Album" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit album</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="container">
    <div class="row" style="height: 100vh;">
        <div class="col d-flex justify-content-center align-items-center">
            <%
                Album album = (Album) request.getAttribute("album");
                if (album == null) {
                    return;
                }
            %>
            <form class="editForm" action="albums?id_artist=<%=album.getArtist().getId()%>" method="post">
                <h3 class="card-title">Edit album</h3>
                <input type="hidden" name="id" value="<%=album.getId()%>">
                <input type="hidden" name="action" value="edit">
                <label for="name" class="form-label">Name</label>
                <input type="text" class="form-control" id="name" name="name" value="<%=album.getName()%>" required>
                <label for="genre" class="form-label">Genre</label>
                <input type="text" class="form-control" id="genre" name="genre" value="<%=album.getGenre()%>"
                       required>
                <br>
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