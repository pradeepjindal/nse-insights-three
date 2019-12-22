package org.pra.nse.db.upload;

import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.in.FmBean;
import org.pra.nse.csv.read.FmCsvReader;
import org.pra.nse.db.dao.NseFutureMarketDao;
import org.pra.nse.db.model.NseFutureMarketTab;
import org.pra.nse.db.model.NseOptionMarketTab;
import org.pra.nse.db.repository.NseFutureMarketRepository;
import org.pra.nse.db.repository.NseOptionMarketRepository;
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
public class FutureMarketUploader extends BaseUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FutureMarketUploader.class);

    private final NseFutureMarketRepository futureMarketRepository;
    private final NseOptionMarketRepository optionMarketRepository;
    private final NseFutureMarketDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraNameUtils praNameUtils;
    private final FmCsvReader csvReader;

    public FutureMarketUploader(NseFutureMarketRepository nseFutureMarketRepository,
                                NseOptionMarketRepository nseOptionMarketRepository,
                                NseFutureMarketDao nseFutureMarketDao,
                                NseFileUtils nseFileUtils,
                                PraNameUtils praNameUtils,
                                FmCsvReader fmCsvReader) {
        super(praNameUtils, ApCo.FM_DIR_NAME, ApCo.PRA_FM_FILE_PREFIX);
        this.futureMarketRepository = nseFutureMarketRepository;
        this.optionMarketRepository = nseOptionMarketRepository;
        this.dao = nseFutureMarketDao;
        this.nseFileUtils = nseFileUtils;
        this.praNameUtils = praNameUtils;
        this.csvReader = fmCsvReader;
    }


    public void upload(LocalDate forDate) {
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("FM-upload | SKIPPING - already uploaded | for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("FM-upload | uploading | for date:[{}]", forDate);
        }

        String fromFile = ApCo.FM_FILES_PATH + File.separator+ ApCo.PRA_FM_FILE_PREFIX +forDate+ ApCo.PRA_DATA_FILE_EXT;
        LOGGER.info("FM-upload | looking for file Name along with path:[{}]",fromFile);

        if(nseFileUtils.isFileExist(fromFile)) {
            LOGGER.info("file exists: [{}]", fromFile);
        } else {
            LOGGER.info("file does not exist: [{}]", fromFile);
            return;
        }
        Map<FmBean, FmBean> foBeanMap = csvReader.read(null, fromFile);
        //LOGGER.info("{}", foBeanMap.size());

        NseFutureMarketTab futureTab = new NseFutureMarketTab();
        NseOptionMarketTab optionTab = new NseOptionMarketTab();
        AtomicInteger totalRecordFailed = new AtomicInteger();
        foBeanMap.values().forEach( source -> {
            try {
                if("FUTSTK".equals(source.getInstrument()) || "FUTIDX".equals(source.getInstrument())) {
                    futureTab.reset();
                    futureTab.setInstrument(source.getInstrument());
                    futureTab.setSymbol(source.getSymbol());
                    futureTab.setExpiryDate(DateUtils.toLocalDate(source.getExpiry_Dt()));
                    futureTab.setStrikePrice(source.getStrike_Pr());
                    futureTab.setOptionType(source.getOption_Typ());
                    futureTab.setOpen(source.getOpen());
                    futureTab.setHigh(source.getHigh());
                    futureTab.setLow(source.getLow());
                    futureTab.setClose(source.getClose());
                    futureTab.setSettlePrice(source.getSettle_Pr());
                    futureTab.setContracts(source.getContracts());
                    futureTab.setValueInLakh(source.getVal_InLakh());
                    futureTab.setOpenInt(source.getOpen_Int());
                    futureTab.setChangeInOi(source.getChg_In_Oi());
                    futureTab.setTradeDate(DateUtils.toLocalDate(source.getTimestamp()));
                    futureMarketRepository.save(futureTab);
                }else if("OPTSTK".equals(source.getInstrument()) || "OPTIDX".equals(source.getInstrument())) {
                    optionTab.reset();
                    optionTab.setInstrument(source.getInstrument());
                    optionTab.setSymbol(source.getSymbol());
                    optionTab.setExpiryDate(DateUtils.toLocalDate(source.getExpiry_Dt()));
                    optionTab.setStrikePrice(source.getStrike_Pr());
                    optionTab.setOptionType(source.getOption_Typ());
                    optionTab.setOpen(source.getOpen());
                    optionTab.setHigh(source.getHigh());
                    optionTab.setLow(source.getLow());
                    optionTab.setClose(source.getClose());
                    optionTab.setSettlePrice(source.getSettle_Pr());
                    optionTab.setContracts(source.getContracts());
                    optionTab.setValueInLakh(source.getVal_InLakh());
                    optionTab.setOpenInt(source.getOpen_Int());
                    optionTab.setChangeInOi(source.getChg_In_Oi());
                    optionTab.setTradeDate(DateUtils.toLocalDate(source.getTimestamp()));
                    optionMarketRepository.save(optionTab);
                }
            }catch(DataIntegrityViolationException dive) {
                totalRecordFailed.incrementAndGet();
            }

        });
        LOGGER.info("Total record failed: [{}]", totalRecordFailed.get());
    }

}
