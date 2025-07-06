package pavlo.melnyk.transitanalyzer.config;

import static pavlo.melnyk.transitanalyzer.util.AppConstants.GTFS_ROUTES_PATH;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import pavlo.melnyk.transitanalyzer.dto.RouteDto;
import pavlo.melnyk.transitanalyzer.entity.Route;
import pavlo.melnyk.transitanalyzer.repository.RouteRepository;

@Configuration
@RequiredArgsConstructor
public class RouteInfoProcessingJobConfig {

    private final RouteRepository routeRepository;

    @Bean
    public FlatFileItemReader<RouteDto> routeInfoItemReader() {
        return new FlatFileItemReaderBuilder<RouteDto>()
                .name("routeInfoItemReader")
                .resource(new ClassPathResource(GTFS_ROUTES_PATH))
                .delimited()
                .names("routeId", "agencyId", "routeShortName",
                        "routeLongName", "routeDesc", "routeType",
                        "routeUrl", "routeColor", "routeTextColor")
                .linesToSkip(1)
                .targetType(RouteDto.class)
                .build();
    }

    @Bean
    public ItemProcessor<RouteDto, Route> routeInfoProcessor() {
        return dto -> new Route(
                dto.getRouteId(),
                dto.getRouteShortName(),
                dto.getRouteLongName(),
                dto.getRouteType()
        );
    }

    @Bean
    public RepositoryItemWriter<Route> routeInfoItemWriter() {
        RepositoryItemWriter<Route> writer = new RepositoryItemWriter<>();
        writer.setRepository(routeRepository);
        return writer;
    }

    @Bean
    public Step processRouteInfoStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager) {
        return new StepBuilder("processRouteInfoStep", jobRepository)
                .<RouteDto, Route>chunk(1000, transactionManager)
                .reader(routeInfoItemReader())
                .processor(routeInfoProcessor())
                .writer(routeInfoItemWriter())
                .build();
    }

    @Bean
    public Job processRouteInfoJob(JobRepository jobRepository, Step processRouteInfoStep) {
        return new JobBuilder("processRouteInfoJob", jobRepository)
                .flow(processRouteInfoStep)
                .end()
                .build();
    }
}
