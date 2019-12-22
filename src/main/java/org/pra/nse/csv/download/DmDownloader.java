package org.pra.nse.csv.download;

import org.pra.nse.ApCo;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.DownloadUtils;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
public class DmDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DmDownloader.class);

    private final NseFileUtils nseFileUtils;
    private final PraNameUtils praNameUtils;
    private final DownloadUtils downloadFile;

    public DmDownloader(NseFileUtils nseFileUtils, PraNameUtils praNameUtils, DownloadUtils downloadFile) {
        this.nseFileUtils = nseFileUtils;
        this.praNameUtils = praNameUtils;
        this.downloadFile = downloadFile;
    }

    public void download(LocalDate fromDate) {
        String dataDir = ApCo.BASE_DATA_DIR + File.separator + ApCo.DM_DIR_NAME;
        List<String> filesDownloadUrl = prepareFileUrls(fromDate);

        filesDownloadUrl.stream().forEach( fileUrl -> {
            downloadFile.downloadFile(fileUrl, dataDir,
                    () -> (dataDir + File.separator + fileUrl.substring(ApCo.DM_BASE_URL.length()+1, 63)),
                    downloadedFilePathAndName -> {
                        transformToCsvNew(downloadedFilePathAndName);
                    }
            );
        });
    }

    public void downloadFromLast() {
        String dataDir = ApCo.BASE_DATA_DIR + File.separator + ApCo.DM_DIR_NAME;
        String str = praNameUtils.getLatestFileNameFor(dataDir, ApCo.PRA_DM_FILE_PREFIX, ApCo.PRA_DATA_FILE_EXT, 1);
        LocalDate dt = DateUtils.getLocalDateFromPath(str);
        List<String> filesDownloadUrl = prepareFileUrls(dt.plusDays(1));

        filesDownloadUrl.stream().forEach( fileUrl -> {
            downloadFile.downloadFile(fileUrl, dataDir,
                    () -> (dataDir + File.separator + fileUrl.substring(ApCo.DM_BASE_URL.length()+1, 63)),
                    downloadedFilePathAndName -> {
                        transformToCsvNew(downloadedFilePathAndName);
                    }
            );
        });
    }

    private List<String> prepareFileUrls(LocalDate downloadFromDate) {
        String dataDir = ApCo.BASE_DATA_DIR + File.separator + ApCo.DM_DIR_NAME;
        List<String> filesToBeDownloaded = nseFileUtils.constructFileNames(
                downloadFromDate,
                ApCo.NSE_DM_FILE_NAME_DATE_FORMAT,
                ApCo.NSE_DM_FILE_PREFIX,
                ApCo.NSE_DM_FILE_EXT);
        filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(dataDir, null, null));
        return nseFileUtils.constructFileDownloadUrl(ApCo.DM_BASE_URL, filesToBeDownloaded);
    }

    private void transformToCsvNew(String downloadedDirAndFileName) {
        int firstIndex = downloadedDirAndFileName.lastIndexOf("_");
        String tradeDate = DateUtils.transformDate(downloadedDirAndFileName.substring(firstIndex+1, firstIndex+9));
        String csvFileName = ApCo.PRA_DM_FILE_PREFIX
                            + DateUtils.transformDate(downloadedDirAndFileName.substring(firstIndex+1, firstIndex+9))
                            + ApCo.PRA_DATA_FILE_EXT;
        String toFile = ApCo.BASE_DATA_DIR + File.separator + ApCo.DM_DIR_NAME + File.separator + csvFileName;
        AtomicInteger atomicInteger = new AtomicInteger();
        File csvOutputFile = new File(toFile);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            try (Stream<String> stream = Files.lines(Paths.get(downloadedDirAndFileName))) {
                stream.filter(line-> atomicInteger.incrementAndGet() > 3)
                        .map(row -> {
                            if(atomicInteger.get() == 4) {
                                return "RecType,SrNo,Symbol,SecurityType,TradedQty,DeliverableQty,DeliveryToTradeRatio,TradeDate";
                            } else {
                                return row + "," + tradeDate;
                            }
                        }).forEach(pw::println);
            } catch (IOException e) {
                LOGGER.warn("Error in MAT entry: {}", e);
            }
        } catch (FileNotFoundException e) {
            LOGGER.warn("Error: {}", e);
        }
    }

}
