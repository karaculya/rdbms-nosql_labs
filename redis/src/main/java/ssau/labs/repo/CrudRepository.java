package ssau.labs.repo;

import java.util.List;

public interface CrudRepository<T> {
    void save(T t);

    T findById(Integer id);

    List<T> findAll();

    void update(Integer id, T t);

    void deleteById(Integer id);
}
