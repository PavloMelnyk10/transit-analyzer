package pavlo.melnyk.transitanalyzer.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pavlo.melnyk.transitanalyzer.dto.RouteDetailsDto;
import pavlo.melnyk.transitanalyzer.service.RouteService;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping("/{routeShortName}")
    public ResponseEntity<RouteDetailsDto> getRouteDetails(
            @PathVariable String routeShortName,
            @RequestParam(name = "direction", defaultValue = "0") int directionId) {
        return routeService.getRouteDetails(routeShortName, directionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
