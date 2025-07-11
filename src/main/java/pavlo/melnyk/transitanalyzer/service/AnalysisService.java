package pavlo.melnyk.transitanalyzer.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pavlo.melnyk.transitanalyzer.dto.RouteInfoResponseDto;
import pavlo.melnyk.transitanalyzer.dto.StopDistanceDto;
import pavlo.melnyk.transitanalyzer.dto.StopSearchDto;

public interface AnalysisService {
    Page<StopDistanceDto> findStopsNear(String stopId, double distanceInKm, Pageable pageable);

    List<RouteInfoResponseDto> findRoutesByStop(String stopId);

    List<StopSearchDto> searchStopsByName(String name);
}
