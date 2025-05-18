package ssau.labs.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Album implements Serializable {
    private Integer id;
    private String name;
    private String genre;
    private Integer artistId;
}
