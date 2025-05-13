package main.java.ssau.nosql_1.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import ssau.nosql_1.dao.ArtistDao;
import ssau.nosql_1.dao.util.HibernateUtil;
import ssau.nosql_1.model.Artist;

import java.util.List;

public class ArtistDaoImpl implements ArtistDao {
    @Override
    public void createArtist(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (!name.isEmpty()) {
                session.beginTransaction();
                Artist artist = new Artist(name);
                session.persist(artist);
            }
        }
    }

    @Override
    public Artist getArtistById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            return session.get(Artist.class, id);
        }
    }

    @Override
    public List<Artist> getAllArtists() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Artist", Artist.class).list();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteArtist(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.delete(session.get(Artist.class, id));
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateArtist(Integer id, String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Artist artist = session.get(Artist.class, id);
            if (artist != null) {
                if (!artist.getName().equals(name)) artist.setName(name);
                session.update(artist);
                session.getTransaction().commit();
            }
        }
    }

    // todo LEFT (RIGHT, FULL OUTER, INNER, CROSS)
    @Override
    public List<Object[]> getArtistAlbumAndCompositionCountById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "SELECT art.name, COUNT(DISTINCT al.id) AS Number_Of_Albums, " +
                        "COUNT(DISTINCT comp.id) AS Number_Of_Compositions " +
                        "FROM Artist art " +
                        "LEFT JOIN Album al ON art.id = al.artist.id " +
                        "LEFT JOIN Composition comp ON comp.album.id = al.id " +
                        "WHERE art.id=:id GROUP BY art.name, al.id", Object[].class)
                    .setParameter("id", id)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // todo Используют группировку (GROUP BY, HAVING)
    @Override
    public List<Object[]> getArtistWithAlbums() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Object[]> artistsWithAlbums = session.createQuery(
            """
                    SELECT art.id, art.name, COUNT(al.id)
                    FROM Artist art
                    JOIN Album al ON art.id = al.artist.id
                    GROUP BY art.name, art.id""", Object[].class)
                .list();
        session.close();
        return artistsWithAlbums;
    }

    @Override
    public List<Object[]> getGroupByHaving() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Object[]> artists = session.createQuery(
                        """
                                SELECT art.id, art.name, COUNT(al.id)
                                FROM Artist art
                                JOIN Album al ON art.id = al.artist.id
                                GROUP BY art.name, art.id
                                HAVING COUNT(al.id) > 1""", Object[].class)
                .list();
        session.close();
        return artists;
    }
}
