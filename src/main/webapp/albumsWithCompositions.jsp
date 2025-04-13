<%@ page import="java.util.List" %>
<%@ page import="ssau.nosql_1.dao.util.DaoFactory" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Albums with compositions</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<section class="container m-5">
    <%@ include file="header.jsp" %>
    <div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="albums.jsp">All albums</a></li>
                <li class="breadcrumb-item active" aria-current="page">
                    <a href="albumsWithCompositions.jsp">Albums with compositions</a></li>
            </ol>
        </nav>

        <%
            List<Object[]> albums = DaoFactory.getAlbumDao().getAlbumsWithCompositions();
            if (albums == null || albums.isEmpty()) {
                out.println("<h5>No album contains any songs</h5>");
            } else {
                for (Object[] album : albums) {
        %>
        <div class="card card-form" id="album-<%= album[0] %>">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <h5 class="card-title">
                        <%= album[1] %>
                    </h5>
                    <div class="dropdown">
                        <button class="btn more-options" type="button"
                                id="dropdownMenuButton<%=album[0]  %>"
                                data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <img src="https://img.icons8.com/ios-glyphs/30/000000/more.png" alt="More options">
                        </button>
                        <ul class="dropdown-menu dropdown-menu-right"
                            aria-labelledby="dropdownMenuButton<%=album[0] %>">
                            <li><a class="dropdown-item" id="showComp"
                                   href="albums?artistId=<%=album[4]%>&action=showCompositions&id=<%=album[0]%>">Показать
                                композиции</a>
                            </li>
                            <li><a class="dropdown-item"
                                   href="albums?artistId=<%=album[4]%>&action=edit&id=<%=album[0]%>">Редактировать</a>
                            </li>
                            <li><a class="dropdown-item" onclick="deleteAlbum(<%=album[0]%>)">Удалить</a>
                            </li>
                        </ul>
                    </div>
                </div>
                <p class="card-text">
                    Genre: <%=album[2]%><br>
                    Count compositions: <%=album[3]%>
                </p>
            </div>
        </div>
        <%
                }
            }
        %>
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
