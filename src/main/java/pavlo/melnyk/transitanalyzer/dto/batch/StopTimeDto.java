package pavlo.melnyk.transitanalyzer.dto.batch;

import lombok.Data;

@Data
public class StopTimeDto {
    private String tripId;
    private String arrivalTime;
    private String departureTime;
    private String stopId;
    private int stopSequence;
    private String stopHeadsign;
    private Integer pickupType;
    private Integer dropOffType;
    private String shapeDistTraveled;
    private String timepoint;
}
