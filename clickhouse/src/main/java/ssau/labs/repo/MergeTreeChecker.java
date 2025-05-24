package ssau.labs.repo;

import ssau.labs.db.ClickHouseConnector;
import ssau.labs.db.MutationChecker;
import ssau.labs.model.Artist;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface MergeTreeChecker<T> {
    void insert(Artist artist, int field);
    void select();
    static void execute(Artist artist, int sign, String query, ClickHouseConnector connector, MutationChecker checker) {
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, artist.getId());
            statement.setString(2, artist.getName());
            statement.setInt(3, sign);
            statement.executeUpdate();

            boolean isDone = false;
            while (!isDone) {
                isDone = checker.mutationIsDone();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
