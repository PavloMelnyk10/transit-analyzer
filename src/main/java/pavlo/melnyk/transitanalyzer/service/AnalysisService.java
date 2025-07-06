package pavlo.melnyk.transitanalyzer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pavlo.melnyk.transitanalyzer.dto.StopDistanceDto;

public interface AnalysisService {
    Page<StopDistanceDto> findStopsNear(String stopId, double distanceInKm, Pageable pageable);
}
