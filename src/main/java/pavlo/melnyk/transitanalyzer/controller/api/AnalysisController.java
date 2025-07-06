package pavlo.melnyk.transitanalyzer.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pavlo.melnyk.transitanalyzer.dto.StopDistanceDto;
import pavlo.melnyk.transitanalyzer.service.AnalysisService;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/stops-near/{stopId}")
    public ResponseEntity<Page<StopDistanceDto>> getStopsNear(
            @PathVariable String stopId,
            @RequestParam(defaultValue = "1.0") double distance,
            Pageable pageable) {
        Page<StopDistanceDto> stopsPage = analysisService
                .findStopsNear(stopId, distance, pageable);
        return ResponseEntity.ok(stopsPage);
    }
}
