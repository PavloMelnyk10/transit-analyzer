package pavlo.melnyk.transitanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "pavlo.melnyk.transitanalyzer.repository")
@EnableScheduling
public class TransitAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransitAnalyzerApplication.class, args);
    }
}
