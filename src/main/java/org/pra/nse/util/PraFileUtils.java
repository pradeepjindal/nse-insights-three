package org.pra.nse.util;


import org.pra.nse.ApCo;
import org.pra.nse.ProCo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

@Component
public class PraFileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PraFileUtils.class);

    public String getLatestFileNameFor(String fileDir, String filePrefix, String fileExt, int occurrence) {
        return getLatestFileNameFor(fileDir, filePrefix, fileExt, occurrence, LocalDate.now());
    }
    public String getLatestFileNameFor(String fileDir, String filePrefix, String fileExt, int occurrence, LocalDate localDate) {
        LocalDate date = localDate;
        File file;
        String fileName;
        String filePathWithFileName = null;
        for(int i=0; i<occurrence; i++) {
            do {
                fileName = filePrefix + ApCo.PRA_DTF.format(date) + fileExt;
                //LOGGER.info("getLatestFileName | fileName: {}", fileName);
                filePathWithFileName = fileDir + File.separator + fileName;
                //LOGGER.info("getLatestFileName | filePathWithFileName: {}", filePathWithFileName);
                date = date.minusDays(1);
                file = new File(filePathWithFileName);
                if(file.exists()) break;
                filePathWithFileName = null;
                if(date.compareTo(ApCo.DOWNLOAD_FROM_DATE.minusDays(5)) < 0) break;
            } while(true);
        }
        return filePathWithFileName;
    }

    private String dateToString(LocalDate localDate) {
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                // case insensitive to parse JAN and FEB
                //.parseCaseInsensitive()
                // add pattern
                .appendPattern("yyyy-MM-dd")
                // create formatter (use English Locale to parse month names)
                .toFormatter(Locale.ENGLISH);
        return LocalDate.parse(localDate.toString(), dtf).toString();
    }

    public String validateDownload() {
        String cmDate = getLatestFileNameFor(ApCo.CM_FILES_PATH, ApCo.PRA_CM_FILE_PREFIX, ApCo.PRA_DATA_FILE_EXT, 1);
        cmDate = ProCo.extractDate(cmDate);

        String foDate = getLatestFileNameFor(ApCo.FM_FILES_PATH, ApCo.PRA_FM_FILE_PREFIX, ApCo.PRA_DATA_FILE_EXT, 1);
        foDate = ProCo.extractDate(foDate);

        String mtDate = getLatestFileNameFor(ApCo.DM_FILES_PATH, ApCo.PRA_DM_FILE_PREFIX, ApCo.PRA_DATA_FILE_EXT, 1);
        mtDate = ProCo.extractDate(mtDate);

        if(cmDate.equals(foDate) && foDate.equals(mtDate)) {
            return cmDate;
        } else {
            LOGGER.warn("Not All files are available: fo=[fo-{}], cm=[cm-{}], mt=[mt-{}]", foDate, cmDate, mtDate);
            LOGGER.info("fo=[fo-{}]", foDate);
            LOGGER.info("cm=[cm-{}]", cmDate);
            LOGGER.info("mt=[mt-{}]", mtDate);
            //throw new RuntimeException("All Files Does Not Exist: ABORTING");
            return null;
        }
    }
}
