package pavlo.melnyk.transitanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job processStopsJob;
    private final Job processRoutesJob;

    @PostMapping("/import-stops")
    public String importStopsJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(processStopsJob, jobParameters);
        } catch (Exception e) {
            return "Error starting job: " + e.getMessage();
        }
        return "Job 'processStopsJob' has been started";
    }

    @PostMapping("/import-routes")
    public String importRoutesJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(processRoutesJob, jobParameters);
        } catch (Exception e) {
            return "Error starting job: " + e.getMessage();
        }
        return "Job 'processRoutesJob' has been started";
    }
}
