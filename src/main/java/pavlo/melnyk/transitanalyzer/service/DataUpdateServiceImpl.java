package pavlo.melnyk.transitanalyzer.service;

import static pavlo.melnyk.transitanalyzer.util.AppConstants.GTFS_DIR;
import static pavlo.melnyk.transitanalyzer.util.AppConstants.GTFS_STOPS_PATH;
import static pavlo.melnyk.transitanalyzer.util.AppConstants.GTFS_URL;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataUpdateServiceImpl implements DataUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(DataUpdateServiceImpl.class);

    private final JobLauncher jobLauncher;
    private final Job processStopsJob;
    private final Job processRoutesJob;
    private final Job processRouteInfoJob;
    private final Job processTripsJob;
    private final Job processStopTimesJob;

    @PostConstruct
    @Override
    public void init() {
        logger.info("Checking for GTFS data on startup...");
        File gtfsDir = new File(GTFS_DIR);
        File stopsFile = new File(GTFS_STOPS_PATH);

        if (!gtfsDir.exists() || !stopsFile.exists()) {
            logger.info("GTFS data not found. Starting initial download and processing.");
            updateAndProcessData();
        } else {
            logger.info("GTFS data already exists. Skipping initial download.");
        }
    }

    @Scheduled(cron = "0 0 0 */3 * *")
    @Override
    public void scheduledUpdate() {
        logger.info("Starting scheduled GTFS data update...");
        updateAndProcessData();
    }

    @Override
    public void updateAndProcessData() {
        try {
            Path gtfsDirPath = Paths.get(GTFS_DIR);
            if (!Files.exists(gtfsDirPath)) {
                Files.createDirectories(gtfsDirPath);
                logger.info("Created GTFS directory at: {}", gtfsDirPath.toAbsolutePath());
            }

            Path downloadPath = Paths.get(GTFS_DIR + "GTFS_KRK_T.zip");
            downloadFile(new URL(GTFS_URL), downloadPath);
            unzip(downloadPath, Paths.get(GTFS_DIR));
            Files.delete(downloadPath);
            logger.info("GTFS data update completed successfully.");

            runBatchJobs();
        } catch (Exception e) {
            logger.error("Failed to update and process GTFS data", e);
        }
    }

    private void downloadFile(URL url, Path targetPath) throws IOException {
        logger.info("Downloading data from {}", url);
        try (InputStream in = url.openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        logger.info("Downloaded data to {}", targetPath);
    }

    private void unzip(Path zipFilePath, Path destDir) throws IOException {
        logger.info("Unzipping {} to {}", zipFilePath, destDir);
        File destDirFile = destDir.toFile();
        if (!destDirFile.exists()) {
            destDirFile.mkdirs();
        }
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDirFile, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        logger.info("Unzipping completed.");
    }

    private void runBatchJobs() {
        logger.info("Starting batch jobs to process new GTFS data.");
        try {
            runJob(processStopsJob, "processStopsJob");
            runJob(processRoutesJob, "processRoutesJob");
            runJob(processTripsJob, "processTripsJob");
            runJob(processStopTimesJob, "processStopTimesJob");
            runJob(processRouteInfoJob, "processRouteInfoJob");
            logger.info("All batch jobs completed successfully.");
        } catch (Exception e) {
            logger.error("An error occurred during batch job execution", e);
        }
    }

    private void runJob(Job job, String jobName) throws Exception {
        logger.info("Running job: {}", jobName);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(job, jobParameters);
        logger.info("Finished job: {}", jobName);
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
