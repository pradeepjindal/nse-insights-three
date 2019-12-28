package org.pra.nse.csv.transform;

import org.pra.nse.ApCo;
import org.pra.nse.util.NseFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class TransformHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformHelper .class);

    private final NseFileUtils nseFileUtils;

    public TransformHelper(NseFileUtils nseFileUtils) {
        this.nseFileUtils = nseFileUtils;
    }

    void transform(String dataDir, String filePrefix, String nseFileName, String praFileName) {
        String source = dataDir + File.separator + nseFileName;
        String target = dataDir + File.separator + praFileName;
        if(nseFileUtils.isFileExist(target)) {
            LOGGER.info("file exists - {}", target);
        } else if (nseFileUtils.isFileExist(source)) {
            try {
                nseFileUtils.unzipNew(source, filePrefix);
                LOGGER.info("file transformed - {}", target);
            } catch (FileNotFoundException fnfe) {
                LOGGER.info("file not found - {}", source);
            } catch (IOException e) {
                LOGGER.warn("Error while unzipping file: {}", e);
            }
        }
    }
}
