package ssau.labs.db;

import ru.yandex.clickhouse.ClickHouseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MutationChecker {
    private final ClickHouseConnector connector;
    private String tableName;

    public MutationChecker(ClickHouseConnector connector, String tableName) {
        this.connector = connector;
        this.tableName = tableName;
    }

    public boolean waitForMutation(String mutationId) throws SQLException {
        String query = "SELECT is_done FROM system.mutations WHERE table = '" + tableName + "'";
        if (mutationId != null) {
            query += " AND mutation_id = '" + mutationId + "'";
        }

        try (ClickHouseConnection connection = connector.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            if (!rs.next()) {
                return true;
            }
            do {
                if (rs.getInt("is_done") == 1) {
                    return true;
                }
            } while (rs.next());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Mutation check interrupted", e);
        }

        return false;
    }

    public String getLastMutationId() throws SQLException {
        String query = "SELECT mutation_id FROM system.mutations WHERE table = '"
                + tableName + "' " +
                "ORDER BY create_time DESC LIMIT 1";

        try (ClickHouseConnection connection = connector.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                return rs.getString("mutation_id");
            }
            return null;
        }
    }
}
