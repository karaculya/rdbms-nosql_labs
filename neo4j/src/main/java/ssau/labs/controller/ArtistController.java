package ssau.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssau.labs.model.Artist;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/artist")
public class ArtistController {
    private final Neo4jClient neo4jClient;

    @Autowired
    public ArtistController(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Artist artist) {
        String query = "CREATE (a:Artist {name: $name}) RETURN a.name as name";

        Optional<String> createdName = neo4jClient.query(query)
                .bind(artist.getName()).to("name")
                .fetchAs(String.class)
                .one();

        return createdName.map(name -> ResponseEntity.status(HttpStatus.CREATED).body(name))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public List<String> getAll() {
        String query = "MATCH (a:Artist) RETURN a.name as name ORDER BY a.name";

        return (List<String>) neo4jClient.query(query)
                .fetchAs(String.class)
                .all();
    }

    @GetMapping("/{name}")
    public ResponseEntity<String> getByName(@PathVariable String name) {
        String query = "MATCH (a:Artist {name: $name}) RETURN a.name as name";

        Optional<String> artist = neo4jClient.query(query)
                .bind(name).to("name")
                .fetchAs(String.class)
                .one();

        return artist.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{oldName}")
    public ResponseEntity<String> update(
            @PathVariable String oldName,
            @RequestBody Artist artist) {
        String query = "MATCH (a:Artist {name: $oldName}) " +
                "SET a.name = $newName RETURN a.name as name";

        Optional<String> updatedName = neo4jClient.query(query)
                .bind(oldName).to("oldName")
                .bind(artist.getName()).to("newName")
                .fetchAs(String.class)
                .one();

        return updatedName.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        String query = "MATCH (a:Artist {name: $name}) DELETE a";

        long deletedCount = neo4jClient.query(query)
                .bind(name).to("name")
                .run()
                .counters()
                .nodesDeleted();

        return deletedCount > 0
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
    @DeleteMapping()
    public ResponseEntity<Void> deleteAll() {
        String query = "MATCH (a:Artist) DETACH DELETE a";

        long deletedCount = neo4jClient.query(query)
                .run()
                .counters()
                .nodesDeleted();

        return deletedCount > 0
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
