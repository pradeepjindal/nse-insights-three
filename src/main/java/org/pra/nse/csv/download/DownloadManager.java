package org.pra.nse.csv.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DownloadManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadManager.class);

    private final CmDownloader cmDownloader;
    private final FmDownloader fmDownloader;
    private final DmDownloader dmDownloader;
    private final BpDownloader bpDownloader;

    public DownloadManager(CmDownloader cmDownloader,
                           FmDownloader fmDownloader,
                           DmDownloader dmDownloader,
                           BpDownloader bpDownloader) {
        this.cmDownloader = cmDownloader;
        this.fmDownloader = fmDownloader;
        this.dmDownloader = dmDownloader;
        this.bpDownloader = bpDownloader;
    }

    public void download() {
        LOGGER.info(".");
        LOGGER.info("____________________ Download Manager");

        cmDownloader.downloadFromLast();
        LOGGER.info("--------------------");
        fmDownloader.downloadFromLast();
        LOGGER.info("--------------------");
        dmDownloader.downloadFromLast();
        LOGGER.info("--------------------");
        bpDownloader.downloadFromLast();

//        cmDownloader.downloadFromDate();
//        LOGGER.info("--------------------");
//        fmDownloader.downloadFromDate();
//        LOGGER.info("--------------------");
//        dmDownloader.downloadFromDate();
//        LOGGER.info("--------------------");
//        bpDownloader.downloadFromDate();

        LOGGER.info("==================== Download Manager");
    }

}
