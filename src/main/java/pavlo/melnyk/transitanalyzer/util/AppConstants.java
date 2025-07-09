package pavlo.melnyk.transitanalyzer.util;

public final class AppConstants {
    public static final String FIELD_LOCATION = "location";

    public static final String UNIT_KM = "km";

    public static final String GTFS_URL = "https://gtfs.ztp.krakow.pl/GTFS_KRK_T.zip";
    public static final String GTFS_DIR = "src/main/resources/gtfs/";
    public static final String GTFS_STOPS_PATH = GTFS_DIR + "stops.txt";
    public static final String GTFS_SHAPES_PATH = GTFS_DIR + "shapes.txt";
    public static final String GTFS_ROUTES_PATH = GTFS_DIR + "routes.txt";
    public static final String GTFS_TRIPS_PATH = GTFS_DIR + "trips.txt";
    public static final String GTFS_STOP_TIMES_PATH = GTFS_DIR + "stop_times.txt";

    private AppConstants() {
    }
}
