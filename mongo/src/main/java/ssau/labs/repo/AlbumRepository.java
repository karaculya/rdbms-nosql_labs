package ssau.labs.repo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.micrometer.common.util.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ssau.labs.model.Album;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AlbumRepository {
    private static final String COLLECTION_NAME = "albums";
    private static final String NAME_FIELD = "name";
    private static final String GENRE_FIELD = "genre";
    private static final String ARTIST_ID_FIELD = "artistId";
    private static final String ID_FIELD = "_id";

    private final MongoCollection<Document> collection;

    @Autowired
    public AlbumRepository(MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    public List<Album> findAll() {
        List<Album> albums = new ArrayList<>();
        collection.find().forEach(doc -> albums.add(toAlbum(doc)));
        return albums;
    }

    public Optional<Album> findById(String id) {
        Document doc = collection.find(Filters.eq(ID_FIELD, id)).first();
        return Optional.ofNullable(toAlbum(doc));
    }

    public List<Album> findByArtistId(String artistId) {
        List<Album> albums = new ArrayList<>();
        collection.find(Filters.eq(ARTIST_ID_FIELD, artistId))
                .forEach(doc -> albums.add(toAlbum(doc)));
        return albums;
    }

    public Album save(Album album) {
        Document doc = toDocument(album);
        collection.insertOne(doc);
        return album;
    }

    public boolean update(String id, Album album) {
        UpdateResult result = collection.replaceOne(
                Filters.eq(ID_FIELD, id),
                toDocument(album)
        );
        return result.getModifiedCount() > 0;
    }

    public boolean delete(String id) {
        DeleteResult result = collection.deleteOne(Filters.eq(ID_FIELD, id));
        return result.getDeletedCount() > 0;
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }

    private Album toAlbum(Document doc) {
        if (doc == null) return null;
        return new Album(
                doc.get(ID_FIELD).toString(),
                doc.get(NAME_FIELD).toString(),
                doc.get(GENRE_FIELD).toString(),
                doc.get(ARTIST_ID_FIELD).toString()
        );
    }

    private Document toDocument(Album album) {
        return StringUtils.isNotBlank(album.getId()) ?
                new Document(ID_FIELD, album.getId())
                        .append(NAME_FIELD, album.getName())
                        .append(GENRE_FIELD, album.getGenre())
                        .append(ARTIST_ID_FIELD, album.getArtistId())
                : new Document(NAME_FIELD, album.getName())
                .append(GENRE_FIELD, album.getGenre())
                .append(ARTIST_ID_FIELD, album.getArtistId());
    }
}