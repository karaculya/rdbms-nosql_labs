package ssau.labs.model;

import lombok.Data;

import java.sql.Time;

@Data
public class Composition {
    private Integer id;
    private String name;
    private Time duration;
    private Album album;
}
