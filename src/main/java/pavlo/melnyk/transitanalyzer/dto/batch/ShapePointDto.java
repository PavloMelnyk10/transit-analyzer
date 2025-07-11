package pavlo.melnyk.transitanalyzer.dto.batch;

import lombok.Data;

@Data
public class ShapePointDto {
    private String shapeId;
    private double shapePtLat;
    private double shapePtLon;
    private int shapePtSequence;
    private Double shapeDistTraveled;
}
