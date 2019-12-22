package org.pra.nse.db.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UploadManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadManager.class);

    private final CashMarketUploader cashMarketUploader;
    private final FutureMarketUploader futureMarketUploader;
    private final DeliveryMarketUploader deliveryMarketUploader;
    //private final BpDownloader bpDownloader;

    public UploadManager(CashMarketUploader cashMarketUploader,
                         FutureMarketUploader futureMarketUploader,
                         DeliveryMarketUploader deliveryMarketUploader) {
        this.cashMarketUploader = cashMarketUploader;
        this.futureMarketUploader = futureMarketUploader;
        this.deliveryMarketUploader = deliveryMarketUploader;
    }

    public void upload(LocalDate fromDate) {
        LOGGER.info(".");
        LOGGER.info("==================== Upload Manager");

//        cashMarketUploader.uploadFromDate(fromDate);
//        LOGGER.info("--------------------");
//        futureMarketUploader.uploadFromDate(fromDate);
//        LOGGER.info("--------------------");
//        deliveryMarketUploader.uploadFromDate(fromDate);

        cashMarketUploader.uploadFromLast();
        LOGGER.info("--------------------");
        futureMarketUploader.uploadFromLast();
        LOGGER.info("--------------------");
        deliveryMarketUploader.uploadFromLast();

        LOGGER.info("==================== Upload Manager");
    }


}
