package pavlo.melnyk.transitanalyzer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "trips")
public class Trip {
    @Id
    @Field(type = FieldType.Keyword)
    private String tripId;

    @Field(type = FieldType.Keyword, name = "route_id")
    private String routeId;

    @Field(type = FieldType.Keyword, name = "service_id")
    private String serviceId;

    @Field(type = FieldType.Text)
    private String tripHeadsign;

    @Field(type = FieldType.Integer, name = "direction_id")
    private Integer directionId;

    @Field(type = FieldType.Keyword)
    private String shapeId;
}
