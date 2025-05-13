<%@ page import="java.util.List" %>
<%@ page import="ssau.nosql_1.model.Album" %>
<%@ page import="ssau.nosql_1.dao.util.DaoFactory" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Albums</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<section class="container m-5">
    <%@ include file="header.jsp" %>
    <div class="container">
        <%
            List<Object[]> numAlbumsAndComp = (List<Object[]>) request.getAttribute("numAlbumsAndComp");
            if (numAlbumsAndComp != null && !numAlbumsAndComp.isEmpty()) {
                for (Object[] row : numAlbumsAndComp) {
                    out.println("<h2>" + row[0] + "</h2>");
                    out.println("<p>Альбомов: " + row[1] +
                            "<br> Композиций: " + row[2] + "</p>");
                }
            } else {
                out.println("<nav aria-label=\"breadcrumb\">\n" +
                        "            <ol class=\"breadcrumb\">\n" +
                        "                <li class=\"breadcrumb-item active\"><a href=\"albums.jsp\">All albums</a></li>\n" +
                        "                <li class=\"breadcrumb-item\" aria-current=\"page\">\n" +
                        "                    <a href=\"albumsWithCompositions.jsp\">Albums with compositions</a></li>\n" +
                        "            </ol>\n" +
                        "        </nav>");
            }
        %>
        <!-- Форма добавления альбома, скрытая по умолчанию -->
        <div class="card mb-4" id="addAlbumFormContainer" style="display: none;">
            <div class="card-header">Add album</div>
            <div class="card-body">
                <form id="addArtistForm" action="albums" method="POST">
                    <div class="mb-3">
                        <label for="name" class="form-label">Album name</label>
                        <input type="text" class="form-control" id="name" name="name" required>
                        <label for="genre" class="form-label">Album genre</label>
                        <input type="text" class="form-control" id="genre" name="genre" required>
                    </div>
                    <button type="button" class="btn btn-dark"
                            onclick="createAlbum(<%=request.getParameter("id")%>)">Add
                    </button>
                </form>
            </div>
        </div>

        <div class="d-flex flex-row flex-wrap gap-2 mb-3">
            <%
                String displayClass = numAlbumsAndComp == null ? "d-none" : "";
            %>
            <div class="card add-btn d-flex justify-content-center align-items-center <%= displayClass %>"
                 onclick="toggleForm('addAlbumFormContainer')">
                <img src="https://img.icons8.com/ios-glyphs/30/ffffff/plus.png" alt="Add artist">
                <p>Add album</p>
            </div>
            <%
                List<Album> albums = (List<Album>) request.getAttribute("albums");
                if (numAlbumsAndComp != null && (albums == null || albums.isEmpty())) {
                    out.println("<h5>This artist haven't albums, look at another albums</h5>");
                } else {
                    albums = DaoFactory.getAlbumDao().getAllAlbums();
                }
                if (albums != null && !albums.isEmpty()) {
                    for (Album album : albums) {
            %>
            <div class="card card-form" id="album-<%= album.getId() %>">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <h5 class="card-title"><%= album.getName() %></h5>
                        <div class="dropdown">
                            <button class="btn more-options" type="button"
                                    id="dropdownMenuButton<%=album.getId()  %>"
                                    data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <img src="https://img.icons8.com/ios-glyphs/30/000000/more.png" alt="More options">
                            </button>
                            <ul class="dropdown-menu dropdown-menu-right"
                                aria-labelledby="dropdownMenuButton<%=album.getId() %>">
                                <li><a class="dropdown-item" id="showComp"
                                       href="albums?artistId=<%=album.getArtist().getId()%>&action=showCompositions&id=<%=album.getId()%>">Показать композиции</a>
                                </li>
                                <li><a class="dropdown-item"
                                       href="albums?artistId=<%=album.getArtist().getId()%>&action=edit&id=<%=album.getId()%>">Редактировать</a>
                                </li>
                                <li><a class="dropdown-item" onclick="deleteAlbum(<%=album.getId()%>)">Удалить</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <p class="card-text">
                        Genre: <%=album.getGenre()%>
                    </p>
                </div>
            </div>
            <%
                    }
                }
            %>
        </div>
    </div>
</section>
<script>
    function toggleForm(id_form) {
        const formContainer = document.getElementById(id_form);
        if (formContainer.style.display === "none") {
            formContainer.style.display = "block";
        } else {
            formContainer.style.display = "none";
        }
    }

    function deleteAlbum(id) {
        if (confirm("Вы уверены, что хотите удалить этот альбом?")) {
            $.ajax({
                url: 'albums?action=delete&id=' + id,
                type: 'GET',
                success: function () {
                    const element = document.getElementById("album-" + id);
                    element.parentNode.removeChild(element);
                    location.reload();
                },
                error: function (xhr, status, error) {
                    console.error("Не удалось удалить альбом:", error);
                }
            })
        }
    }

    function createAlbum(artistId) {
        const genre = $("#genre").val();
        const name = $("#name").val();
        if (name && genre) {
            $.ajax({
                url: "albums",
                type: "POST",
                data: {
                    'id': artistId,
                    'action': 'save',
                    'name': name,
                    'genre': genre
                },
                success: function () {
                    location.reload();
                },
                error: function (xhr, status, error) {
                    alert("Не удалось создать альбом:" + error);
                }
            })
        }
    }

</script>

<script src="${pageContext.request.contextPath}/webjars/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>