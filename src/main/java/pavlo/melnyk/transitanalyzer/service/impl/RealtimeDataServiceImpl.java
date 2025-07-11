package pavlo.melnyk.transitanalyzer.service.impl;

import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pavlo.melnyk.transitanalyzer.service.RealtimeDataService;

@Service
@Slf4j
public class RealtimeDataServiceImpl implements RealtimeDataService {

    private static final String TRIP_UPDATES_URL = "https://gtfs.ztp.krakow.pl/TripUpdates_T.pb";

    @Override
    @Cacheable("activeTripIds")
    public Set<String> getActiveTripIds() {
        log.info("Fetching and caching active trip IDs from {}", TRIP_UPDATES_URL);
        try (InputStream inputStream = new URL(TRIP_UPDATES_URL).openStream()) {
            FeedMessage feedMessage = FeedMessage.parseFrom(inputStream);
            return feedMessage.getEntityList().stream()
                    .filter(feedEntity
                            -> feedEntity.hasTripUpdate() && feedEntity.getTripUpdate().hasTrip())
                    .map(feedEntity
                            -> feedEntity.getTripUpdate().getTrip().getTripId())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Failed to fetch or parse trip updates from URL: {}", TRIP_UPDATES_URL, e);
            return Collections.emptySet();
        }
    }
}
