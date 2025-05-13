package ssau.labs.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@NoArgsConstructor
@RedisHash("Album")
public class Album {
    @Id
    @Indexed
    private Integer id;
    private String name;
    private String genre;
    private Artist artist;
}
