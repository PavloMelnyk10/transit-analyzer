package pavlo.melnyk.transitanalyzer.controller.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pavlo.melnyk.transitanalyzer.dto.RouteInfoResponseDto;
import pavlo.melnyk.transitanalyzer.dto.StopDistanceDto;
import pavlo.melnyk.transitanalyzer.dto.StopSearchDto;
import pavlo.melnyk.transitanalyzer.service.StopService;

@RestController
@RequestMapping("/api/stop")
@RequiredArgsConstructor
public class StopController {
    private final StopService analysisService;

    @GetMapping("/stops-near/{stopId}")
    public ResponseEntity<Page<StopDistanceDto>> getStopsNear(
            @PathVariable String stopId,
            @RequestParam(defaultValue = "1") double distance,
            Pageable pageable) {
        return ResponseEntity.ok(analysisService.findStopsNear(stopId, distance, pageable));
    }

    @GetMapping("/{stopId}/routes")
    public ResponseEntity<List<RouteInfoResponseDto>> getRoutesForStop(
            @PathVariable String stopId) {
        return ResponseEntity.ok(analysisService.findRoutesByStop(stopId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StopSearchDto>> searchStopsByName(@RequestParam String name) {
        return ResponseEntity.ok(analysisService.searchStopsByName(name));
    }
}
