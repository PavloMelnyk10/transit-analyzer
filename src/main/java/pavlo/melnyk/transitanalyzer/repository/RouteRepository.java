package pavlo.melnyk.transitanalyzer.repository;

import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pavlo.melnyk.transitanalyzer.entity.Route;

public interface RouteRepository extends ElasticsearchRepository<Route, String> {
    List<Route> findAllByRouteIdIn(List<String> routeIds);
}
