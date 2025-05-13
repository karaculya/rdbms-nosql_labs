<%@ page import="ssau.nosql_1.model.Artist" %>
<%@ page import="java.util.List" %>
<%@ page import="ssau.nosql_1.dao.util.DaoFactory" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Artists</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<section class="container m-5">
    <%@ include file="header.jsp"%>
    <div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item active"><a href="#">All artists</a></li>
                <li class="breadcrumb-item" aria-current="page">
                    <a href="artistsWithAlbums.jsp">Artists with albums</a></li>
            </ol>
        </nav>

        <!-- Форма добавления артиста, скрытая по умолчанию -->
        <div class="card mb-4" id="addArtistFormContainer" style="display: none;">
            <div class="card-header">Add artist</div>
            <div class="card-body">
                <form id="addArtistForm">
                    <div class="mb-3">
                        <label for="name" class="form-label">Artist Name</label>
                        <input type="text" class="form-control" id="name" name="name" required>
                    </div>
                    <button type="button" class="btn btn-dark" onclick="createArtist()">Add</button>
                </form>
            </div>
        </div>

        <div class="d-flex flex-row flex-wrap gap-2 mb-3">
            <div class="card add-btn d-flex justify-content-center align-items-center"
                 onclick="toggleAddArtistForm()">
                <img src="https://img.icons8.com/ios-glyphs/30/ffffff/plus.png" alt="Add artist">
                <p>Add artist</p>
            </div>
            <%
                List<Artist> artists = DaoFactory.getArtistDao().getAllArtists();
                for (Artist artist : artists) {
            %>
            <div class="card card-form" id="artist-<%= artist.getId() %>">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <h5 class="card-title"><%=artist.getName()%>
                        </h5>
                        <div class="dropdown">
                            <button class="btn more-options" type="button" id="dropdownMenuButton<%= artist.getId()%>"
                                    aria-expanded="false <%=artist.getId()%>" data-bs-toggle="dropdown"
                                    aria-haspopup="true">
                                <img src="https://img.icons8.com/?size=100&id=85460&format=png&color=000000"
                                     alt="More options">
                            </button>
                            <ul class="dropdown-menu dropdown-menu-right"
                                aria-labelledby="dropdownMenuButton<%= artist.getId() %>">
                                <li><a class="dropdown-item" href="artists?action=edit&id=<%= artist.getId() %>">
                                    Редактировать</a>
                                </li>
                                <li><a class="dropdown-item" onclick="deleteArtist(<%= artist.getId() %>)">Удалить</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <%}%>
        </div>
    </div>
</section>
<script>
    function toggleAddArtistForm() {
        const formContainer = document.getElementById("addArtistFormContainer");
        if (formContainer.style.display === "none") {
            formContainer.style.display = "block";
        } else {
            formContainer.style.display = "none";
        }
    }

    function deleteArtist(id) {
        if (confirm("Вы уверены, что хотите удалить этого артиста?")) {
            $.ajax({
                url: 'artists?action=delete&id=' + id,
                type: 'GET',
                success: function () {
                    const element = document.getElementById("artist-" + id)
                    element.parentNode.removeChild(element);
                },
                error: function (xhr, status, error) {
                    console.error("Не удалось удалить артиста:", error);
                }
            });
        }
    }

    function createArtist() {
        const name = $("#name").val();
        if (name) {
            $.ajax({
                url: 'artists',
                type: 'POST',
                data: {
                    'action': 'save',
                    'name': name
                },
                success: function () {
                    location.reload();
                },
                error: function (xhr, status, error) {
                    console.error("Не удалось создать артиста:", error);
                }
            });
        }
    }

</script>
<script src="${pageContext.request.contextPath}/webjars/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>