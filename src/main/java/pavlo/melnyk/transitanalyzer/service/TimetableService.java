package pavlo.melnyk.transitanalyzer.service;

import java.util.List;
import pavlo.melnyk.transitanalyzer.dto.DepartureDto;

public interface TimetableService {
    List<DepartureDto> getDeparturesForStop(String stopId);
}
