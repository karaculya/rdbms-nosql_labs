<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.io.*, java.math.BigInteger, ssau.nosql_1.dao.impl.CompositionDaoImpl" %>
<%@ page import="ssau.nosql_1.dao.util.DaoFactory" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Треугольное число</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<section class="container m-5">
    <%@ include file="header.jsp" %>
    <div class="row">
        <div class="d-flex flex-column justify-content-center align-items-center">
            <h2 class="text-center">Введите число для вычисления треугольного числа</h2>
            <form action="recursive.jsp" method="GET" class="d-flex flex-column gap-2">
                <label for="number">Введите число (n):</label>
                <input type="number" id="number" name="number" required>
                <button type="submit">Рассчитать</button>
            </form>
        </div>
    </div>

    <%
        String numberParam = request.getParameter("number");
        if (numberParam != null && !numberParam.isEmpty()) {
            try {
                int n = Integer.parseInt(numberParam);
                BigInteger triangularNumber = DaoFactory.getCompositionDao().getRecursiveTable(n);

                StringBuilder expression = new StringBuilder();
                for (int i = 1; i <= n; i++) {
                    expression.append(i);
                    if (i < n) {
                        expression.append(" + ");
                    }
                }
    %>
    <div class="text-center mt-3">
        <h3>Результат:</h3>
        <p><%= expression.toString() %> = <%= triangularNumber %>
        </p>
    </div>
    <%
            } catch (NumberFormatException e) {
                out.println("<p style='color: red;'>Ошибка: Введите корректное число!</p>");
            }
        }
    %>
</section>
</body>
</html>
