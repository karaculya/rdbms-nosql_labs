package ssau.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ssau.labs.model.Artist;
import ssau.labs.service.ArtistService;

import java.util.List;

@RestController
@RequestMapping("/artist")
public class ArtistController {
    @Autowired
    private ArtistService service;

    @PostMapping
    public void saveArtist(@RequestBody Artist artist) {
        service.save(artist);
    }

    @GetMapping("/{id}")
    public Artist getArtistById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @GetMapping()
    public List<Artist> getAllArtists() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    public void updateArtist(@PathVariable Integer id, @RequestBody Artist artist) {
        service.update(id, artist);
    }

    @DeleteMapping("/{id}")
    public void deleteArtist(@PathVariable Integer id) {
        service.delete(id);
    }
}
