package ssau.labs.repo.impl;

import ssau.labs.db.MutationChecker;
import ssau.labs.model.Artist;
import ssau.labs.db.ClickHouseConnector;
import ssau.labs.repo.CrudRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepository implements CrudRepository<Artist> {
    private final ClickHouseConnector connector;
    private final MutationChecker checker;

    public ArtistRepository(ClickHouseConnector connector) {
        this.connector = connector;
        this.checker = new MutationChecker(connector, "artists");
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String query = "CREATE OR REPLACE TABLE artists("
                + "id   UInt32, "
                + "name String "
                + ") ENGINE = MergeTree() "
                + "ORDER BY id;";
        try (Statement statement = connector.getConnection().createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println("Failed to creating TABLE artists");
            e.printStackTrace();
        }
    }

    @Override
    public void save(Artist artist) {
        String query = "INSERT INTO artists VALUES (?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, artist.getId());
            statement.setString(2, artist.getName());
            statement.executeUpdate();
            boolean isDone = false;
            while (!isDone) {
                isDone = checker.mutationIsDone();
            }
        } catch (SQLException e) {
            System.out.println("Failed to creating artist");
            e.printStackTrace();
        }
    }

    @Override
    public Artist findById(Integer id) {
        String query = "SELECT * FROM artists WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            if (set != null && set.next()) {
                Artist artist = new Artist();
                artist.setId(set.getInt("id"));
                artist.setName(set.getString("name"));
                return artist;
            }
        } catch (SQLException e) {
            System.out.println("Failed to finding artist with id " + id);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Artist artist) {
        String query = "ALTER TABLE artists UPDATE name = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, artist.getName());
            statement.setInt(2, artist.getId());
            statement.executeUpdate();
            boolean isDone = false;
            while (!isDone) {
                isDone = checker.mutationIsDone();
            }
        } catch (SQLException e) {
            System.out.println("Failed to updating artist, error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "ALTER TABLE artists DELETE WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            boolean isDone = false;
            while (!isDone) {
                isDone = checker.mutationIsDone();
            }
        } catch (SQLException e) {
            System.out.println("Failed to deleting artist with id " + id);
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet findAll() {
        String query = "SELECT * FROM artists";
        try (Statement statement = connector.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            printRow(resultSet);
            return resultSet;
        } catch (SQLException e) {
            System.out.println("Failed to getting all artists");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Artist> printRow(ResultSet resultSet) throws SQLException {
        List<Artist> artists = new ArrayList<>();

        System.out.println("id | name");

        while (resultSet.next()) {
            Artist artist = new Artist();
            artist.setId(resultSet.getInt("id"));
            artist.setName(resultSet.getString("name"));
            System.out.println(artist.getId() + " | " + artist.getName());
            artists.add(artist);
        }
        return artists;
    }
}
