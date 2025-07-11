package pavlo.melnyk.transitanalyzer.repository;

import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pavlo.melnyk.transitanalyzer.entity.Trip;

public interface TripRepository extends ElasticsearchRepository<Trip, String> {
    List<Trip> findAllByTripIdIn(List<String> tripIds);
}
