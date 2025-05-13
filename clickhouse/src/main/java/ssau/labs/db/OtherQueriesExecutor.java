package ssau.labs.db;

import ru.yandex.clickhouse.ClickHouseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OtherQueriesExecutor {
    private final ClickHouseConnector connector;

    public OtherQueriesExecutor(ClickHouseConnector connector) {
        this.connector = connector;
    }

    public void innerJoin() {
        String query = "SELECT a.name as artist_name, al.name as album_name " +
                "FROM albums al " +
                "INNER JOIN artists a ON a.id = al.artist_id";
        System.out.println("INNER JOIN:");
        executeQueryAndPrintResult(query);
    }

    public void leftJoin() {
        String query = "SELECT a.name as artist_name, al.name as album_name " +
                "FROM albums al " +
                "LEFT JOIN artists a ON al.artist_id = a.id";
        System.out.println("LEFT JOIN:");
        executeQueryAndPrintResult(query);
    }

    public void rightJoin() {
        String query = "SELECT a.name as artist_name, al.name as album_name " +
                "FROM albums al " +
                "RIGHT JOIN artists a ON al.artist_id = a.id";
        System.out.println("RIGHT JOIN:");
        executeQueryAndPrintResult(query);
    }

    public void fullJoin() {
        String query = "SELECT a.name as artist_name, al.name as album_name " +
                "FROM albums al " +
                "FULL OUTER JOIN artists a ON al.artist_id = a.id";
        System.out.println("FULL JOIN:");
        executeQueryAndPrintResult(query);
    }

    public void crossJoin() {
        String query = "SELECT a.name as artist_name, al.name as album_name " +
                "FROM albums al " +
                "CROSS JOIN artists a";
        System.out.println("CROSS JOIN:");
        executeQueryAndPrintResult(query);
    }

    public void groupByHaving() {
        String sql = "SELECT genre, COUNT(*) as album_count " +
                "FROM albums " +
                "GROUP BY genre " +
                "HAVING COUNT(*) > 1";

        System.out.println("genre | " + "album_count");

        try (ClickHouseConnection connection = connector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                String genre = rs.getString("genre");
                int albumCount = rs.getInt("album_count");
                System.out.println(genre + " | " + albumCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeQueryAndPrintResult(String query) {
        try {
            StringBuffer result = new StringBuffer("artist_name | album_name\n");
            ResultSet rs = ClickHouseConnector.executeQuery(connector.getConnection(), query);

            while (rs.next()) {
                result.append(rs.getString("artist_name") + " | ");
                result.append(rs.getString("album_name") + "\n");
            }

            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
