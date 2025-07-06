package pavlo.melnyk.transitanalyzer.dto;

import lombok.Data;

@Data
public class StopDto {
    private String stopId;
    private String stopCode;
    private String stopName;
    private String stopDesc;
    private double stopLat;
    private double stopLon;
    private String zoneId;
    private String stopUrl;
    private int locationType;
    private String parentStation;
    private String stopTimezone;
    private String wheelchairBoarding;
}
