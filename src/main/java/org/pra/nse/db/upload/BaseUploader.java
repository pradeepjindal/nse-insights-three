package org.pra.nse.db.upload;

import org.pra.nse.ApCo;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;

public abstract class BaseUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUploader.class);

    private final PraFileUtils praFileUtils;

    private final String fileDirName;
    private final String filePrefix;

    protected BaseUploader(PraFileUtils praFileUtils, String fileDirName, String filePrefix) {
        this.praFileUtils = praFileUtils;
        this.fileDirName = fileDirName;
        this.filePrefix = filePrefix;
    }


    public void uploadFromDate() {
        uploadFromDate(ApCo.DOWNLOAD_FROM_DATE);
    }
    public void uploadFromDate(LocalDate fromDate) {
        looper(fromDate);
    }

    public void uploadFromLast() {
        String dataDir = ApCo.BASE_DATA_DIR + File.separator + fileDirName;
        String str = praFileUtils.getLatestFileNameFor(dataDir, filePrefix, ApCo.PRA_DATA_FILE_EXT, 1);
        LocalDate dt = str == null ? LocalDate.now() : DateUtils.getLocalDateFromPath(str);

        looper(dt);
    }


    abstract void upload(LocalDate forDate);


    private void looper(LocalDate fromDate) {
        LocalDate today = LocalDate.now();
        LocalDate processingDate = fromDate.minusDays(1);
        do {
            processingDate = processingDate.plusDays(1);
            LOGGER.info("upload processing date: [{}], {}", processingDate, processingDate.getDayOfWeek());
            if("SATURDAY".equals(processingDate.getDayOfWeek().name()) || "SUNDAY".equals(processingDate.getDayOfWeek().name())) {
                // TODO | Deepawali, sunday trading - logic to be incorporated
                continue;
            } else {
                upload(processingDate);
            }
        } while(today.compareTo(processingDate) > 0);
    }
}
