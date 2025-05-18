package ssau.labs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Composition implements Serializable {
    private Integer id;
    private String name;
    private Time duration;
    private Integer albumId;
}
