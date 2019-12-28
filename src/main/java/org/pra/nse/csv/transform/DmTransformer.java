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
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
public class DmTransformer extends BaseTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DmTransformer.class);

    private final String Data_Dir = ApCo.BASE_DATA_DIR + File.separator + ApCo.DM_DIR_NAME;


    public DmTransformer(TransformHelper transformHelper, NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
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
        String str = praFileUtils.getLatestFileNameFor(Data_Dir, ApCo.PRA_DM_FILE_PREFIX, ApCo.PRA_DATA_FILE_EXT, 1);
        LocalDate dateOfLatestFile = DateUtils.getLocalDateFromPath(str, ApCo.PRA_FILE_NAME_DATE_REGEX);
        Map<String, String> filePairMap = prepare(dateOfLatestFile);
        looper(filePairMap);
    }


    private Map<String, String> prepare(LocalDate fromDate) {
        List<String> fileNames = nseFileUtils.constructFileNames(
                fromDate,
                ApCo.NSE_DM_FILE_NAME_DATE_FORMAT,
                ApCo.NSE_DM_FILE_PREFIX,
                ApCo.NSE_DM_FILE_EXT);
        //filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(dataDir, null, null));
        //
        Map<String, String> filePairMap = new HashMap<>();
        fileNames.forEach( nseFileName -> {
            LOGGER.info("{}", nseFileName);
            //DateUtils.extractDate(fileName, ApCo.NSE_DM_FILE_NAME_DATE_REGEX, ApCo.NSE_DM_FILE_NAME_DATE_FORMAT);
            LocalDate localDate = DateUtils.getLocalDateFromPath(nseFileName, ApCo.NSE_DM_FILE_NAME_DATE_REGEX, ApCo.NSE_DM_FILE_NAME_DATE_FORMAT);
            String praFileName = ApCo.PRA_DM_FILE_PREFIX + localDate.toString() + ApCo.PRA_DATA_FILE_EXT;
            filePairMap.put(nseFileName, praFileName);
        });
        return filePairMap;
    }

    private void looper(Map<String, String> filePairMap) {
        filePairMap.forEach( (nseFileName, praFileName) -> {
            transform(nseFileName, praFileName);
        });
    }

    private void transform(String nseFileName, String praFileName) {
        String source = Data_Dir + File.separator + nseFileName;
        String target = Data_Dir + File.separator + praFileName;
        if(nseFileUtils.isFileExist(target)) {
            LOGGER.info("target exists - {}", target);
        } else if (nseFileUtils.isFileExist(source)) {
            try {
                transformToDmCsv(source);
                LOGGER.info("source transformed to - {}", target);
            } catch (Exception e) {
                LOGGER.warn("Error while transforming file: {} {}", source, e);
            }
        } else {
            LOGGER.info("source not found - {}", source);
        }
    }

    private void transformToDmCsv(String downloadedDirAndFileName) {
        int firstIndex = downloadedDirAndFileName.lastIndexOf("_");
        String tradeDate = DateUtils.transformDate(downloadedDirAndFileName.substring(firstIndex+1, firstIndex+9));
        String csvFileName =
                ApCo.PRA_DM_FILE_PREFIX
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
