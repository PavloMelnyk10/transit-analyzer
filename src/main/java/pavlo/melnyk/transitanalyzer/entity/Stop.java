package pavlo.melnyk.transitanalyzer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Document(indexName = "stops")
@Getter
@Setter
@AllArgsConstructor
public class Stop {
    @Id
    private String stopId;

    @Field(type = FieldType.Text, name = "stop_name")
    private String name;

    @GeoPointField
    private GeoPoint location;
}
