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
@Document(indexName = "stop_times")
public class StopTime {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String tripId;

    @Field(type = FieldType.Text)
    private String arrivalTime;

    @Field(type = FieldType.Text)
    private String departureTime;

    @Field(type = FieldType.Keyword)
    private String stopId;

    @Field(type = FieldType.Integer)
    private int stopSequence;
}
