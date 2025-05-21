package ssau.labs.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Album {
    private String id;
    private String name;
    private String genre;
    private String artistId;
}
