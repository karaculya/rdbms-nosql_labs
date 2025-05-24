package ssau.labs;

import ssau.labs.db.ClickHouseConnector;
import ssau.labs.repo.mergeTrees.ArtistCollapsingMergeTree;
import ssau.labs.repo.mergeTrees.ArtistReplacingMergeTree;
import ssau.labs.model.Album;
import ssau.labs.model.Artist;
import ssau.labs.db.OtherQueriesExecutor;
import ssau.labs.repo.impl.AlbumRepository;
import ssau.labs.repo.impl.ArtistRepository;

public class Main {
    public static void main(String[] args) {
        ClickHouseConnector connector = new ClickHouseConnector();

        Artist coldplay = new Artist(1, "Coldplay");
        Artist placebo = new Artist(2,  "Placeb");
        Artist mujuice = new Artist(3, "Mujuice");

        Album downshifting = new Album(1, "Downshifting", "Электронная", mujuice);
        Album melancholium = new Album(2, "Melancholium", "Электронная", mujuice);
        Album aPlaceForUsToDream = new Album(3, "A Place For Us To Dream", "Альтернатива", placebo);
        Album parachutes = new Album(4, "Parachutes", "Рок", coldplay);
        Album myloXyloto = new Album(5, "Mylo Xyloto", "Рок", coldplay);

        ArtistRepository artistRepo = new ArtistRepository(connector);

        artistRepo.save(placebo);
        artistRepo.save(coldplay);
        artistRepo.save(mujuice);

//        /* Artist CRUD
        artistRepo.findAll();
        System.out.println("-----------------------------------");

        System.out.println(artistRepo.findById(placebo.getId()));
        System.out.println("-----------------------------------");

        placebo.setName("Placebo");
        artistRepo.update(placebo);
        artistRepo.findAll();
        System.out.println("-----------------------------------");

        artistRepo.delete(placebo.getId());
        artistRepo.findAll();
        System.out.println("-----------------------------------");
        artistRepo.save(placebo);
//         */
//        /* Album CRUD
        AlbumRepository albumRepo = new AlbumRepository(connector, artistRepo);

        albumRepo.save(downshifting);
        albumRepo.save(melancholium);
        albumRepo.save(aPlaceForUsToDream);
        albumRepo.save(parachutes);
        albumRepo.save(myloXyloto);
        albumRepo.findAll();
        System.out.println("-----------------------------------");

        System.out.println(albumRepo.findById(melancholium.getId()));
        System.out.println("-----------------------------------");

        melancholium.setName("A Rush of Blood to the Head");
        albumRepo.update(melancholium);
        albumRepo.findAll();
        System.out.println("-----------------------------------");

        albumRepo.delete(melancholium.getId());
        albumRepo.findAll();
        System.out.println("-----------------------------------");
//         */
//        /* Joins and group by having
        OtherQueriesExecutor joins = new OtherQueriesExecutor(connector);
        joins.innerJoin();
        System.out.println("-----------------------------------");
        joins.leftJoin();
        System.out.println("-----------------------------------");
        joins.rightJoin();
        System.out.println("-----------------------------------");
        joins.fullJoin();
        System.out.println("-----------------------------------");
        joins.crossJoin();
        System.out.println("-----------------------------------");
        joins.groupByHaving();
        System.out.println("-----------------------------------");
//         */
//        /* Artist with replacingMergeTree
        ArtistReplacingMergeTree replacingMergeTree = new ArtistReplacingMergeTree(connector);

        replacingMergeTree.insert(coldplay, 1);
        replacingMergeTree.insert(coldplay, 2);
        replacingMergeTree.insert(coldplay, 3);

        replacingMergeTree.select();
        System.out.println("-----------------------------------");
//         */
//        /* Artist with collapsingMergeTree
        ArtistCollapsingMergeTree collapsingMergeTree = new ArtistCollapsingMergeTree(connector);

        collapsingMergeTree.insert(coldplay, 1);
        collapsingMergeTree.insert(mujuice, 1);
        collapsingMergeTree.insert(coldplay, -1);

        collapsingMergeTree.select();
//         */
    }
}