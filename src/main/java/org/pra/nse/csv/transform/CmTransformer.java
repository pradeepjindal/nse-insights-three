package org.pra.nse.csv.transform;

import org.pra.nse.ApCo;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CmTransformer extends BaseTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmTransformer .class);

    public CmTransformer(TransformHelper transformHelper, NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
        super(transformHelper, nseFileUtils, praFileUtils);
    }


    public void transformFromDate() {
        transformFromDate(ApCo.DOWNLOAD_FROM_DATE);
    }
    public void transformFromDate(LocalDate fromDate) {
        Map<String, String> filePairMap = prepare(fromDate);
        looper(filePairMap);
    }

    public void transformFromLastDate() {
        String str = praFileUtils.getLatestFileNameFor(Data_Dir, ApCo.PRA_CM_FILE_PREFIX, ApCo.PRA_DATA_FILE_EXT, 1);
        LocalDate dateOfLatestFile = DateUtils.getLocalDateFromPath(str, ApCo.PRA_FILE_NAME_DATE_REGEX);
        Map<String, String> filePairMap = prepare(dateOfLatestFile);
        looper(filePairMap);
    }


    private Map<String, String> prepare(LocalDate fromDate) {
        List<String> fileNames = nseFileUtils.constructFileNames(
                fromDate,
                ApCo.NSE_CM_FILE_NAME_DATE_FORMAT,
                ApCo.NSE_CM_FILE_PREFIX,
                ApCo.NSE_CM_FILE_SUFFIX + ApCo.NSE_CM_FILE_EXT);
        //filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(dataDir, null, null));
        //
        Map<String, String> filePairMap = new HashMap<>();
        fileNames.forEach( nseFileName -> {
            LOGGER.info("{}", nseFileName);
            //DateUtils.extractDate(fileName, ApCo.NSE_CM_FILE_NAME_DATE_REGEX, ApCo.NSE_CM_FILE_NAME_DATE_FORMAT);
            LocalDate localDate = DateUtils.getLocalDateFromPath(nseFileName, ApCo.NSE_CM_FILE_NAME_DATE_REGEX, ApCo.NSE_CM_FILE_NAME_DATE_FORMAT);
            String praFileName = ApCo.PRA_CM_FILE_PREFIX + localDate.toString() + ApCo.PRA_DATA_FILE_EXT;
            filePairMap.put(nseFileName, praFileName);
        });
        return filePairMap;
    }

    private void looper(Map<String, String> filePairMap) {
        filePairMap.forEach( (nseFileName, praFileName) -> {
            transformHelper.transform(Data_Dir, ApCo.PRA_CM_FILE_PREFIX, nseFileName, praFileName);
        });
    }

//    private void transform(String nseFileName, String praFileName) {
//        String source = Data_Dir + File.separator + nseFileName;
//        String target = Data_Dir + File.separator + praFileName;
//        if(nseFileUtils.isFileExist(target)) {
//            LOGGER.info("file exists - {}", target);
//        } else if (nseFileUtils.isFileExist(source)) {
//            try {
//                nseFileUtils.unzipNew(source, ApCo.PRA_CM_FILE_PREFIX);
//                LOGGER.info("file transformed - {}", target);
//            } catch (FileNotFoundException fnfe) {
//                LOGGER.info("file not found - {}", source);
//            } catch (IOException e) {
//                LOGGER.warn("Error while unzipping file: {}", e);
//            }
//        }
//    }

}
