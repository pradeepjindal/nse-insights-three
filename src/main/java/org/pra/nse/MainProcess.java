package org.pra.nse;

import org.pra.nse.csv.download.DownloadManager;
import org.pra.nse.csv.transform.TransformManager;
import org.pra.nse.db.upload.UploadManager;
import org.pra.nse.processor.*;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MainProcess implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainProcess.class);

    private final PraFileUtils praFileUtils;

    private final DownloadManager downloadManager;
    private final TransformManager transformManager;
    private final UploadManager uploadManager;
    private final ReportManager reportManager;

    public MainProcess(PraFileUtils praFileUtils,
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
    public void run(ApplicationArguments args) {
        LOGGER.info("Main Process | ============================== | commencing");
        try {
            downloadManager.download();
            transformManager.transform();
            uploadManager.upload();
            if(praFileUtils.validateDownload() != null) {
                reportManager.report();
            }
        } catch(Exception e) {
            LOGGER.error("ERROR: {}", e);
        }
        LOGGER.info(".");
        LOGGER.info("Main Process | ============================== | finishing");
    }

}
