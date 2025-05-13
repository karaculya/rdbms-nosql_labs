package ssau.labs.mergeTrees;

import ssau.labs.db.ClickHouseConnector;
import ssau.labs.db.MutationChecker;
import ssau.labs.model.Artist;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ArtistCollapsingMergeTree {
    private final ClickHouseConnector connector;
    private final MutationChecker checker;

    public ArtistCollapsingMergeTree(ClickHouseConnector connector) {
        this.connector = connector;
        this.checker = new MutationChecker(this.connector, "");
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

    public void insert(Artist artist, int sign) {
        String query = "INSERT INTO artistsCollapsingMT (id, name, Sign) VALUES (" +
                "'" + artist.getId() + "'," +
                "'" + artist.getName() + "'," +
                "'" + sign + "');";
        try {
            boolean isDone = false;
            while (!isDone) {
                isDone = ClickHouseConnector.mutationIsDone(connector.getConnection(), checker, query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectWithGroupByHaving() {
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

    public static String signSelect(Connection connection) {
        String returned = "";
        String query = "select id,title,author,sign from PUBLISHERS.COL_BOOK where id in " +
                "(select id from PUBLISHERS.COL_BOOK GROUP BY id having sum(sign)>0)";
        //select id,title, author, version from PUBLISHERS.REP_BOOK WHERE ( version) IN (SELECT MAX(version) FROM PUBLISHERS.REP_BOOK GROUP BY id) LIMIT 1 by id

        try {
            ResultSet set = null;
            Statement state = connection.createStatement(1004, 1007);
            set = state.executeQuery(query);
            if (set.next()) {
                String buf = "";
                buf = buf + "id:" + set.getInt("id") + ";\t";
                buf = buf + "Название:" + set.getString("title") + ";\t";
                buf = buf + "Автор:" + set.getString("author") + ";\t";
                buf = buf + "Знак:" + set.getString("sign") + ";\n";
                returned += buf;

                while (set.next()) {
                    buf = "";
                    buf = buf + "id:" + set.getInt("id") + ";\t";
                    buf = buf + "Название:" + set.getString("title") + ";\t";
                    buf = buf + "Автор:" + set.getString("author") + ";\t";
                    buf = buf + "Знак:" + set.getString("sign") + ";\n";
                    returned += buf;
                }
            }
        } catch (Exception e) {
            returned = e.getMessage();
        }

        return returned;
    }
}
