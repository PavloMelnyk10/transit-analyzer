package pavlo.melnyk.transitanalyzer.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pavlo.melnyk.transitanalyzer.dto.DepartureDto;
import pavlo.melnyk.transitanalyzer.entity.Route;
import pavlo.melnyk.transitanalyzer.entity.StopTime;
import pavlo.melnyk.transitanalyzer.entity.Trip;
import pavlo.melnyk.transitanalyzer.repository.RouteRepository;
import pavlo.melnyk.transitanalyzer.repository.StopTimeRepository;
import pavlo.melnyk.transitanalyzer.repository.TripRepository;

@Service
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {

    private static final DateTimeFormatter GTFS_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final StopTimeRepository stopTimeRepository;
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;

    @Override
    public List<DepartureDto> getDeparturesForStop(String stopId) {
        List<StopTime> stopTimes = stopTimeRepository.findByStopId(stopId);
        Map<String, Trip> tripsById = getTripsMap(stopTimes);
        Map<String, Route> routesById = getRoutesMap(tripsById);
        return buildDepartureList(stopTimes, tripsById, routesById);
    }

    private Map<String, Trip> getTripsMap(List<StopTime> stopTimes) {
        List<String> tripIds = stopTimes.stream()
                .map(StopTime::getTripId)
                .distinct()
                .collect(Collectors.toList());
        return tripRepository.findAllByTripIdIn(tripIds).stream()
                .collect(Collectors.toMap(Trip::getTripId, Function.identity()));
    }

    private Map<String, Route> getRoutesMap(Map<String, Trip> tripsById) {
        List<String> routeIds = tripsById.values().stream()
                .map(Trip::getRouteId)
                .distinct()
                .collect(Collectors.toList());
        return routeRepository.findAllByRouteIdIn(routeIds).stream()
                .collect(Collectors.toMap(Route::getRouteId, Function.identity()));
    }

    private List<DepartureDto> buildDepartureList(List<StopTime> stopTimes, 
                                                  Map<String, Trip> tripsById, 
                                                  Map<String, Route> routesById) {
        LocalTime now = LocalTime.now();
        return stopTimes.stream()
                .filter(st -> isFutureDeparture(st, now))
                .map(stopTime -> buildDepartureDto(stopTime, tripsById, routesById))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(DepartureDto::getDepartureTime))
                .limit(20)
                .collect(Collectors.toList());
    }

    private DepartureDto buildDepartureDto(StopTime stopTime, 
                                           Map<String, Trip> tripsById, 
                                           Map<String, Route> routesById) {
        Trip trip = tripsById.get(stopTime.getTripId());
        if (trip == null) {
            return null;
        }
        Route route = routesById.get(trip.getRouteId());
        if (route == null) {
            return null;
        }
        return new DepartureDto(route.getRouteShortName(), 
                                trip.getTripHeadsign(), 
                                stopTime.getDepartureTime());
    }

    private boolean isFutureDeparture(StopTime stopTime, LocalTime now) {
        try {
            String departureTimeStr = stopTime.getDepartureTime();
            int hour = Integer.parseInt(departureTimeStr.substring(0, 2));
            if (hour >= 24) {
                return true;
            }
            return LocalTime.parse(departureTimeStr, GTFS_TIME_FORMATTER).isAfter(now);
        } catch (Exception e) {
            return false;
        }
    }
}
