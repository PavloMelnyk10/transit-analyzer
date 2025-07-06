package pavlo.melnyk.transitanalyzer.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pavlo.melnyk.transitanalyzer.entity.Trip;

public interface TripRepository extends ElasticsearchRepository<Trip, String> {
}
