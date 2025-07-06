package pavlo.melnyk.transitanalyzer.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StopDistanceDto {
    private String stopId;
    private String name;
    private GeoPoint location;
    private Double distanceInKm;

    public Double getDistanceInKm() {
        if (distanceInKm == null) {
            return null;
        }
        return BigDecimal.valueOf(distanceInKm)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
