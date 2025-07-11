package pavlo.melnyk.transitanalyzer.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pavlo.melnyk.transitanalyzer.dto.RouteDetailsDto;
import pavlo.melnyk.transitanalyzer.dto.StopOnRouteDto;
import pavlo.melnyk.transitanalyzer.entity.Route;
import pavlo.melnyk.transitanalyzer.entity.Stop;
import pavlo.melnyk.transitanalyzer.entity.StopTime;
import pavlo.melnyk.transitanalyzer.entity.Trip;
import pavlo.melnyk.transitanalyzer.repository.RouteRepository;
import pavlo.melnyk.transitanalyzer.repository.StopRepository;
import pavlo.melnyk.transitanalyzer.repository.StopTimeRepository;
import pavlo.melnyk.transitanalyzer.repository.TripRepository;
import pavlo.melnyk.transitanalyzer.service.RealtimeDataService;
import pavlo.melnyk.transitanalyzer.service.RouteService;

@Slf4j
@Service
@AllArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final RealtimeDataService realtimeDataService;
    private final StopRepository stopRepository;
    private final StopTimeRepository stopTimeRepository;

    @Override
    public Optional<RouteDetailsDto> getRouteDetails(
            String routeShortName, int directionId) {

        Optional<Route> routeOptional =
                routeRepository.findByRouteShortName(routeShortName).stream().findFirst();
        if (routeOptional.isEmpty()) {
            return Optional.empty();
        }
        Route route = routeOptional.get();

        List<Trip> allTrips = tripRepository
                .findAllByRouteIdAndDirectionId(route.getRouteId(), directionId);

        Set<String> activeTripIds = realtimeDataService.getActiveTripIds();
        List<Trip> trips = allTrips.stream()
                .filter(trip -> activeTripIds.contains(trip.getTripId()))
                .collect(Collectors.toList());

        if (trips.isEmpty()) {
            log.warn("No active trips found for routeShortName: {}."
                    + " Returning empty list of stops.", routeShortName);
            return Optional.of(new RouteDetailsDto(route.getRouteLongName(), List.of()));
        }

        Trip longestTrip = findLongestTrip(trips);
        if (longestTrip == null) {
            return Optional.of(new RouteDetailsDto(route.getRouteLongName(), List.of()));
        }

        List<StopTime> stopTimes = stopTimeRepository
                .findByTripIdOrderByStopSequenceAsc(longestTrip.getTripId());

        List<String> stopIds = stopTimes.stream()
                .map(StopTime::getStopId).collect(Collectors.toList());
        Map<String, Stop> stopsById = StreamSupport
                .stream(stopRepository.findAllById(stopIds).spliterator(), false)
                .collect(Collectors.toMap(Stop::getStopId, Function.identity()));

        List<StopOnRouteDto> stopsOnRoute = stopTimes.stream()
                .map(stopTime -> {
                    Stop stop = stopsById.get(stopTime.getStopId());
                    return new StopOnRouteDto(
                            stop.getStopId(),
                            stop.getName(),
                            stopTime.getStopSequence()
                    );
                })
                .collect(Collectors.toList());

        return Optional.of(new RouteDetailsDto(route.getRouteLongName(), stopsOnRoute));
    }

    private Trip findLongestTrip(List<Trip> trips) {
        Trip longestTrip = null;
        int maxSequence = -1;

        for (Trip trip : trips) {
            Optional<StopTime> lastStopTimeOpt =
                    stopTimeRepository.findFirstByTripIdOrderByStopSequenceDesc(trip.getTripId());
            if (lastStopTimeOpt.isPresent()) {
                StopTime lastStopTime = lastStopTimeOpt.get();
                if (lastStopTime.getStopSequence() > maxSequence) {
                    maxSequence = lastStopTime.getStopSequence();
                    longestTrip = trip;
                }
            }
        }
        return longestTrip;
    }
}
