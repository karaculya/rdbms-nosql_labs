package ssau.nosql_1.dao.util;

import ssau.nosql_1.dao.AlbumDao;
import ssau.nosql_1.dao.ArtistDao;
import ssau.nosql_1.dao.CompositionDao;
import ssau.nosql_1.dao.impl.AlbumDaoImpl;
import ssau.nosql_1.dao.impl.ArtistDaoImpl;
import ssau.nosql_1.dao.impl.CompositionDaoImpl;

public class DaoFactory {
    private static ArtistDao artistDao = null;
    private static AlbumDao albumDao = null;
    private static CompositionDao compositionDao = null;
    private static DaoFactory instance = null;

    public static synchronized DaoFactory getInstance() {
        if (instance == null) instance = new DaoFactory();
        return instance;
    }

    public static ArtistDao getArtistDao() {
        if (artistDao == null) artistDao = new ArtistDaoImpl();
        return artistDao;
    }

    public static AlbumDao getAlbumDao() {
        if (albumDao == null) albumDao = new AlbumDaoImpl();
        return albumDao;
    }

    public static CompositionDao getCompositionDao() {
        if (compositionDao == null) compositionDao = new CompositionDaoImpl();
        return compositionDao;
    }
}
