package pavlo.melnyk.transitanalyzer.dto;

import lombok.Data;

@Data
public class TripDto {
    private String tripId;
    private String routeId;
    private String serviceId;
    private String tripHeadsign;
    private String tripShortName;
    private Integer directionId;
    private String blockId;
    private String shapeId;
    private Integer wheelchairAccessible;
}
