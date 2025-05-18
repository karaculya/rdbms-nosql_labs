package ssau.labs.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ssau.labs.model.Artist;
import ssau.labs.repo.impl.ArtistRepository;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/artist")
public class ArtistController {
    @Autowired
    private ArtistRepository repository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveArtist(@RequestBody Artist artist) {
        repository.save(artist);
    }

    @GetMapping("/{id}")
    public Artist getArtistById(@PathVariable Integer id) {
        Artist artist = repository.findById(id);
        System.out.println(artist);
        return artist;
    }

    @GetMapping()
    public List<Artist> getAllArtists() {
        try {
            List<Artist> artists = repository.findAll();
            log.info("Found {} artists", artists.size());
            return artists;
        } catch (Exception e) {
            log.error("Error getting all artists", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateArtist(@PathVariable Integer id, @RequestBody Artist artist) {
        repository.update(id, artist);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteArtist(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
