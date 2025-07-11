package pavlo.melnyk.transitanalyzer.controller.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pavlo.melnyk.transitanalyzer.dto.DepartureDto;
import pavlo.melnyk.transitanalyzer.service.TimetableService;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @GetMapping("/{stopId}")
    public ResponseEntity<List<DepartureDto>> getDepartures(@PathVariable String stopId) {
        List<DepartureDto> departures = timetableService.getDeparturesForStop(stopId);
        return ResponseEntity.ok(departures);
    }
}
