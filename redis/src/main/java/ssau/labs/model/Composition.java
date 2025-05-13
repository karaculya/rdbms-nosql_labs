package ssau.labs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.index.Indexed;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Composition {
    @Id
    @Indexed
    private Integer id;
    private String name;
    private Time duration;
    private Album album;
}
