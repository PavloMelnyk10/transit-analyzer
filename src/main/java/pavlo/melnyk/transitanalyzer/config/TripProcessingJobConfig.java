package pavlo.melnyk.transitanalyzer.config;

import static pavlo.melnyk.transitanalyzer.util.AppConstants.GTFS_TRIPS_PATH;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import pavlo.melnyk.transitanalyzer.dto.TripDto;
import pavlo.melnyk.transitanalyzer.entity.Trip;
import pavlo.melnyk.transitanalyzer.repository.TripRepository;

@Configuration
@RequiredArgsConstructor
public class TripProcessingJobConfig {

    private final TripRepository tripRepository;

    @Bean
    @StepScope
    public FlatFileItemReader<TripDto> tripItemReader() {
        return new FlatFileItemReaderBuilder<TripDto>()
                .name("tripItemReader")
                .resource(new FileSystemResource(GTFS_TRIPS_PATH))
                .linesToSkip(1)
                .delimited()
                .names("tripId", "routeId", "serviceId", "tripHeadsign", "tripShortName",
                        "directionId", "blockId", "shapeId", "wheelchairAccessible")
                .targetType(TripDto.class)
                .build();
    }

    @Bean
    public ItemProcessor<TripDto, Trip> tripProcessor() {
        return dto -> new Trip(
                dto.getTripId(),
                dto.getRouteId(),
                dto.getServiceId(),
                dto.getTripHeadsign(),
                dto.getDirectionId(),
                dto.getShapeId()
        );
    }

    @Bean
    public RepositoryItemWriter<Trip> tripItemWriter() {
        RepositoryItemWriter<Trip> writer = new RepositoryItemWriter<>();
        writer.setRepository(tripRepository);
        return writer;
    }

    @Bean
    public Step processTripsStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager) {
        return new StepBuilder("processTripsStep", jobRepository)
                .<TripDto, Trip>chunk(1000, transactionManager)
                .reader(tripItemReader())
                .processor(tripProcessor())
                .writer(tripItemWriter())
                .build();
    }

    @Bean
    public Job processTripsJob(JobRepository jobRepository, Step processTripsStep) {
        return new JobBuilder("processTripsJob", jobRepository)
                .flow(processTripsStep)
                .end()
                .build();
    }
}
