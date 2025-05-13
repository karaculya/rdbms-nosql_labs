package ssau.labs.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ssau.labs.model.Album;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Integer> {
}
