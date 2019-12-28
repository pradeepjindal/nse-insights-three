package org.pra.nse.csv.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TransformManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformManager.class);

    private final CmTransformer cmTransformer;
    private final FmTransformer fmTransformer;
    private final DmTransformer dmTransformer;

    public TransformManager(CmTransformer cmTransformer, FmTransformer fmTransformer, DmTransformer dmTransformer) {
        this.cmTransformer = cmTransformer;
        this.fmTransformer = fmTransformer;
        this.dmTransformer = dmTransformer;
    }


    public void transform() {
        LOGGER.info(".");
        LOGGER.info("____________________ Transform Manager");

        cmTransformer.transformFromLastDate();
        LOGGER.info("--------------------");
        fmTransformer.transformFromLastDate();
        LOGGER.info("--------------------");
        dmTransformer.transformFromLastDate();

//        cmTransformer.transformFromDate();
//        LOGGER.info("--------------------");
//        fmTransformer.transformFromDate();
//        LOGGER.info("--------------------");
//        dmTransformer.transformFromDate();

        LOGGER.info("==================== Transform Manager");
    }
}
