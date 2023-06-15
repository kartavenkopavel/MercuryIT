package community.redrover.mercuryit.example.http;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmployeeEntity {

    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";
    public static final String TITLE_FIELD = "title";

    @Id
    @GeneratedValue
    @Column(name = ID_FIELD)
    private Long id;

    @Column(name = NAME_FIELD)
    private String name;

    @Column(name = TITLE_FIELD)
    private String title;
}
