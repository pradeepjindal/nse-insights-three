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
import java.time.LocalDate;
import java.util.List;

@Component
public class FmDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FmDownloader.class);

    private final NseFileUtils nseFileUtils;
    private final PraNameUtils praNameUtils;
    private final DownloadUtils downloadFile;

    public FmDownloader(NseFileUtils nseFileUtils, PraNameUtils praNameUtils, DownloadUtils downloadFile) {
        this.nseFileUtils = nseFileUtils;
        this.praNameUtils = praNameUtils;
        this.downloadFile = downloadFile;
    }

    public void download(LocalDate fromDate) {
        String dataDir = ApCo.BASE_DATA_DIR + File.separator + ApCo.FM_DIR_NAME;
        List<String> filesDownloadUrl = prepareFileUrls(fromDate);

        filesDownloadUrl.stream().forEach( fileUrl -> {
            downloadFile.downloadFile(fileUrl, dataDir,
                    () -> (dataDir + File.separator + fileUrl.substring(65, 88)),
                    zipFilePathAndName -> {
                        try {
                            nseFileUtils.unzipNew(zipFilePathAndName, ApCo.PRA_FM_FILE_PREFIX);
                        } catch (IOException e) {
                            LOGGER.warn("Error while downloading file: {}", e);
                        }
                    });
        });
    }

    public void downloadFromLast() {
        String dataDir = ApCo.BASE_DATA_DIR + File.separator + ApCo.FM_DIR_NAME;
        String str = praNameUtils.getLatestFileNameFor(dataDir, ApCo.PRA_FM_FILE_PREFIX, ApCo.PRA_DATA_FILE_EXT, 1);
        LocalDate dt = DateUtils.getLocalDateFromPath(str);
        List<String> filesDownloadUrl = prepareFileUrls(dt.plusDays(1));

        filesDownloadUrl.stream().forEach( fileUrl -> {
            downloadFile.downloadFile(fileUrl, dataDir,
                    () -> (dataDir + File.separator + fileUrl.substring(65, 88)),
                    zipFilePathAndName -> {
                        try {
                            nseFileUtils.unzipNew(zipFilePathAndName, ApCo.PRA_FM_FILE_PREFIX);
                        } catch (IOException e) {
                            LOGGER.warn("Error while downloading file: {}", e);
                        }
                    });
        });
    }

    private List<String> prepareFileUrls(LocalDate fromDate) {
        String dataDir = ApCo.BASE_DATA_DIR + File.separator + ApCo.FM_DIR_NAME;
        List<String> filesToBeDownloaded = nseFileUtils.constructFileNames(
                fromDate,
                ApCo.NSE_FM_FILE_NAME_DATE_FORMAT,
                ApCo.NSE_FM_FILE_PREFIX,
                ApCo.NSE_FO_FILE_SUFFIX);
        filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(dataDir, null, null));
        return nseFileUtils.constructFileDownloadUrlWithYearAndMonth(ApCo.FO_BASE_URL, filesToBeDownloaded);
    }
}