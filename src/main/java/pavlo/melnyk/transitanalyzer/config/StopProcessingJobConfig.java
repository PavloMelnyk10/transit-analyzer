package pavlo.melnyk.transitanalyzer.config;

import static pavlo.melnyk.transitanalyzer.util.AppConstants.GTFS_STOPS_PATH;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import pavlo.melnyk.transitanalyzer.batch.StopItemProcessor;
import pavlo.melnyk.transitanalyzer.dto.batch.StopDto;
import pavlo.melnyk.transitanalyzer.entity.Stop;
import pavlo.melnyk.transitanalyzer.repository.StopRepository;

@Configuration
@RequiredArgsConstructor
public class StopProcessingJobConfig {

    private final StopRepository stopRepository;

    @Bean
    @StepScope
    public FlatFileItemReader<StopDto> stopItemReader() {
        return new FlatFileItemReaderBuilder<StopDto>()
                .name("stopItemReader")
                .resource(new FileSystemResource(GTFS_STOPS_PATH))
                .delimited()
                .names("stopId", "stopCode", "stopName", "stopDesc", "stopLat", "stopLon",
                        "zoneId", "stopUrl", "locationType", "parentStation", "stopTimezone",
                        "wheelchairBoarding", "platformCode")
                .linesToSkip(1)
                .targetType(StopDto.class)
                .build();
    }

    @Bean
    public ItemProcessor<StopDto, Stop> stopItemProcessor() {
        return new StopItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<Stop> stopItemWriter() {
        return new RepositoryItemWriterBuilder<Stop>()
                .repository(stopRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step processStopsStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 FlatFileItemReader<StopDto> stopItemReader,
                                 ItemProcessor<StopDto, Stop> stopItemProcessor,
                                 RepositoryItemWriter<Stop> stopItemWriter) {
        return new StepBuilder("processStopsStep", jobRepository)
                .<StopDto, Stop>chunk(1000, transactionManager)
                .reader(stopItemReader)
                .processor(stopItemProcessor)
                .writer(stopItemWriter)
                .build();
    }

    @Bean
    public Job processStopsJob(JobRepository jobRepository, Step processStopsStep) {
        return new JobBuilder("processStopsJob", jobRepository)
                .flow(processStopsStep)
                .end()
                .build();
    }
}
