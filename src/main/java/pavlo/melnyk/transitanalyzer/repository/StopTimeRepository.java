package pavlo.melnyk.transitanalyzer.repository;

import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pavlo.melnyk.transitanalyzer.entity.StopTime;

public interface StopTimeRepository extends ElasticsearchRepository<StopTime, String> {
    List<StopTime> findByStopId(String stopId);
}
