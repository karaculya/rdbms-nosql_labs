package ssau.labs.repo.mergeTrees;

import ssau.labs.db.ClickHouseConnector;
import ssau.labs.db.MutationChecker;
import ssau.labs.model.Artist;
import ssau.labs.repo.MergeTreeChecker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ArtistReplacingMergeTree implements MergeTreeChecker {
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
                + "version UInt32 "
                + ") ENGINE = ReplacingMergeTree(version) "
                + "ORDER BY id;" ;

        try (Statement statement = connector.getConnection().createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void insert(Artist artist, int version) {
        String query = "INSERT INTO artistsReplacingMT (id, name, version) VALUES (?,?,?)" ;
        MergeTreeChecker.execute(artist, version, query, connector, checker);
    }

    @Override
    public void select() {
        String query = "SELECT * FROM artistsReplacingMT ORDER BY version DESC LIMIT 1 by id;" ;

        try (Statement statement = connector.getConnection().createStatement()) {
            ResultSet set = statement.executeQuery(query);
            if (set != null) {
                StringBuffer buffer = new StringBuffer("id | name | version\n");
                while (set.next()) {
                    buffer.append(set.getInt("id")).append(" | ");
                    buffer.append(set.getString("name")).append(" | ");
                    buffer.append(set.getInt("version")).append("\n");
                }
                System.out.println(buffer);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
