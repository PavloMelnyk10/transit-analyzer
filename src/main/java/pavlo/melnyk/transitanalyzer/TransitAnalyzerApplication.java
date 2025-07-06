package pavlo.melnyk.transitanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "pavlo.melnyk.transitanalyzer.repository")
public class TransitAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransitAnalyzerApplication.class, args);
    }
}
