package ssau.labs.repo;

import ssau.labs.db.ClickHouseConnector;
import ssau.labs.db.MutationChecker;
import ssau.labs.model.Album;
import ssau.labs.model.Artist;

import java.sql.ResultSet;
import java.sql.SQLException;
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

        try {
            ClickHouseConnector.execute(connector.getConnection(), query);
        } catch (SQLException e) {
            System.out.println("Failed to creating TABLE albums");
            e.printStackTrace();
        }
    }

    @Override
    public void save(Album album) {
        String query = "INSERT INTO albums (id, name, genre, artist_id) VALUES ('"
                + album.getId() + "', '"
                + album.getName() + "', '"
                + album.getGenre() + "', '"
                + album.getArtist().getId() + "')";
        try {
            boolean isDone = false;
            while (!isDone) {
                isDone = ClickHouseConnector.mutationIsDone(connector.getConnection(), checker, query);
            }
        } catch (SQLException e) {
            System.out.println("Failed to creating album");
            e.printStackTrace();
        }
    }

    @Override
    public Album findById(Integer id) {
        String query = "SELECT * FROM albums WHERE id = '" + id + "'";

        try {
            ResultSet set = ClickHouseConnector.executeQuery(connector.getConnection(), query);
            if (set != null) {
                while (set.next()) {
                    Album album = new Album();
                    album.setId(set.getInt("id"));
                    album.setName(set.getString("name"));
                    album.setGenre(set.getString("genre"));
                    Integer artistId = set.getInt("artist_id");
                    Artist artist = artistRepository.findById(artistId);
                    if (artist.getId().equals(artistId)) {
                        album.setArtist(artist);
                    }
                    return album;
                }
            }
            int alId = set.getInt("id");
            String name = set.getString("name");
            String genre = set.getString("genre");
            int arId = set.getInt("artist_id");
            return new Album(alId, name, genre, artistRepository.findById(arId));
        } catch (SQLException e) {
            System.out.println("Failed to findById album");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Album album) {
        String query = "ALTER TABLE albums UPDATE name='" + album.getName() + "'," +
                " genre = '" + album.getGenre() + "', " +
                "artist_id = '" + album.getArtist().getId() +
                "' WHERE id='" + album.getId() + "'";

        try {
            boolean isDone = false;
            while (!isDone) {
                isDone = ClickHouseConnector.mutationIsDone(connector.getConnection(), checker, query);
            }
        } catch (SQLException e) {
            System.out.println("Failed to updating album");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "ALTER TABLE albums DELETE WHERE id='" + id + "'";

        try {
            boolean isDone = false;
            while (!isDone) {
                isDone = ClickHouseConnector.mutationIsDone(connector.getConnection(), checker, query);
            }
        } catch (SQLException e) {
            System.out.println("Failed to deleting album");
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet findAll() {
        try {
            ResultSet resultSet = ClickHouseConnector.executeQuery(connector.getConnection(), "SELECT * FROM albums");
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
            Album album = new Album();
            album.setId(resultSet.getInt("id"));
            album.setName(resultSet.getString("name"));
            album.setGenre(resultSet.getString("genre"));
            Integer artistId = resultSet.getInt("artist_id");
            Artist artist = artistRepository.findById(artistId);
            if (artist.getId().equals(artistId)) {
                album.setArtist(artist);
            }
            System.out.println(album.getId() + " | " +
                    album.getName() + " | " +
                    album.getGenre() + " | " +
                    album.getArtist().getName());
            albums.add(album);
        }
        return albums;
    }
}
