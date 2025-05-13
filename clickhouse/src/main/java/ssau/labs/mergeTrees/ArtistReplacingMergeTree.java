package ssau.labs.mergeTrees;

import ssau.labs.db.ClickHouseConnector;
import ssau.labs.db.MutationChecker;
import ssau.labs.model.Artist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ArtistReplacingMergeTree {
    private final ClickHouseConnector connector;
    private final MutationChecker checker;

    public ArtistReplacingMergeTree(ClickHouseConnector connector) {
        this.connector = connector;
        this.checker = new MutationChecker(this.connector, "artistsReplacingMT");
        create();
    }

    private void create() {
        String query = "CREATE OR REPLACE TABLE artistsReplacingMT("
                + "id   UInt32, "
                + "name String, "
                + "last_tour UInt32 "
                + ") ENGINE = ReplacingMergeTree(last_tour) "
                + "ORDER BY id;";

        try (Statement statement = connector.getConnection().createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void insert(Artist artist, int lastTour) {
        String query = "INSERT INTO artistsReplacingMT (id, name, last_tour) VALUES (" +
                "'" + artist.getId() + "'," +
                "'" + artist.getName() + "'," +
                "'" + lastTour + "');";
        try {
            boolean isDone = false;
            while (!isDone) {
                isDone = ClickHouseConnector.mutationIsDone(connector.getConnection(), checker, query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectWithLimitBy() {
        String query = "SELECT * FROM artistsReplacingMT ORDER BY id LIMIT 1 by id;";

        try (Statement statement = connector.getConnection().createStatement()) {
            ResultSet set = statement.executeQuery(query);
            if (set != null) {
                StringBuffer buffer = new StringBuffer("id | name | last_tour\n");
                while (set.next()) {
                    buffer.append(set.getInt("id")).append(" | ");
                    buffer.append(set.getString("name")).append(" | ");
                    buffer.append(set.getInt("last_tour")).append("\n");
                }
                System.out.println(buffer);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
