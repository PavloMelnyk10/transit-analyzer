package pavlo.melnyk.transitanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopSearchDto {
    private String stopId;
    private String stopName;
}
