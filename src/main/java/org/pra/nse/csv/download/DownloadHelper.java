package org.pra.nse.csv.download;

import org.pra.nse.ApCo;
import org.pra.nse.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class DownloadHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadHelper.class);

    public boolean shouldDownload(List<String> urlListToBeDownloaded) {
        if(urlListToBeDownloaded.size() == 1 && dayOrTimeIsNotValid()) {
            LOGGER.info("skipping file | TIME: {} | {}", LocalTime.now(), urlListToBeDownloaded.get(0));
            return false;
        }
        return true;
    }

    private boolean dayOrTimeIsNotValid() {
        return DateUtils.isItWeekend(LocalDate.now()) || LocalTime.now().isBefore(ApCo.DAILY_DOWNLOAD_TIME);
    }
}
