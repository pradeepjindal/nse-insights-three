package org.pra.nse.scheduler;

import org.pra.nse.csv.download.DownloadManager;
import org.pra.nse.csv.transform.TransformManager;
import org.pra.nse.db.upload.UploadManager;
import org.pra.nse.processor.ReportManager;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class DailyNseJobScheduler implements SchedulingConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyNseJobScheduler.class);

    private final int POOL_SIZE = 4;

    private final PraFileUtils praFileUtils;

    private final DownloadManager downloadManager;
    private final TransformManager transformManager;
    private final UploadManager uploadManager;
    private final ReportManager reportManager;

    private TaskScheduler taskScheduler;
    private ScheduledFuture<?> dailyJob;

    public DailyNseJobScheduler(PraFileUtils praFileUtils,
                                DownloadManager downloadManager,
                                TransformManager transformManager,
                                UploadManager uploadManager,
                                ReportManager reportManager) {
        this.praFileUtils = praFileUtils;
        this.downloadManager = downloadManager;
        this.transformManager = transformManager;
        this.uploadManager = uploadManager;
        this.reportManager = reportManager;
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("pra-scheduler-");
        threadPoolTaskScheduler.initialize();

        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);

        assignJobToScheduler(threadPoolTaskScheduler); // Assign the dailyJob to the scheduler

        // this will be used in later part of the article during refreshing the cron expression dynamically
        this.taskScheduler = threadPoolTaskScheduler;
    }

    private void assignJobToScheduler(TaskScheduler scheduler) {
        dailyJob = scheduler.schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.info("cron executed at "+ new Date());
                        try {
                            downloadManager.download();
                            transformManager.transform();
                            uploadManager.upload();
                            if(praFileUtils.validateDownload()) {
                                reportManager.report();
                            }
                        } catch(Exception e) {
                            LOGGER.error("ERROR: {}", e);
                        }
                    }
                }, new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        //https://stackoverflow.com/questions/30887822/spring-cron-vs-normal-cron
                        //"0 0 18 * * MON-FRI" means every weekday at 6:00 PM.
                        //String cronExp="0 0/1 * * * *"; //Can be pulled from a db . This will run every minute
                        //String cronExp="0 32 10 * * *";
                        //String cronExp="0 0 19 * * *"; // daily at 6pm
                        String cronExp="0 0/30 * * * *"; // daily at 6pm
                    return new CronTrigger(cronExp).nextExecutionTime(triggerContext);
                    }
                }
        );
        LOGGER.info("job assigned - nse downloader, uploader, processor and emailer");
    }

}
