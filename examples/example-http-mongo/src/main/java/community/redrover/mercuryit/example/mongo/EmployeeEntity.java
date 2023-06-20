package community.redrover.mercuryit.example.mongo;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;


@Document
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmployeeEntity {

    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";
    public static final String TITLE_FIELD = "title";

    @Id
    @Field(name = ID_FIELD, targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = NAME_FIELD)
    private String name;

    @Field(name = TITLE_FIELD)
    private String title;
}
