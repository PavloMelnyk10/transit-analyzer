package pavlo.melnyk.transitanalyzer.service;

import java.util.Optional;
import pavlo.melnyk.transitanalyzer.dto.RouteDetailsDto;

public interface RouteService {
    Optional<RouteDetailsDto> getRouteDetails(String routeShortName, int directionId);
}
