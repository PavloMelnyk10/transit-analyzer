package pavlo.melnyk.transitanalyzer.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import pavlo.melnyk.transitanalyzer.entity.RouteShape;

@Repository
public interface RouteShapeRepository extends ElasticsearchRepository<RouteShape, String> {
}
