package pavlo.melnyk.transitanalyzer.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import pavlo.melnyk.transitanalyzer.entity.Stop;

@Repository
public interface StopRepository extends ElasticsearchRepository<Stop, String> {
}
