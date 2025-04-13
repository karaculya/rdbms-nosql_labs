<%@ page import="java.util.List" %>
<%@ page import="ssau.nosql_1.dao.util.DaoFactory" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Artists with albums</title>
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
                <li class="breadcrumb-item"><a href="artists.jsp">All artists</a></li>
                <li class="breadcrumb-item active" aria-current="page">
                    <a href="#">Artists with albums</a></li>
            </ol>
        </nav>

        <%
            List<Object[]> artistsWithAlbums = DaoFactory.getArtistDao().getArtistWithAlbums();
            String displayClass = artistsWithAlbums == null ? "d-none" : "";
        %>

        <div class="d-flex flex-row flex-wrap gap-2 mb-3 <%= displayClass %>">
            <%
                if (artistsWithAlbums != null) {
                    for (Object[] prop : artistsWithAlbums) {
            %>
            <div class="card card-form" id="artist-<%= prop[0] %>">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <h5 class="card-title"><%=prop[1]%>
                        </h5>
                        <div class="dropdown">
                            <button class="btn more-options" type="button" id="dropdownMenuButton<%= prop[0]%>"
                                    aria-expanded="false <%=prop[0]%>" data-bs-toggle="dropdown"
                                    aria-haspopup="true">
                                <img src="https://img.icons8.com/?size=100&id=85460&format=png&color=000000"
                                     alt="More options">
                            </button>
                            <ul class="dropdown-menu dropdown-menu-right"
                                aria-labelledby="dropdownMenuButton<%= prop[0] %>">
                                <li><a class="dropdown-item"
                                       href="artists?action=showAlbums&artistId=<%= prop[0] %>">Показать альбомы</a></li>
                                <li><a class="dropdown-item" href="artists?action=edit&id=<%= prop[0] %>">
                                    Редактировать</a>
                                </li>
                                <li><a class="dropdown-item" onclick="deleteArtist(<%= prop[0] %>)">Удалить</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <p class="card-text">Количество альбомов: <%=prop[2]%>
                    </p>
                </div>
            </div>
            <%}
            }
            %>
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
