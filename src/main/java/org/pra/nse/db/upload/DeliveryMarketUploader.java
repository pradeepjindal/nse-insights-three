package org.pra.nse.db.upload;

import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.in.DmBean;
import org.pra.nse.csv.read.DmCsvReader;
import org.pra.nse.db.dao.NseDeliveryMarketDao;
import org.pra.nse.db.model.NseDeliveryMarketTab;
import org.pra.nse.db.repository.NseDeliveryMarketRepository;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DeliveryMarketUploader extends BaseUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryMarketUploader.class);

    private final NseDeliveryMarketRepository repository;
    private final NseDeliveryMarketDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraNameUtils praNameUtils;
    private final DmCsvReader csvReader;

    public DeliveryMarketUploader(NseDeliveryMarketRepository nseDeliveryMarketRepository,
                                  NseDeliveryMarketDao nseDeliveryMarketDao,
                                  NseFileUtils nseFileUtils,
                                  PraNameUtils praNameUtils,
                                  DmCsvReader csvReader) {
        super(praNameUtils, ApCo.DM_DIR_NAME, ApCo.PRA_DM_FILE_PREFIX);
        this.repository = nseDeliveryMarketRepository;
        this.dao = nseDeliveryMarketDao;
        this.nseFileUtils = nseFileUtils;
        this.praNameUtils = praNameUtils;
        this.csvReader = csvReader;
    }


    public void upload(LocalDate forDate) {
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("DM-upload | SKIPPING - already uploaded | for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("DM-upload | uploading | for date:[{}]", forDate);
        }

        String fromFile = ApCo.DM_FILES_PATH + File.separator+ ApCo.PRA_DM_FILE_PREFIX +forDate+ ApCo.PRA_DATA_FILE_EXT;
        LOGGER.info("DM-upload | looking for file Name along with path:[{}]",fromFile);

        if(nseFileUtils.isFileExist(fromFile)) {
            LOGGER.info("file exists: [{}]", fromFile);
        } else {
            LOGGER.info("file does not exist: [{}]", fromFile);
            return;
        }
        Map<String, DmBean> mtLatestBeanMap = csvReader.read(fromFile);

        NseDeliveryMarketTab target = new NseDeliveryMarketTab();
        AtomicInteger totalRecordFailed = new AtomicInteger();
        mtLatestBeanMap.values().forEach( source-> {
            target.reset();
            target.setSymbol(source.getSymbol());
            target.setSecurityType(source.getSecurityType());
            target.setTradedQty(source.getTradedQty());
            target.setDeliverableQty(source.getDeliverableQty());
            target.setDeliveryToTradeRatio(source.getDeliveryToTradeRatio());
            target.setTradeDate(DateUtils.toLocalDate(source.getTradeDate()));
            try {
                repository.save(target);
            } catch(DataIntegrityViolationException dive) {
                totalRecordFailed.incrementAndGet();
            }
        });
        LOGGER.info("Total record failed: [{}]", totalRecordFailed.get());
    }

}
