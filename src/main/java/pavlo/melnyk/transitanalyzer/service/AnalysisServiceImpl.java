package pavlo.melnyk.transitanalyzer.service;

import static pavlo.melnyk.transitanalyzer.util.AppConstants.FIELD_LOCATION;
import static pavlo.melnyk.transitanalyzer.util.AppConstants.UNIT_KM;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.GeoDistanceOrder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import pavlo.melnyk.transitanalyzer.dto.RouteInfoResponseDto;
import pavlo.melnyk.transitanalyzer.dto.StopDistanceDto;
import pavlo.melnyk.transitanalyzer.dto.StopSearchDto;
import pavlo.melnyk.transitanalyzer.entity.Route;
import pavlo.melnyk.transitanalyzer.entity.Stop;
import pavlo.melnyk.transitanalyzer.entity.StopTime;
import pavlo.melnyk.transitanalyzer.entity.Trip;
import pavlo.melnyk.transitanalyzer.repository.RouteRepository;
import pavlo.melnyk.transitanalyzer.repository.StopRepository;
import pavlo.melnyk.transitanalyzer.repository.StopTimeRepository;
import pavlo.melnyk.transitanalyzer.repository.TripRepository;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final StopRepository stopRepository;
    private final StopTimeRepository stopTimeRepository;
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;

    @Override
    public Page<StopDistanceDto> findStopsNear(String stopId,
                                               double distanceInKm,
                                               Pageable pageable) {
        Stop centerStop = stopRepository.findById(stopId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stop not found with id: " + stopId));
        GeoPoint centerLocation = centerStop.getLocation();

        Criteria criteria = new Criteria(FIELD_LOCATION)
                .within(centerLocation, distanceInKm + UNIT_KM);
        Query query = new CriteriaQuery(criteria);
        GeoDistanceOrder order = new GeoDistanceOrder(FIELD_LOCATION,
                centerLocation).withUnit(UNIT_KM);
        query.addSort(Sort.by(order));
        query.setPageable(pageable);
        SearchHits<Stop> hits = elasticsearchOperations.search(query, Stop.class);
        List<StopDistanceDto> dtoList = hits.getSearchHits().stream().map(hit -> {
            Stop stop = hit.getContent();
            Double distance = (Double) hit.getSortValues().getFirst();
            return new StopDistanceDto(
                    stop.getStopId(),
                    stop.getName(),
                    stop.getLocation(),
                    distance);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, hits.getTotalHits());
    }

    @Override
    public List<RouteInfoResponseDto> findRoutesByStop(String stopId) {
        List<StopTime> stopTimes = stopTimeRepository.findByStopId(stopId);
        if (stopTimes.isEmpty()) {
            return List.of();
        }

        Set<String> routeIds = getRouteIdsForStopTimes(stopTimes);
        Iterable<Route> routes = routeRepository.findAllById(routeIds);

        return StreamSupport.stream(routes.spliterator(), false)
                .map(this::buildRouteInfoResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StopSearchDto> searchStopsByName(String name) {
        Query query = new StringQuery("{\"match_phrase_prefix\":{\"stop_name\":\"" + name + "\"}}");
        SearchHits<Stop> searchHits = elasticsearchOperations.search(query, Stop.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> new StopSearchDto(
                        hit.getContent().getStopId(), hit.getContent().getName()))
                .collect(Collectors.toList());
    }

    private Set<String> getRouteIdsForStopTimes(List<StopTime> stopTimes) {
        Set<String> tripIds = stopTimes.stream()
                .map(StopTime::getTripId)
                .collect(Collectors.toSet());

        Iterable<Trip> trips = tripRepository.findAllById(tripIds);

        return StreamSupport.stream(trips.spliterator(), false)
                .map(Trip::getRouteId)
                .collect(Collectors.toSet());
    }

    private RouteInfoResponseDto buildRouteInfoResponseDto(Route route) {
        return new RouteInfoResponseDto(
                route.getRouteShortName(),
                route.getRouteLongName(),
                route.getRouteType());
    }
}
