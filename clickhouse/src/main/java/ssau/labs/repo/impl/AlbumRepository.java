package ssau.labs.repo.impl;

import ssau.labs.db.ClickHouseConnector;
import ssau.labs.db.MutationChecker;
import ssau.labs.model.Album;
import ssau.labs.model.Artist;
import ssau.labs.repo.CrudRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AlbumRepository implements CrudRepository<Album> {
    private final ClickHouseConnector connector;
    private final ArtistRepository artistRepository;
    private final MutationChecker checker;

    public AlbumRepository(ClickHouseConnector connector, ArtistRepository artistRepository) {
        this.connector = connector;
        this.artistRepository = artistRepository;
        this.checker = new MutationChecker(connector, "albums");
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String query = "CREATE OR REPLACE TABLE albums (" +
                "id Int32, " +
                "name String, " +
                "genre String, " +
                "artist_id Int32" +
                ") ENGINE = MergeTree() " +
                "ORDER BY id";

        try (Statement statement = connector.getConnection().createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println("Failed to creating TABLE albums");
            e.printStackTrace();
        }
    }

    @Override
    public void save(Album album) {
        String query = "INSERT INTO albums (id, name, genre, artist_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, album.getId());
            statement.setString(2, album.getName());
            statement.setString(3, album.getGenre());
            statement.setInt(4, album.getArtist().getId());
            statement.executeUpdate();
            boolean isDone = false;
            while (!isDone) {
                isDone = checker.mutationIsDone();
            }
        } catch (SQLException e) {
            System.out.println("Failed to creating album");
            e.printStackTrace();
        }
    }

    @Override
    public Album findById(Integer id) {
        String query = "SELECT * FROM albums WHERE id = ?";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            if (set != null && set.next()) {
                return createAlbumByResultSet(set);
            }
        } catch (SQLException e) {
            System.out.println("Failed to findById album");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Album album) {
        String query = "ALTER TABLE albums UPDATE name= ?, genre = ?, artist_id = ? WHERE id= ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, album.getName());
            statement.setString(2, album.getGenre());
            statement.setInt(3, album.getArtist().getId());
            statement.setInt(4, album.getId());
            statement.executeUpdate();
            boolean isDone = false;
            while (!isDone) {
                isDone = checker.mutationIsDone();
            }
        } catch (SQLException e) {
            System.out.println("Failed to updating album");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "ALTER TABLE albums DELETE WHERE id = ?";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            boolean isDone = false;
            while (!isDone) {
                isDone = checker.mutationIsDone();
            }
        } catch (SQLException e) {
            System.out.println("Failed to deleting album");
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet findAll() {
        try (Statement statement = connector.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM albums");
            printRow(resultSet);
            return resultSet;
        } catch (SQLException e) {
            System.out.println("Failed to getting all artists");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Album> printRow(ResultSet resultSet) throws SQLException {
        List<Album> albums = new ArrayList<>();
        System.out.println("id | name | genre | artist_name");

        while (resultSet.next()) {
            Album album = createAlbumByResultSet(resultSet);
            System.out.println(album.getId() + " | " +
                    album.getName() + " | " +
                    album.getGenre() + " | " +
                    album.getArtist().getName());
            albums.add(album);
        }
        return albums;
    }

    private Album createAlbumByResultSet(ResultSet resultSet) throws SQLException {
        Album album = new Album();
        album.setId(resultSet.getInt("id"));
        album.setName(resultSet.getString("name"));
        album.setGenre(resultSet.getString("genre"));
        Integer artistId = resultSet.getInt("artist_id");
        Artist artist = artistRepository.findById(artistId);
        if (artist.getId().equals(artistId)) {
            album.setArtist(artist);
        }
        return album;
    }
}
