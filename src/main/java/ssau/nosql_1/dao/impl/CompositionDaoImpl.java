package ssau.nosql_1.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import ssau.nosql_1.dao.CompositionDao;
import ssau.nosql_1.dao.util.HibernateUtil;
import ssau.nosql_1.model.Album;
import ssau.nosql_1.model.Composition;

import java.math.BigInteger;
import java.sql.Time;
import java.util.List;

public class CompositionDaoImpl implements CompositionDao {
    @Override
    public void createComposition(int albumId, String name, Time duration) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (!name.isEmpty()) {
                session.beginTransaction();
                Album album = session.get(Album.class, albumId);
                Composition composition = new Composition(album, duration, name);
                session.persist(composition);
            }
        }
    }

    @Override
    public List<Composition> getAllCompositions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Composition", Composition.class).list();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Composition> getAllCompositionsByAlbumId(int albumId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from Composition c WHERE c.album.id = :albumId", Composition.class)
                    .setParameter("albumId", albumId)
                    .list();
        } catch (HibernateException e) {
            System.out.println("Compositions not found");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteComposition(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.delete(session.get(Composition.class, id));
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateComposition(Integer id, String name, Time duration) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Composition composition = session.get(Composition.class, id);
            if (composition != null) {
                if (!composition.getName().equals(name)) composition.setName(name);
                if (!composition.getDuration().equals(duration)) composition.setDuration(duration);
                session.update(composition);
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public Composition getCompositionById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            return session.get(Composition.class, id);
        }
    }

    @Override
    public List<String> getCompositionsNamesLike(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<String> compNames = session.
                    createQuery("SELECT c.name FROM Composition c WHERE LOWER(c.name) " +
                            "LIKE LOWER(:name)", String.class)
                    .setParameter("name", "%" + name + "%").list();
            return compNames;
        }
    }

    @Override
    public List<Object[]> getFullTable() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                        SELECT com.id, com.name, com.duration, al.name
                        FROM Composition com
                        FULL JOIN Album al
                        ON com.album.id = al.id""", Object[].class)
                    .list();
        }
    }

    @Override
    public List<Object[]> getCrossTable() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createNativeQuery(
                    "SELECT com.id, com.name AS composition_name, com.duration, " +
                            "al.name AS album_name " +
                            "FROM compositions com " +
                            "CROSS JOIN albums al")
                    .getResultList();
            }
    }

    @Override
    public BigInteger getRecursiveTable(int n) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (BigInteger) session.createNativeQuery(
                            "WITH RECURSIVE t(n) AS ( " +
                                    "SELECT CAST(1 AS INTEGER) " +
                                    "UNION ALL " +
                                    "SELECT n + 1 FROM t WHERE n < CAST(:n AS INTEGER) " +
                                    ") " +
                                    "SELECT SUM(n) FROM t")
                    .setParameter("n", n)
                    .getSingleResult();
        }
    }
}