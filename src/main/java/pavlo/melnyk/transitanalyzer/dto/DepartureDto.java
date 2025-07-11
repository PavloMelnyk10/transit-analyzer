package pavlo.melnyk.transitanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepartureDto {
    private String routeName;
    private String direction;
    private String departureTime;
}
