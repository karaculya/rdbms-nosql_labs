package ssau.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ssau.labs.model.Album;
import ssau.labs.repo.impl.AlbumRepository;

import java.util.List;

@RestController
@RequestMapping("/album")
public class AlbumController {
    @Autowired
    private AlbumRepository repository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAlbum(@RequestBody Album album) {
        repository.save(album);
    }

    @GetMapping("/{id}")
    public Album getAlbumById(@PathVariable Integer id) {
        return repository.findById(id);
    }

    @GetMapping("/by-artist/{artistId}")
    public List<Album> getAlbumsByArtist(@PathVariable Integer artistId) {
        return repository.findByArtistId(artistId);
    }

    @GetMapping
    public List<Album> getAllAlbums() {
        return repository.findAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateAlbum(@PathVariable Integer id, @RequestBody Album album) {
        repository.update(id, album);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAlbum(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
