package ssau.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssau.labs.model.Composition;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/composition")
public class CompositionController {
    private final Neo4jClient neo4jClient;

    @Autowired
    public CompositionController(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @PostMapping
    public ResponseEntity<Composition> create(@RequestBody Composition composition) {
        String query = "MATCH (a:Album {name: $albumName}) " +
                "CREATE (c:Composition {name: $name, duration: $duration}) " +
                "CREATE (c)-[:Part_of]->(a) " +
                "RETURN c.name as name, c.duration as duration, a.name as albumName";

        Optional<Composition> createdComposition = neo4jClient.query(query)
                .bind(composition.getName()).to("name")
                .bind(composition.getDuration()).to("duration")
                .bind(composition.getAlbumName()).to("albumName")
                .fetchAs(Composition.class)
                .mappedBy((typeSystem, record) ->
                        new Composition(
                                record.get("name").asString(),
                                record.get("duration").asString(),
                                record.get("albumName").asString()
                        ))
                .one();

        return createdComposition.map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public List<Composition> getAll() {
        String query = "MATCH (c:Composition)-[:Part_of]->(a:Album) " +
                "RETURN c.name as name, " +
                "c.duration as duration, " +
                "a.name as albumName ORDER BY c.name";

        return neo4jClient.query(query)
                .fetchAs(Composition.class)
                .mappedBy((typeSystem, record) ->
                        new Composition(
                                record.get("name").asString(),
                                record.get("duration").asString(),
                                record.get("albumName").asString()
                        ))
                .all()
                .stream()
                .toList();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Composition> getByName(@PathVariable String name) {
        String query = "MATCH (c:Composition {name: $name})-" +
                "[:Part_of]->(a:Album) RETURN c.name as name, " +
                "c.duration as duration, a.name as albumName";

        Optional<Composition> composition = neo4jClient.query(query)
                .bind(name).to("name")
                .fetchAs(Composition.class)
                .mappedBy((typeSystem, record) ->
                        new Composition(
                                record.get("name").asString(),
                                record.get("duration").asString(),
                                record.get("albumName").asString()
                        ))
                .one();

        return composition.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{oldName}")
    public ResponseEntity<Composition> update(
            @PathVariable String oldName,
            @RequestBody Composition updatedComposition) {

        String query = "MATCH (c:Composition {name: $oldName})-" +
                "[:Part_of]->(a:Album) SET c.name = $newName, " +
                "c.duration = $newDuration RETURN c.name as name, " +
                "c.duration as duration, a.name as albumName";

        Optional<Composition> composition = neo4jClient.query(query)
                .bind(oldName).to("oldName")
                .bind(updatedComposition.getName()).to("newName")
                .bind(updatedComposition.getDuration()).to("newDuration")
                .fetchAs(Composition.class)
                .mappedBy((typeSystem, record) ->
                        new Composition(
                                record.get("name").asString(),
                                record.get("duration").asString(),
                                record.get("albumName").asString()
                        ))
                .one();

        return composition.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        String query = "MATCH (c:Composition {name: $name}) DETACH DELETE c";

        long deletedCount = neo4jClient.query(query)
                .bind(name).to("name")
                .run()
                .counters()
                .nodesDeleted();

        return deletedCount > 0
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/relation/{compositionName}/{albumName}")
    public ResponseEntity<Void> deleteRelation(
            @PathVariable String compositionName,
            @PathVariable String albumName) {

        String query = "MATCH (c:Composition {name: $compositionName})-" +
                "[r:Part_of]->(a:Album {name: $albumName}) DELETE r";

        long relationsDeleted = neo4jClient.query(query)
                .bind(compositionName).to("compositionName")
                .bind(albumName).to("albumName")
                .run()
                .counters()
                .relationshipsDeleted();

        return relationsDeleted > 0
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteAll() {
        String query = "MATCH (c:Composition) DETACH DELETE c";

        long deletedCount = neo4jClient.query(query)
                .run()
                .counters()
                .nodesDeleted();

        return deletedCount > 0
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}