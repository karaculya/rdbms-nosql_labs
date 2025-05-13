package ssau.nosql_1.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "compositions")
@Data
@NoArgsConstructor
public class Composition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,\n" +
    @Column(nullable = false)
    private String name; //name VARCHAR(64) NOT NULL,\n" +
    @Column(nullable = false)
    private Time duration;  //  duration TIME NOT NULL,\n" +
    @ManyToOne(optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    public Composition(Album album, Time duration, String name) {
        this.album = album;
        this.duration = duration;
        this.name = name;
    }
}
