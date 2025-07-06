package pavlo.melnyk.transitanalyzer.service;

import static pavlo.melnyk.transitanalyzer.util.AppConstants.FIELD_LOCATION;
import static pavlo.melnyk.transitanalyzer.util.AppConstants.UNIT_KM;

import java.util.List;
import java.util.stream.Collectors;
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
import org.springframework.stereotype.Service;
import pavlo.melnyk.transitanalyzer.dto.StopDistanceDto;
import pavlo.melnyk.transitanalyzer.entity.Stop;
import pavlo.melnyk.transitanalyzer.repository.StopRepository;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final StopRepository stopRepository;

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
}
