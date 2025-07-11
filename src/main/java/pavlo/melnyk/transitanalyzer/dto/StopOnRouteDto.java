package pavlo.melnyk.transitanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopOnRouteDto {
    private String stopId;
    private String stopName;
    private int stopSequence;
}
