package ssau.labs.db;

import ru.yandex.clickhouse.ClickHouseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MutationChecker {
    private final ClickHouseConnector connector;
    private final String tableName;

    public MutationChecker(ClickHouseConnector connector, String tableName) {
        this.connector = connector;
        this.tableName = tableName;
    }

    public boolean mutationIsDone() throws SQLException {
        String mutationId = getLastMutationId();
        return waitForMutation(mutationId);
    }

    private boolean waitForMutation(String mutationId) throws SQLException {
        String query = "SELECT is_done FROM system.mutations WHERE table = ? AND mutation_id = ?";

        while (!Thread.currentThread().isInterrupted()) {
            try (ClickHouseConnection connection = connector.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, tableName);
                statement.setString(2, mutationId);
                ResultSet rs = statement.executeQuery();

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
        }
        throw new SQLException("Mutation check interrupted");
    }

    private String getLastMutationId() throws SQLException {
        String query = "SELECT mutation_id FROM system.mutations WHERE table = ? " +
                "ORDER BY create_time DESC LIMIT 1";

        try (ClickHouseConnection connection = connector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tableName);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getString("mutation_id");
            }
            return null;
        }
    }
}
