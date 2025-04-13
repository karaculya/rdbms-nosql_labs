<%@ page import="ssau.nosql_1.model.Composition" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit composition</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="container">
    <div class="row" style="height: 100vh;">
        <div class="col d-flex justify-content-center align-items-center">
            <%
                Composition composition = (Composition) request.getAttribute("composition");
                if (composition == null) {
                    return;
                }
            %>
            <form class="editForm" action="compositions?album_id=<%= composition.getAlbum().getId()%>" method="post">
                <h3 class="card-title">Edit composition</h3>
                <input type="hidden" name="id" value="<%= composition.getId()%>">
                <input type="hidden" name="action" value="edit">
                <label for="name" class="form-label">Name</label>
                <input type="text" class="form-control" id="name" name="name" value="<%=composition.getName()%>" required>
                <label for="duration" class="form-label">Duration</label>
                <input type="time" step="1" class="form-control" id="duration" name="duration"
                       value="<%=composition.getDuration()%>" required>
                <div class="d-flex justify-content-center align-items-center">
                    <button type="submit" class="btn btn-primary my-4">Save</button>
                </div>
            </form>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/webjars/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/js/bootstrap.bundle.min.js"></script>
<script>
    const compositionId = '<%= composition.getId() %>';
    const albumId = '<%= composition.getAlbum().getId() %>';
    console.log("Composition ID:", compositionId);
    console.log("Album ID:", albumId);
</script>
</body>
</html>