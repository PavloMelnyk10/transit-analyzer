package pavlo.melnyk.transitanalyzer.config;

import static pavlo.melnyk.transitanalyzer.util.AppConstants.GTFS_STOP_TIMES_PATH;

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
import pavlo.melnyk.transitanalyzer.dto.batch.StopTimeDto;
import pavlo.melnyk.transitanalyzer.entity.StopTime;
import pavlo.melnyk.transitanalyzer.repository.StopTimeRepository;

@Configuration
@RequiredArgsConstructor
public class StopTimeProcessingJobConfig {

    private final StopTimeRepository stopTimeRepository;

    @Bean
    @StepScope
    public FlatFileItemReader<StopTimeDto> stopTimeItemReader() {
        return new FlatFileItemReaderBuilder<StopTimeDto>()
                .name("stopTimeItemReader")
                .resource(new FileSystemResource(GTFS_STOP_TIMES_PATH))
                .linesToSkip(1)
                .delimited()
                .names("tripId", "arrivalTime", "departureTime", "stopId",
                        "stopSequence", "stopHeadsign", "pickupType",
                        "dropOffType", "shapeDistTraveled", "timepoint")
                .targetType(StopTimeDto.class)
                .build();
    }

    @Bean
    public ItemProcessor<StopTimeDto, StopTime> stopTimeProcessor() {
        return dto -> new StopTime(
                dto.getTripId() + "-" + dto.getStopSequence(), // Generate unique ID
                dto.getTripId(),
                dto.getArrivalTime(),
                dto.getDepartureTime(),
                dto.getStopId(),
                dto.getStopSequence()
        );
    }

    @Bean
    public RepositoryItemWriter<StopTime> stopTimeItemWriter() {
        RepositoryItemWriter<StopTime> writer = new RepositoryItemWriter<>();
        writer.setRepository(stopTimeRepository);
        return writer;
    }

    @Bean
    public Step processStopTimesStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager) {
        return new StepBuilder("processStopTimesStep", jobRepository)
                .<StopTimeDto, StopTime>chunk(1000, transactionManager)
                .reader(stopTimeItemReader())
                .processor(stopTimeProcessor())
                .writer(stopTimeItemWriter())
                .build();
    }

    @Bean
    public Job processStopTimesJob(JobRepository jobRepository, Step processStopTimesStep) {
        return new JobBuilder("processStopTimesJob", jobRepository)
                .flow(processStopTimesStep)
                .end()
                .build();
    }
}
