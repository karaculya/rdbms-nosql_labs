package ssau.labs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssau.labs.model.Artist;
import ssau.labs.repo.ArtistRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {
    @Autowired
    private ArtistRepository repository;

    public void save(Artist artist) {
        repository.save(artist);
    }

    public Artist findById(Integer id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Artist> findAll() {
        return (List<Artist>) repository.findAll();
    }

    public void update(Integer id, Artist artist) {
        Optional<Artist> existingCustomer = repository.findById(id);

        if (existingCustomer.isPresent()) {
            Artist updated
                    = existingCustomer.get();

            updated.setName(artist.getName());
            repository.deleteById(id);
            repository.save(updated);
        }
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
