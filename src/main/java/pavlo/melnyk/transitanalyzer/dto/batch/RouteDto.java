package pavlo.melnyk.transitanalyzer.dto.batch;

import lombok.Data;

@Data
public class RouteDto {
    private String routeId;
    private String agencyId;
    private String routeShortName;
    private String routeLongName;
    private String routeDesc;
    private int routeType;
    private String routeUrl;
    private String routeColor;
    private String routeTextColor;
}
