package ssau.labs.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Time;

@Data
@AllArgsConstructor
public class Composition {
    private String id;
    private String name;
    private String duration;
    private String albumId;
}
