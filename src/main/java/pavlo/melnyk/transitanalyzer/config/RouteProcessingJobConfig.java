package pavlo.melnyk.transitanalyzer.config;

import static pavlo.melnyk.transitanalyzer.util.AppConstants.GTFS_SHAPES_PATH;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import pavlo.melnyk.transitanalyzer.batch.RouteShapeProcessor;
import pavlo.melnyk.transitanalyzer.dto.batch.ShapePointDto;
import pavlo.melnyk.transitanalyzer.entity.RouteShape;
import pavlo.melnyk.transitanalyzer.repository.RouteShapeRepository;

@Configuration
@RequiredArgsConstructor
public class RouteProcessingJobConfig {

    private final RouteShapeRepository routeShapeRepository;
    private final RouteShapeProcessor routeShapeProcessor;

    @Bean
    @StepScope
    public FlatFileItemReader<ShapePointDto> shapePointItemReader() {
        FlatFileItemReader<ShapePointDto> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(GTFS_SHAPES_PATH));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames("shape_id", "shape_pt_lat", "shape_pt_lon",
                                "shape_pt_sequence", "shape_dist_traveled");
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(ShapePointDto.class);
                    }
                });
            }
        });
        return reader;
    }

    @Bean
    public Tasklet processShapesTasklet(FlatFileItemReader<ShapePointDto> shapePointItemReader) {
        return (contribution, chunkContext) -> {
            List<ShapePointDto> allPoints = new ArrayList<>();
            shapePointItemReader.open(chunkContext.getStepContext()
                    .getStepExecution().getExecutionContext());
            ShapePointDto point;
            while ((point = shapePointItemReader.read()) != null) {
                allPoints.add(point);
            }
            shapePointItemReader.close();

            Map<String, List<ShapePointDto>> pointsByShapeId = allPoints.stream()
                    .collect(Collectors.groupingBy(ShapePointDto::getShapeId));

            List<RouteShape> routeShapes = pointsByShapeId.entrySet().stream()
                    .map(entry -> routeShapeProcessor.process(entry.getKey(), entry.getValue()))
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());

            routeShapeRepository.saveAll(routeShapes);

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step processShapesStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  Tasklet processShapesTasklet) {
        return new StepBuilder("processShapesStep", jobRepository)
                .tasklet(processShapesTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job processRoutesJob(JobRepository jobRepository, Step processShapesStep) {
        return new JobBuilder("processRoutesJob", jobRepository)
                .flow(processShapesStep)
                .end()
                .build();
    }
}
