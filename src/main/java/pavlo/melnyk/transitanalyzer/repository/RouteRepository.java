package pavlo.melnyk.transitanalyzer.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pavlo.melnyk.transitanalyzer.entity.Route;

public interface RouteRepository extends ElasticsearchRepository<Route, String> {
}
