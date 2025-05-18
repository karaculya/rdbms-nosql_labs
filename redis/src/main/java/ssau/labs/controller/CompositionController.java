package ssau.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ssau.labs.model.Composition;
import ssau.labs.repo.impl.CompositionRepository;

import java.util.List;

@RestController
@RequestMapping("/composition")
public class CompositionController {
    @Autowired
    private CompositionRepository repository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveComposition(@RequestBody Composition composition) {
        repository.save(composition);
    }

    @GetMapping("/{id}")
    public Composition getCompositionById(@PathVariable Integer id) {
        return repository.findById(id);
    }

    @GetMapping("/by-album/{albumId}")
    public List<Composition> getCompositionsByAlbum(@PathVariable Integer albumId) {
        return repository.findByAlbumId(albumId);
    }

    @GetMapping
    public List<Composition> getAllCompositions() {
        return repository.findAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateComposition(@PathVariable Integer id, @RequestBody Composition composition) {
        repository.update(id, composition);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComposition(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
