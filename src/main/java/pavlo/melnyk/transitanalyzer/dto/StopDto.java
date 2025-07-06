package pavlo.melnyk.transitanalyzer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopDto {
    private String stopId;
    private String stopCode;
    private String stopName;
    private String stopDesc;
    private Double stopLat;
    private Double stopLon;
    private String zoneId;
    private String stopUrl;
    private Integer locationType;
    private String parentStation;
    private String stopTimezone;
    private Integer wheelchairBoarding;
    private String platformCode;
}
