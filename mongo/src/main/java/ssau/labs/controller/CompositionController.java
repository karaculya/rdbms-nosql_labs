package ssau.labs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssau.labs.model.Composition;
import ssau.labs.repo.CompositionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/compositions")
public class CompositionController {
    private final CompositionRepository repository;

    public CompositionController(CompositionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Composition>> getAllCompositions() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Composition> getCompositionById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Composition>> getCompositionsByAlbum(@PathVariable String albumId) {
        return ResponseEntity.ok(repository.findByAlbumId(albumId));
    }

    @PostMapping
    public ResponseEntity<Composition> createComposition(@RequestBody Composition composition) {
        return ResponseEntity.ok(repository.save(composition));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Composition> updateComposition(
            @PathVariable String id,
            @RequestBody Composition composition) {
        if (repository.update(id, composition)) {
            return ResponseEntity.ok(composition);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComposition(@PathVariable String id) {
        if (repository.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        repository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/album/{albumId}")
    public ResponseEntity<String> deleteCompositionsByAlbum(@PathVariable String albumId) {
        return ResponseEntity.ok(repository.deleteManyByAlbumId(albumId));
    }

    @PutMapping("/album/{oldAlbumId}/{newAlbumId}")
    public ResponseEntity<String> updateCompositionsAlbum(
            @PathVariable String oldAlbumId,
            @PathVariable String newAlbumId) {
        return ResponseEntity.ok(repository.updateManyAlbumId(oldAlbumId, newAlbumId));
    }
}