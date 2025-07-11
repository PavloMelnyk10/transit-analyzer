package pavlo.melnyk.transitanalyzer.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Component;
import org.wololo.jts2geojson.GeoJSONWriter;
import pavlo.melnyk.transitanalyzer.dto.batch.ShapePointDto;
import pavlo.melnyk.transitanalyzer.entity.RouteShape;

@Component
@RequiredArgsConstructor
public class RouteShapeProcessor {

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final GeoJSONWriter geoJsonWriter = new GeoJSONWriter();
    private final ObjectMapper objectMapper;

    public RouteShape process(String shapeId, List<ShapePointDto> points) {
        if (points.size() < 2) {
            return null;
        }

        points.sort(Comparator.comparingInt(ShapePointDto::getShapePtSequence));

        Coordinate[] coordinates = points.stream()
                .map(p -> new Coordinate(p.getShapePtLon(), p.getShapePtLat()))
                .toArray(Coordinate[]::new);

        try {
            LineString lineString = geometryFactory.createLineString(coordinates);
            String geoJsonString = geoJsonWriter.write(lineString).toString();
            Map<String, Object> geoJson = objectMapper.readValue(geoJsonString,
                    new TypeReference<>() {});
            return new RouteShape(shapeId, geoJson);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to parse GeoJSON for shapeId="
                    + shapeId + ": " + e.getMessage());
            return null;
        }
    }
}
