<%@ page import="java.util.List" %>
<%@ page import="ssau.nosql_1.model.Composition" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Compositions</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/js/bootstrap.bundle.min.js">
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.14.1/themes/base/jquery-ui.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<section class="container m-5">
    <%@ include file="header.jsp" %>
    <div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="compsFull.jsp">Full</a></li>
                <li class="breadcrumb-item"><a href="compCross.jsp">Cross</a></li>
            </ol>
        </nav>

        <% List<Object[]> nameAlbumAndSumDuration = (List<Object[]>) request.getAttribute("results");
            List<Composition> compositions = (List<Composition>) request.getAttribute("compositions");
            if (nameAlbumAndSumDuration != null) {
                if (compositions == null || compositions.isEmpty()) {
                    for (Object[] row : nameAlbumAndSumDuration) {
                        out.print("<div class=\"d-flex justify-content-between\"><div>" +
                                "<h2>" + row[0] + "</h2></div>" +
                                "<button class=\"btn btn-dark\" onclick=toggleForm(\"addCompositionFormContainer\")>" +
                                "<img src=\"https://img.icons8.com/ios-glyphs/30/ffffff/plus.png\" alt=\"Add composition\">Add composition" +
                                "</button></div></div>");
                    }
                } else {
                    for (Object[] row : nameAlbumAndSumDuration) {
                        out.print("<div class=\"d-flex justify-content-between\"><div>" +
                                "<h2>" + row[0] + "</h2>" +
                                "<p>Общая длительность: " + row[1] + "</p></div>" +
                                "<div>" +
                                "<button class=\"btn btn-dark\" onclick=toggleForm(\"addCompositionFormContainer\")>" +
                                "<img src=\"https://img.icons8.com/ios-glyphs/30/ffffff/plus.png\" alt=\"Add composition\">Add composition" +
                                "</button></div></div>");
                    }
                }
            } else {
                out.print("<h2>All compositions</h2>");
            }
        %>

        <div class="card mb-4" id="addCompositionFormContainer" style="display: none">
            <div class="card-header">Add composition</div>
            <div class="card-body">
                <form id="addArtistForm" autocomplete="off">
                    <div class="mb-3">
                        <div class="ui-widget-icon-block">
                            <label for="name" class="form-label">Name</label>
                            <input type="text" class="form-control" id="name" name="name" autocomplete="off" required>
                        </div>
                        <label for="duration" class="form-label">Duration</label>
                        <input type="time" step="1" class="form-control" id="duration" name="duration" required>
                    </div>
                    <button type="button" class="btn btn-dark"
                            onclick="createComposition(<%=request.getParameter("id")%>)">Add
                    </button>
                </form>
            </div>
        </div>

        <%
            String displayClass = "d-flex";
            if (compositions == null || compositions.isEmpty()) {
                displayClass = "d-none";
                out.println("</div>" +
                        "<h5 class=\"m-2\">This album haven't compositions</h5>" +
                        "<div class=\"d-flex flex-row flex-wrap gap-2 mb-3\">");
        %>

        <div class="<%= displayClass %>">
            <div class="p-2 fw-light">#</div>
            <div class="p-2 flex-grow-1 fw-light">Name</div>
            <div class="p-2 fw-light">Duration</div>
        </div>
        <%
            } else {
                for (Composition composition : compositions) {
        %>
        <div class="d-flex comp" id="composition-<%= composition.getId() %>">
            <div class="p-2 fw-light"><%=composition.getId()%>
            </div>
            <div class="p-2 flex-grow-1"><%=composition.getName()%>
            </div>
            <div class="p-2 duration"><%= composition.getDuration().toLocalTime()%>
            </div>
            <div class="p-2 options">
                <div class="dropdown">
                    <button class="btn btn-link more-options" type="button" data-bs-toggle="dropdown"
                            id="dropdownMenuButton<%=composition.getId()%>">
                        <img src="https://img.icons8.com/ios-glyphs/30/000000/more.png">
                    </button>
                    <ul class="dropdown-menu dropdown-menu-right"
                        aria-labelledby="dropdownMenuButton<%=composition.getId()%>">
                        <li><a class="dropdown-item"
                               href="compositions?album_id=<%=composition.getAlbum().getId()%>&action=edit&id=<%=composition.getId()%>">Редактировать</a>
                        </li>
                        <li><a class="dropdown-item" onclick="deleteComposition(<%=composition.getId()%>)">Удалить</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <%
                }
            }
        %>
    </div>
</section>
<script src="${pageContext.request.contextPath}/webjars/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script>
    function toggleForm(id_form) {
        const formContainer = document.getElementById(id_form);
        if (formContainer.style.display === "none") {
            formContainer.style.display = "block";
        } else {
            formContainer.style.display = "none";
        }
    }

    function deleteComposition(id) {
        if (confirm("Вы уверены, что хотите удалить эту композицию?")) {
            $.ajax({
                url: 'compositions?action=delete&id=' + id,
                type: 'GET',
                success: function () {
                    const element = document.getElementById("composition-" + id);
                    element.parentNode.removeChild(element);
                    location.reload();
                },
                error: function (xhr, status, error) {
                    console.error("Не удалось удалить композицию:" + error);
                }
            })
        }
    }

    $(document).ready(function () {
        $("#name").autocomplete({
            source: function (request, response) {
                $.ajax({
                    url: 'compositions?action=autocomplete',
                    type: 'GET',
                    data: {
                        name: request.term
                    },
                    success: function (data) {
                        response(data);
                    },
                    error: function (error) {
                        alert('Ошибка при получении данных для автодополнения: ' + error);
                    }
                });
            },
            minLength: 2,
            select: function (event, ui) {
                $("#name").val(ui.item.value);
            },
            appendTo: "#autocomplete-list"
        });
    });

    function createComposition(id) {
        const name = $("#name").val();
        const duration = $("#duration").val();
        if (name && duration) {
            $.ajax({
                url: 'compositions',
                type: 'POST',
                data: {
                    'id': id,
                    'action': 'save',
                    'name': name,
                    'duration': duration
                },
                success: function () {
                    location.reload();
                },
                error: function (xhr, status, error) {
                    alert("Не удалось создать композицию: " + error + ", " + status);
                }
            })
        }
    }
</script>
</body>
</html>