package org.pra.nse.csv.download;

import org.pra.nse.ApCo;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@Component
public class CmDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmDownloader.class);

    private final String Data_Dir = ApCo.BASE_DATA_DIR + File.separator + ApCo.CM_DIR_NAME;

    private final DownloadHelper downloadHelper;

    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;


    public CmDownloader(NseFileUtils nseFileUtils, PraFileUtils praFileUtils, DownloadHelper downloadHelper) {
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.downloadHelper = downloadHelper;
    }


    public void downloadFromDate() {
        downloadFromDate(ApCo.DOWNLOAD_FROM_DATE);
    }
    public void downloadFromDate(LocalDate fromDate) {
        List<String> filesDownloadUrl = prepareFileUrls(fromDate);
        looper(filesDownloadUrl);
    }

    public void downloadFromLast() {
        String str = praFileUtils.getLatestFileNameFor(Data_Dir, ApCo.PRA_CM_FILE_PREFIX, ApCo.PRA_DATA_FILE_EXT, 1);
        LocalDate dateOfLatestFile = DateUtils.getLocalDateFromPath(str, ApCo.PRA_FILE_NAME_DATE_REGEX);
        List<String> filesDownloadUrls = prepareFileUrls(dateOfLatestFile.plusDays(1));
        if(filesDownloadUrls == null || filesDownloadUrls.size() == 0) return;

        LocalDate dateOfNextFile = DateUtils.getLocalDateFromPath(
                filesDownloadUrls.get(0),
                ApCo.NSE_CM_FILE_NAME_DATE_REGEX,
                ApCo.NSE_CM_FILE_NAME_DATE_FORMAT);
        if(filesDownloadUrls.size() == 1 && dateOfNextFile.isBefore(LocalDate.now())) {
            download(filesDownloadUrls.get(0));
        } else {
            looper(filesDownloadUrls);
        }
    }


    private List<String> prepareFileUrls(LocalDate downloadFromDate) {
        List<String> filesToBeDownloaded = nseFileUtils.constructFileNames(
                downloadFromDate,
                ApCo.NSE_CM_FILE_NAME_DATE_FORMAT,
                ApCo.NSE_CM_FILE_PREFIX,
                ApCo.NSE_CM_FILE_SUFFIX + ApCo.NSE_CM_FILE_EXT);
        filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(Data_Dir, null, null));
        return nseFileUtils.constructFileDownloadUrlWithYearAndMonth(ApCo.CM_BASE_URL, filesToBeDownloaded);
    }

    private void looper(List<String> urlListToBeDownloaded) {
        if(downloadHelper.shouldDownload(urlListToBeDownloaded)) {
            urlListToBeDownloaded.stream().forEach( fileUrl -> {
                download(fileUrl);
            });
        }
    }

    private void download(String fileUrl) {
        downloadHelper.downloadFile(fileUrl, Data_Dir,
                () -> (Data_Dir + File.separator + fileUrl.substring(62, 85)),
                zipFilePathAndName -> {
                    LOGGER.info("PASSING: unzipping file: {}", zipFilePathAndName);
                });
    }

}
