package ssau.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssau.labs.model.Album;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/album")
public class AlbumController {
    private final Neo4jClient neo4jClient;

    @Autowired
    public AlbumController(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @PostMapping
    public ResponseEntity<Album> create(@RequestBody Album album) {
        String query = "MATCH (a:Artist {name: $artistName}) " +
                "CREATE (al:Album {name: $name, genre: $genre}) " +
                "CREATE (al)-[:Written_by]->(a) " +
                "RETURN al.name as name, al.genre as genre, a.name as artistName";

        Optional<Album> createdAlbum = neo4jClient.query(query)
                .bind(album.getArtistName()).to("artistName")
                .bind(album.getName()).to("name")
                .bind(album.getGenre()).to("genre")
                .fetchAs(Album.class)
                .mappedBy((typeSystem, record) ->
                        new Album(
                                record.get("name").asString(),
                                record.get("genre").asString(),
                                record.get("artistName").asString()
                        ))
                .one();

        return createdAlbum.map(a -> ResponseEntity.status(HttpStatus.CREATED).body(a))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public List<Album> getAll() {
        String query = "MATCH (al:Album)-[:Written_by]->(a:Artist) " +
                "RETURN al.name as name, " +
                "al.genre as genre, " +
                "a.name as artistName ORDER BY al.name";

        return neo4jClient.query(query)
                .fetchAs(Album.class)
                .mappedBy((typeSystem, record) ->
                        new Album(
                                record.get("name").asString(),
                                record.get("genre").asString(),
                                record.get("artistName").asString()
                        ))
                .all()
                .stream()
                .toList();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Album> getByName(@PathVariable String name) {
        String query = "MATCH (al:Album {name: $name})-[:Written_by]->(a:Artist) " +
                "RETURN al.name as name, al.genre as genre, a.name as artistName";

        Optional<Album> album = neo4jClient.query(query)
                .bind(name).to("name")
                .fetchAs(Album.class)
                .mappedBy((typeSystem, record) ->
                        new Album(
                                record.get("name").asString(),
                                record.get("genre").asString(),
                                record.get("artistName").asString()
                        ))
                .one();

        return album.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{oldName}")
    public ResponseEntity<Album> update(
            @PathVariable String oldName,
            @RequestBody Album updatedAlbum) {

        String query = "MATCH (al:Album {name: $oldName})-[:Written_by]->" +
                "(a:Artist) SET al.name = $newName, " +
                "al.genre = $newGenre RETURN al.name as name, " +
                "al.genre as genre, a.name as artistName";

        Optional<Album> album = neo4jClient.query(query)
                .bind(oldName).to("oldName")
                .bind(updatedAlbum.getName()).to("newName")
                .bind(updatedAlbum.getGenre()).to("newGenre")
                .fetchAs(Album.class)
                .mappedBy((typeSystem, record) ->
                        new Album(
                                record.get("name").asString(),
                                record.get("genre").asString(),
                                record.get("artistName").asString()
                        ))
                .one();

        return album.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        String query = "MATCH (a:Album {name: $name}) DETACH DELETE a";

        long deletedCount = neo4jClient.query(query)
                .bind(name).to("name")
                .run()
                .counters()
                .nodesDeleted();

        return deletedCount > 0
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        String query = "MATCH (a:Album) DETACH DELETE a";

        long deletedCount = neo4jClient.query(query)
                .run()
                .counters()
                .nodesDeleted();

        return deletedCount > 0
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/relation/{albumName}/{artistName}")
    public ResponseEntity<Void> deleteRelation(
            @PathVariable String albumName,
            @PathVariable String artistName) {

        String query = "MATCH (a:Album {name: $albumName})" +
                "-[r:Written_by]->(ar:Artist {name: $artistName}) DELETE r";

        long relationsDeleted = neo4jClient.query(query)
                .bind(albumName).to("albumName")
                .bind(artistName).to("artistName")
                .run()
                .counters()
                .relationshipsDeleted();

        return relationsDeleted > 0
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/relation/{albumName}/{artistName}/{year}")
    public ResponseEntity<String> addPropertyToRelationship(
            @PathVariable String albumName,
            @PathVariable String artistName,
            @PathVariable Integer year) {
        String query = "MATCH (a:Album {name: $albumName})-[r:Written_by]->" +
                "(ar:Artist {name: $artistName}) SET r.released = $year";

        long relationshipsCreated = neo4jClient.query(query)
                .bind(albumName).to("albumName")
                .bind(artistName).to("artistName")
                .bind(year).to("year")
                .run()
                .counters()
                .relationshipsCreated();

        return relationshipsCreated > 0
                ? ResponseEntity.ok("Add released " + year)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/relation/{albumName}/{artistName}/released")
    public ResponseEntity<String> removePropertyFromRelationship(
            @PathVariable String albumName,
            @PathVariable String artistName) {
        String query = "MATCH (a:Album {name: $albumName})-[r:Written_by]->" +
                "(ar:Artist {name: $artistName}) REMOVE r.released";

        long relationsDeleted = neo4jClient.query(query)
                .bind(albumName).to("albumName")
                .bind(artistName).to("artistName")
                .run()
                .counters()
                .relationshipsDeleted();

        return relationsDeleted > 0
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}