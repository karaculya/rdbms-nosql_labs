package ssau.nosql_1.dao;

import ssau.nosql_1.model.Album;

import java.util.List;

public interface AlbumDao {
    void createAlbum(int artistId, String name, String genre);
    List<Album> getAllAlbums();
    List<Album> getAllAlbumsByArtistId(int artistId);
    void deleteAlbum(int id);
    void updateAlbum(Integer id, String name, String genre);
    Album getAlbumById(Integer id);
    List<Object[]> getAlbumsWithCompositions();
    List<Object[]> getNameAlbumAndSumDuration(Integer id);
}