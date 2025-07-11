package pavlo.melnyk.transitanalyzer.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDetailsDto {
    private String routeLongName;
    private List<StopOnRouteDto> stops;
}
