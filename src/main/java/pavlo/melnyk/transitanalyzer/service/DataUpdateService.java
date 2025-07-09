package pavlo.melnyk.transitanalyzer.service;

public interface DataUpdateService {

    void init();

    void scheduledUpdate();

    void updateAndProcessData();
}
