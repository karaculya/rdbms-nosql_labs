package ssau.labs.repo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import io.micrometer.common.util.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ssau.labs.model.Artist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ArtistRepository {
    private static final String COLLECTION_NAME = "artists";
    private static final String NAME_FIELD = "name";
    private static final String ID_FIELD = "_id";

    private final MongoCollection<Document> collection;

    @Autowired
    public ArtistRepository(MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    public List<Artist> findAll() {
        List<Artist> artists = new ArrayList<>();
        FindIterable<Document> documents = collection.find();

        for (Document doc : documents) {
            artists.add(toArtist(doc));
        }
        return artists;
    }

    public Optional<Artist> findByName(String name) {
        Document doc = collection.find(new Document(NAME_FIELD, name)).first();
        return Optional.ofNullable(doc).map(this::toArtist);
    }

    public boolean existsByName(String name) {
        return collection.countDocuments(new Document(NAME_FIELD, name)) > 0;
    }

    public Artist save(Artist artist) {
        Document doc = toDocument(artist);
        collection.insertOne(doc);
        return artist;
    }

    public Artist update(String name, Artist artist) {
        collection.replaceOne(new Document(NAME_FIELD, name), toDocument(artist));
        return artist;
    }

    public boolean delete(String name) {
        DeleteResult result = collection.deleteOne(new Document(NAME_FIELD, name));
        return result.getDeletedCount() > 0;
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }

    private Artist toArtist(Document doc) {
        return new Artist(doc.get(ID_FIELD).toString(), doc.get(NAME_FIELD).toString());
    }

    private Document toDocument(Artist artist) {
        return StringUtils.isNotBlank(artist.getId()) ?
                new Document(ID_FIELD, artist.getId())
                        .append(NAME_FIELD, artist.getName())
                : new Document(NAME_FIELD, artist.getName());
    }
}