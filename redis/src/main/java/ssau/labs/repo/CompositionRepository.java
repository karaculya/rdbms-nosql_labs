package ssau.labs.repo;

import org.springframework.data.repository.CrudRepository;
import ssau.labs.model.Composition;

public interface CompositionRepository extends CrudRepository<Composition, Integer> {
}
