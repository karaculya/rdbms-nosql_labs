package ssau.labs.repo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.micrometer.common.util.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ssau.labs.model.Composition;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class CompositionRepository {
    private static final String COLLECTION_NAME = "compositions";
    private static final String NAME_FIELD = "name";
    private static final String DURATION_FIELD = "duration";
    private static final String ALBUM_ID_FIELD = "albumId";
    private static final String ID_FIELD = "_id";

    private final MongoCollection<Document> collection;

    @Autowired
    public CompositionRepository(MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    public List<Composition> findAll() {
        List<Composition> compositions = new ArrayList<>();
        collection.find().forEach(doc -> compositions.add(toComposition(doc)));
        return compositions;
    }

    public Optional<Composition> findById(String id) {
        Document doc = collection.find(Filters.eq(ID_FIELD, id)).first();
        return Optional.ofNullable(toComposition(doc));
    }

    public List<Composition> findByAlbumId(String albumId) {
        List<Composition> compositions = new ArrayList<>();
        collection.find(Filters.eq(ALBUM_ID_FIELD, albumId))
                .forEach(doc -> compositions.add(toComposition(doc)));
        return compositions;
    }

    public Composition save(Composition composition) {
        Document doc = toDocument(composition);
        collection.insertOne(doc);
        return composition;
    }

    public boolean update(String id, Composition composition) {
        UpdateResult result = collection.replaceOne(
                Filters.eq(ID_FIELD, id),
                toDocument(composition)
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

    public String updateManyAlbumId(String oldAlbumId, String newAlbumId) {
        try {
            Bson filter = Filters.eq(ALBUM_ID_FIELD, oldAlbumId);
            Bson update = Updates.set(ALBUM_ID_FIELD, newAlbumId);

            UpdateResult result = collection.updateMany(filter, update);
            return String.format("Updated %d compositions", result.getModifiedCount());
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public String deleteManyByAlbumId(String albumId) {
        try {
            Bson filter = Filters.eq(ALBUM_ID_FIELD, albumId);
            DeleteResult result = collection.deleteMany(filter);
            return String.format("Deleted %d compositions", result.getDeletedCount());
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }

    private Composition toComposition(Document doc) {
        if (doc == null) return null;
        return new Composition(
                doc.get(ID_FIELD).toString(),
                doc.get(NAME_FIELD).toString(),
                doc.get(DURATION_FIELD).toString(),
                doc.get(ALBUM_ID_FIELD).toString()
        );
    }

    private Document toDocument(Composition composition) {
        return StringUtils.isNotBlank(composition.getId()) ?
                new Document(ID_FIELD, composition.getId())
                        .append(NAME_FIELD, composition.getName())
                        .append(DURATION_FIELD, composition.getDuration())
                        .append(ALBUM_ID_FIELD, composition.getAlbumId())
                : new Document(NAME_FIELD, composition.getName())
                .append(DURATION_FIELD, composition.getDuration())
                .append(ALBUM_ID_FIELD, composition.getAlbumId());
    }
}