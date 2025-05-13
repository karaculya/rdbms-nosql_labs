package ssau.labs.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ssau.labs.model.Artist;

@Repository
public interface ArtistRepository extends CrudRepository<Artist, Integer> {
}
