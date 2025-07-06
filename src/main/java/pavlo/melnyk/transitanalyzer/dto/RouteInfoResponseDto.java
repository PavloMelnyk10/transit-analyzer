package pavlo.melnyk.transitanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteInfoResponseDto {
    private String routeShortName;
    private String routeLongName;
    private int routeType;
}
