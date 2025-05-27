package ssau.labs;

import ssau.labs.model.Album;
import ssau.labs.model.Artist;
import ssau.labs.model.Composition;
import ssau.labs.utils.CassandraUtil;
import ssau.labs.utils.MusicOperations;

import java.io.IOException;
import java.time.LocalDate;


public class App {
    public static void main(String[] args) throws IOException {
        MusicOperations musicLibrary = new MusicOperations();
        Artist artist = new Artist(1, "The Beatles", "UK", LocalDate.of(1960, 1, 1));
        Album album = new Album(1, "Abbey Road", "Rock", artist, 1969);
        Composition composition = new Composition(1, "Come Together",
                260, album, 1);

        CassandraUtil.init();

        try {
            musicLibrary.createArtistTable();
            musicLibrary.createAlbumTable();
            musicLibrary.createCompositionTable();

            System.out.println("\n=== CRUD операции с артистами ===");
            musicLibrary.saveArtist(artist);
            musicLibrary.findArtistById(1);
            System.out.println("\nВсе артисты:");
            musicLibrary.printAllArtists();

            artist.setName("The Beatles Updated");
            artist.setCountry("United Kingdom");
            artist.setActiveSince(LocalDate.of(1960, 1, 1));
            musicLibrary.updateArtist(artist);
            musicLibrary.findArtistById(1);

            System.out.println("\n=== CRUD операции с альбомами ===");
            musicLibrary.saveAlbum(album);
            musicLibrary.findAlbumById(1, 1);
            System.out.println("\nВсе альбомы:");
            musicLibrary.printAllAlbums();

            album.setName("Abbey Road Remastered");
            album.setGenre("Classic Rock");
            album.setReleaseYear(1969);
            musicLibrary.updateAlbum(album);
            musicLibrary.findAlbumById(1, 1);

            System.out.println("\n=== CRUD операции с композициями ===");
            musicLibrary.saveComposition(composition);
            musicLibrary.findCompositionById(1);
            System.out.println("\nВсе композиции:");
            musicLibrary.printAllCompositions();

            composition.setName("Come Together (Remastered)");
            composition.setDurationSec(265);
            musicLibrary.updateComposition(composition);
            musicLibrary.findCompositionById(1);

//            musicLibrary.deleteComposition(1);
//            musicLibrary.deleteAlbum(1);
//            musicLibrary.deleteArtist(1);

            // ФИЛЬТРАЦИЯ
            System.out.println("\n=== Запросы с фильтрацией ===");

            Artist artist2 = new Artist(2, "Pink Floyd", "UK", LocalDate.of(1965, 1, 1));
            Album album2 = new Album(2, "The Dark Side of the Moon", "Rock", artist2, 1973);
            musicLibrary.saveArtist(artist2);
            musicLibrary.saveAlbum(album2);

            Composition composition2 = new Composition(1, "Time", 413, album2, 1);
            musicLibrary.saveComposition(composition2);

            musicLibrary.getArtistsByCountry("UK");

            musicLibrary.getAlbumsByGenre("Rock");

            musicLibrary.getCompositionsByDuration(240);

        } finally {
            CassandraUtil.closeSession();
        }
    }
}