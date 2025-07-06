package pavlo.melnyk.transitanalyzer.entity;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.GeoShapeField;

@Document(indexName = "routes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RouteShape {
    @Id
    private String shapeId;

    @GeoShapeField
    private Map<String, Object> shape;
}
