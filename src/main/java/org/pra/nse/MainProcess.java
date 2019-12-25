package org.pra.nse;

import org.pra.nse.csv.download.DownloadManager;
import org.pra.nse.db.upload.UploadManager;
import org.pra.nse.processor.*;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MainProcess implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainProcess.class);

    private final NseFileUtils nseFileUtils;
    private final PraNameUtils praNameUtils;

    private final DownloadManager downloadManager;
    private final UploadManager uploadManager;
    private final ReportManager reportManager;

    private final PradeepProcessor pradeepProcessor;
    private final PradeepProcessorB pradeepProcessorB;
    private final ManishProcessor manishProcessor;
    private final ManishProcessorB manishProcessorB;
    private final DeliverySpikeReport reportsProcessorC;

    public MainProcess(NseFileUtils nseFileUtils, PraNameUtils praNameUtils,
                       DownloadManager downloadManager, UploadManager uploadManager, ReportManager reportManager,
                       PradeepProcessor pradeepProcessor,
                       PradeepProcessorB pradeepProcessorB,
                       ManishProcessor manishProcessor,
                       ManishProcessorB manishProcessorB,
                       DeliverySpikeReport reportsProcessorC) {
        this.nseFileUtils = nseFileUtils;
        this.praNameUtils = praNameUtils;

        this.downloadManager = downloadManager;
        this.uploadManager = uploadManager;
        this.reportManager = reportManager;

        this.pradeepProcessor = pradeepProcessor;
        this.pradeepProcessorB = pradeepProcessorB;
        this.manishProcessor = manishProcessor;
        this.manishProcessorB = manishProcessorB;
        this.reportsProcessorC = reportsProcessorC;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("Main Process | ============================== | commencing");
        try {
            downloadManager.download(ApCo.DOWNLOAD_FROM_DATE);
            praNameUtils.validateDownload();
            uploadManager.upload(ApCo.DOWNLOAD_FROM_DATE);
            reportManager.report();
        } catch(Exception e) {
            LOGGER.error("ERROR: {}", e);
        }
        LOGGER.info(".");
        LOGGER.info("Main Process | ============================== | finishing");
    }
}
