package ssau.labs.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface CrudRepository<T> {
    void save(T t);

    T findById(Integer id);

    void update(T t);

    void delete(Integer id);

    ResultSet findAll();

    List<T> printRow(ResultSet resultSet) throws SQLException;
}
