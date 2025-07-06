package pavlo.melnyk.transitanalyzer.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import pavlo.melnyk.transitanalyzer.dto.StopDto;
import pavlo.melnyk.transitanalyzer.entity.Stop;

public class StopItemProcessor implements ItemProcessor<StopDto, Stop> {

    @Override
    public Stop process(StopDto item) {
        if (item == null) {
            return null;
        }
        String stopId = item.getStopId();
        String name = item.getStopName();
        GeoPoint location = new GeoPoint(item.getStopLat(), item.getStopLon());
        return new Stop(stopId, name, location);
    }
}
