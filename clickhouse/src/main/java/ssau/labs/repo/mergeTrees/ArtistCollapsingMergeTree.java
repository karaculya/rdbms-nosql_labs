package ssau.labs.repo.mergeTrees;

import ssau.labs.db.ClickHouseConnector;
import ssau.labs.db.MutationChecker;
import ssau.labs.model.Artist;
import ssau.labs.repo.MergeTreeChecker;

import java.sql.*;

public class ArtistCollapsingMergeTree implements MergeTreeChecker {
    private final ClickHouseConnector connector;
    private final MutationChecker checker;

    public ArtistCollapsingMergeTree(ClickHouseConnector connector) {
        this.connector = connector;
        this.checker = new MutationChecker(this.connector, "artistsCollapsingMT");
        create();
    }

    private void create() {
        String query = "CREATE OR REPLACE TABLE artistsCollapsingMT("
                + "id   UInt32, "
                + "name String, "
                + "Sign Int8 "
                + ") ENGINE = CollapsingMergeTree(Sign) "
                + "ORDER BY id;";

        try (Statement statement = connector.getConnection().createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void insert(Artist artist, int sign) {
        String query = "INSERT INTO artistsCollapsingMT (id, name, Sign) VALUES (?, ?, ?);";
        MergeTreeChecker.execute(artist, sign, query, connector, checker);
    }

    @Override
    public void select() {
        String query = "SELECT * FROM artistsCollapsingMT " +
                "WHERE id IN (SELECT id FROM artistsCollapsingMT GROUP BY id HAVING SUM(Sign) > 0)";

        try (Statement statement = connector.getConnection().createStatement()) {
            ResultSet set = statement.executeQuery(query);
            if (set != null) {
                StringBuffer buffer = new StringBuffer("id | name | Sign\n");
                while (set.next()) {
                    buffer.append(set.getInt("id")).append(" | ");
                    buffer.append(set.getString("name")).append(" | ");
                    buffer.append(set.getInt("Sign")).append("\n");
                }
                System.out.println(buffer);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
