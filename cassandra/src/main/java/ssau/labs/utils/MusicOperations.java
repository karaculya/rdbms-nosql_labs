package ssau.labs.utils;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import ssau.labs.model.Album;
import ssau.labs.model.Artist;
import ssau.labs.model.Composition;

import java.io.IOException;

public class MusicOperations {
    //  ARTISTS
    public void createArtistTable() throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "CREATE TABLE IF NOT EXISTS artists ("
                + "id INT PRIMARY KEY, "
                + "name TEXT, "
                + "country TEXT, "
                + "active_since DATE);";
        session.execute(query);
        System.out.println("Таблица 'artists' создана!");
    }

    public void saveArtist(Artist artist) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "INSERT INTO artists " +
                "(id, name, country, active_since) VALUES (?, ?, ?, ?);";
        session.execute(session.prepare(query).bind(
                artist.getId(), artist.getName(), artist.getCountry(), artist.getActiveSince()));
        System.out.println("Артист сохранён!");
    }

    public void findArtistById(int id) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "SELECT * FROM artists WHERE id = ?;";
        ResultSet resultSet = session.execute(session.prepare(query).bind(id));

        printArtistResultSet(id, resultSet);
    }

    public void updateArtist(Artist artist) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "UPDATE artists SET name = ?, " +
                "country = ? WHERE id = ? AND active_since = ?;";
        session.execute(session.prepare(query).bind(
                artist.getName(), artist.getCountry(), artist.getId(), artist.getActiveSince()));
        System.out.println("Артист обновлён!");
    }

    public void deleteArtist(int id) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "DELETE FROM artists WHERE id = ?;";
        session.execute(session.prepare(query).bind(id));
        System.out.println("Артист c id " + id + " удалён!");
    }

    public void printAllArtists() throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "SELECT * FROM artists;";
        ResultSet resultSet = session.execute(query);

        for (Row row : resultSet) {
            System.out.println("ID: " + row.getInt("id") +
                    ", Имя: " + row.getString("name") +
                    ", Страна: " + row.getString("country") +
                    ", Активен с: " + row.getLocalDate("active_since"));
        }
    }

    //    ALBUMS
    public void createAlbumTable() throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "CREATE TABLE IF NOT EXISTS albums ("
                + "id INT PRIMARY KEY, "
                + "name TEXT, "
                + "genre TEXT, "
                + "artist_id INT, "
                + "release_year INT);";
        session.execute(query);
        System.out.println("Таблица 'albums' создана!");
    }

    public void saveAlbum(Album album) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "INSERT INTO albums (id, name, genre, artist_id, release_year) VALUES (?, ?, ?, ?, ?);";
        session.execute(session.prepare(query).bind(
                album.getId(),
                album.getName(),
                album.getGenre(),
                album.getArtist().getId(),
                album.getReleaseYear()));
        System.out.println("Альбом сохранён!");
    }

    public void findAlbumById(int artistId, int albumId) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "SELECT * FROM albums WHERE artist_id = ? AND id = ? ALLOW FILTERING;";
        ResultSet resultSet = session.execute(session.prepare(query).bind(artistId, albumId));

        printAlbumResultSet(artistId, albumId, resultSet);
    }

    public void updateAlbum(Album album) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "UPDATE albums SET name = ?, genre = ?, "
                + "release_year = ? WHERE id = ?;";
        session.execute(session.prepare(query).bind(
                album.getName(),
                album.getGenre(),
                album.getReleaseYear(),
                album.getId()));
        System.out.println("Альбом обновлён!");
    }

    public void deleteAlbum(int albumId) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "DELETE FROM albums WHERE id = ?;";
        session.execute(session.prepare(query).bind(albumId));
        System.out.println("Альбом c id " + albumId + " удалён!");
    }

    public void printAllAlbums() throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "SELECT * FROM albums;";
        ResultSet resultSet = session.execute(query);

        for (Row row : resultSet) {
            System.out.println("ID: " + row.getInt("id") +
                    ", Название: " + row.getString("name") +
                    ", Жанр: " + row.getString("genre") +
                    ", ID артиста: " + row.getInt("artist_id") +
                    ", Год выпуска: " + row.getInt("release_year"));
        }
    }

    //    COMPOSITIONS
    public void createCompositionTable() throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "CREATE TABLE IF NOT EXISTS compositions ("
                + "id INT PRIMARY KEY, "
                + "name TEXT, "
                + "duration INT, "
                + "album_id INT, "
                + "track_number INT);";
        session.execute(query);
        System.out.println("Таблица 'compositions' создана!");
    }

    public void saveComposition(Composition composition) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "INSERT INTO compositions (id, name, duration, album_id, track_number) VALUES (?, ?, ?, ?, ?);";
        session.execute(session.prepare(query).bind(
                composition.getId(),
                composition.getName(),
                composition.getDurationSec(),
                composition.getAlbum().getId(),
                composition.getTrackNumber()));
        System.out.println("Композиция сохранена!");
    }

    public void findCompositionById(int compositionId) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "SELECT * FROM compositions WHERE id = ?;";
        ResultSet resultSet = session.execute(session.prepare(query).bind(compositionId));

        printCompositionResultSet(compositionId, resultSet);
    }

    public void updateComposition(Composition composition) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "UPDATE compositions SET name = ?, duration = ?, " +
                "track_number = ?, album_id = ? WHERE id = ?;";
        session.execute(session.prepare(query).bind(
                composition.getName(),
                composition.getDurationSec(),
                composition.getAlbum().getId(),
                composition.getId(),
                composition.getTrackNumber()));
        System.out.println("Композиция обновлена!");
    }

    public void deleteComposition(int compositionId) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "DELETE FROM compositions WHERE id = ?;";
        session.execute(session.prepare(query).bind(compositionId));
        System.out.println("Композиция c id " + compositionId + " удалена!");
    }

    public void printAllCompositions() throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "SELECT * FROM compositions;";
        ResultSet resultSet = session.execute(query);

        for (Row row : resultSet) {
            System.out.println("ID: " + row.getInt("id") +
                    ", Название: " + row.getString("name") +
                    ", Длительность: " + row.getInt("duration") +
                    ", ID альбома: " + row.getInt("album_id") +
                    ", Номер трека: " + row.getInt("track_number"));
        }
    }

    private void printArtistResultSet(int id, ResultSet resultSet) {
        System.out.println("Артист с ID " + id + ":");
        boolean found = false;
        for (Row row : resultSet) {
            found = true;
            System.out.println("ID: " + row.getInt("id"));
            System.out.println("Имя: " + row.getString("name"));
            System.out.println("Страна: " + row.getString("country"));
            System.out.println("Активен с: " + row.getLocalDate("active_since"));
            System.out.println("-----");
        }
        if (!found) {
            System.out.println("Артист не найден.");
        }
    }

    private void printAlbumResultSet(int artistId, int albumId, ResultSet resultSet) {
        System.out.println("Альбом с ID " + albumId + " артиста " + artistId + ":");
        boolean found = false;
        for (Row row : resultSet) {
            found = true;
            System.out.println("ID: " + row.getInt("id"));
            System.out.println("Название: " + row.getString("name"));
            System.out.println("Жанр: " + row.getString("genre"));
            System.out.println("Год выпуска: " + row.getInt("release_year"));
            System.out.println("-----");
        }
        if (!found) {
            System.out.println("Альбом не найден.");
        }
    }

    private void printCompositionResultSet(int compositionId, ResultSet resultSet) {
        System.out.println("Композиция с ID " + compositionId + ":");
        boolean found = false;
        for (Row row : resultSet) {
            found = true;
            System.out.println("ID: " + row.getInt("id"));
            System.out.println("Название: " + row.getString("name"));
            System.out.println("Длительность: " + row.getInt("duration"));
            System.out.println("Номер трека: " + row.getInt("track_number"));
            System.out.println("-----");
        }
        if (!found) {
            System.out.println("Композиция не найдена.");
        }
    }

    public void getArtistsByCountry(String country) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "SELECT * FROM artists WHERE country = ? ALLOW FILTERING;";
        ResultSet resultSet = session.execute(session.prepare(query).bind(country));

        System.out.println("Артисты из страны " + country + ":");
        for (Row row : resultSet) {
            System.out.println("ID: " + row.getInt("id") +
                    ", Имя: " + row.getString("name") +
                    ", Активен с: " + row.getLocalDate("active_since"));
        }
    }

    public void getAlbumsByGenre(String genre) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "SELECT * FROM albums WHERE genre = ? ALLOW FILTERING;";
        ResultSet resultSet = session.execute(session.prepare(query).bind(genre));

        System.out.println("Альбомы жанра " + genre + ":");
        for (Row row : resultSet) {
            System.out.println("ID: " + row.getInt("id") +
                    ", Название: " + row.getString("name") +
                    ", Артист ID: " + row.getInt("artist_id") +
                    ", Год выпуска: " + row.getInt("release_year"));
        }
    }

    public void getCompositionsByDuration(int minDuration) throws IOException {
        CqlSession session = CassandraUtil.getSession();
        String query = "SELECT * FROM compositions WHERE duration >= ? ALLOW FILTERING;";
        ResultSet resultSet = session.execute(session.prepare(query).bind(minDuration));

        System.out.println("Композиции длительностью от " + minDuration + "секунд:");
        for (Row row : resultSet) {
            System.out.println("ID: " + row.getInt("id") +
                    ", Название: " + row.getString("name") +
                    ", Длительность: " + row.getInt("duration") +
                    ", Альбом ID: " + row.getInt("album_id"));
        }
    }
}
