package pavlo.melnyk.transitanalyzer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "routes_info")
public class Route {
    @Id
    @Field(type = FieldType.Keyword)
    private String routeId;

    @Field(type = FieldType.Text, name = "short_name")
    private String routeShortName;

    @Field(type = FieldType.Text, name = "long_name")
    private String routeLongName;

    @Field(type = FieldType.Integer, name = "type")
    private int routeType;
}
