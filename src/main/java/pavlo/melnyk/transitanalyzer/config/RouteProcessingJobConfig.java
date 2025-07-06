package pavlo.melnyk.transitanalyzer.config;

import static pavlo.melnyk.transitanalyzer.util.AppConstants.GTFS_SHAPES_PATH;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import pavlo.melnyk.transitanalyzer.batch.RouteShapeProcessor;
import pavlo.melnyk.transitanalyzer.dto.ShapePointDto;
import pavlo.melnyk.transitanalyzer.entity.RouteShape;
import pavlo.melnyk.transitanalyzer.repository.RouteShapeRepository;

@Configuration
@RequiredArgsConstructor
public class RouteProcessingJobConfig {

    private final RouteShapeRepository routeShapeRepository;
    private final RouteShapeProcessor routeShapeProcessor;

    @Bean
    public FlatFileItemReader<ShapePointDto> shapePointItemReader() {
        return new FlatFileItemReaderBuilder<ShapePointDto>()
                .name("shapePointItemReader")
                .resource(new ClassPathResource(GTFS_SHAPES_PATH))
                .linesToSkip(1)
                .delimited()
                .names("shape_id", "shape_pt_lat", "shape_pt_lon",
                        "shape_pt_sequence", "shape_dist_traveled")
                .fieldSetMapper(fieldSet -> {
                    ShapePointDto dto = new ShapePointDto();
                    dto.setShapeId(fieldSet.readString("shape_id"));
                    dto.setShapePtLat(fieldSet.readDouble("shape_pt_lat"));
                    dto.setShapePtLon(fieldSet.readDouble("shape_pt_lon"));
                    dto.setShapePtSequence(fieldSet.readInt("shape_pt_sequence"));

                    String distTraveled = fieldSet.readString("shape_dist_traveled");
                    if (!distTraveled.isEmpty()) {
                        dto.setShapeDistTraveled(Double.parseDouble(distTraveled));
                    }
                    return dto;
                })
                .build();
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
