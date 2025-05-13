package ssau.nosql_1;

import ssau.nosql_1.dao.util.DaoFactory;
import ssau.nosql_1.model.Artist;

import java.math.BigInteger;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        List<Object[]> objects = DaoFactory.getCompositionDao().getCrossTable();
//        for (Object[] object : objects) {
//            System.out.println(object[0]);
//            System.out.println(object[1]);
//            System.out.println(object[2]);
//            System.out.println(object[3]);
//        }

//        BigInteger objects = DaoFactory.getCompositionDao().getRecursiveTable(3);
//        System.out.println(objects);
//
//        artistDao.createArtist("Bones");
//
//        AlbumDaoImpl albumDao = new AlbumDaoImpl();
//        albumDao.createAlbum(1, "Burden", "Rap");
//
//        CompositionDaoImpl compositionDao = new CompositionDaoImpl();
//        compositionDao.createComposition(1, "SafeAndSound", new Time(0,2,6));

//        System.out.println(compositionDao.getCompositionsNamesLike("SafeAndSound"));
//        List list = Collections.singletonList(albumDao.getNameAlbumAndSumDuration(1));
//        for (Object o : list) {
//            System.out.println(o.toString());
//        }

        System.out.println("АРТИСТЫ У КОТОРЫХ АЛЬБОМОВ БОЛЬШЕ 1 ШТ");
        List<Object[]> list = DaoFactory.getArtistDao().getGroupByHaving();
        for (Object[] o : list) {
            System.out.println(o[0] + " - " + o[1] + " - " + o[2]);
        }
    }
}