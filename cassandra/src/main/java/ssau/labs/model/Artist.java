package ssau.labs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Artist {
    private Integer id;
    private String name;
    private String country;
    private LocalDate activeSince;
}
