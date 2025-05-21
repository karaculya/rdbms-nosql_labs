package ssau.labs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssau.labs.model.Artist;
import ssau.labs.repo.ArtistRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/artists")
public class ArtistController {
    private final ArtistRepository artistRepository;

    public ArtistController(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @GetMapping
    public ResponseEntity<List<Artist>> getAllArtists() {
        List<Artist> artists = artistRepository.findAll();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Artist> getArtistByName(@PathVariable String name) {
        Optional<Artist> artist = artistRepository.findByName(name);
        return artist.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Artist> createArtist(@RequestBody Artist artist) {
        Artist savedArtist = artistRepository.save(artist);
        return ResponseEntity.ok(savedArtist);
    }

    @PutMapping("/{name}")
    public ResponseEntity<Artist> updateArtist(
            @PathVariable String name,
            @RequestBody Artist artist) {
        Artist updated = artistRepository.update(name, artist);
        if (updated != null) {
            return ResponseEntity.ok(artist);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteArtist(@PathVariable String name) {
        boolean deleted = artistRepository.delete(name);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllArtists() {
        artistRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
}