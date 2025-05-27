package ssau.labs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Composition {
    private Integer id;
    private String name;
    private int durationSec;
    private Album album;
    private int trackNumber;
}
