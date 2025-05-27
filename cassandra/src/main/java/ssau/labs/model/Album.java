package ssau.labs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Album {
    private Integer id;
    private String name;
    private String genre;
    private Artist artist;
    private int releaseYear;
}
