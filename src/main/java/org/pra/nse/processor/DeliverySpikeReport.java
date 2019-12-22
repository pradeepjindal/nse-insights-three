package org.pra.nse.processor;

import org.pra.nse.ApCo;
import org.pra.nse.db.dao.NseReportsDao;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.email.EmailService;
import org.pra.nse.util.NseFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DeliverySpikeReport {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeliverySpikeReport.class);

    private final NseReportsDao nseReportsDao;
    private final EmailService emailService;
    private final NseFileUtils nseFileUtils;

    DeliverySpikeReport(NseReportsDao nseReportsDao,
                        EmailService emailService,
                        NseFileUtils nseFileUtils) {
        this.nseReportsDao = nseReportsDao;
        this.emailService = emailService;
        this.nseFileUtils = nseFileUtils;
    }

    public void process() {
        List<DeliverySpikeDto> result = nseReportsDao.getDeliverySpike();

        Set<LocalDate> tradeDates = new HashSet<>();
        List<String> dataLines = new ArrayList<>();
        result.forEach( row-> {
            tradeDates.add(row.getTradeDate());
            dataLines.add(row.toCsvString());
        });
        List<LocalDate> list = tradeDates.stream().collect(Collectors.toList());
        Collections.sort(list);

        String fileName = "DeliverySpikeReport-" + list.get(list.size()-1) + ".csv";
        String toDir = ApCo.BASE_DATA_DIR +File.separator+ ApCo.PRA_DATA_DIR_NAME +File.separator+ fileName;

        if(nseFileUtils.isFileExist(toDir)) {
            LOGGER.info("report already present (regeneration and email would be skipped): {}", toDir);
            return;
        }

        File csvOutputFile = new File(toDir);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("symbol,trade_date,open,high,low,close,traded_chg_prcnt,delivered_chg_prcnt,hmo_prcnt,oml_prcnt");
            dataLines.stream()
                    //.map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error: {}", e);
            throw new RuntimeException("DeliverySpikeReport: Could not create file");
        }

        if(nseFileUtils.isFileExist(toDir)) {
            emailService.sendAttachmentMessage("ca.manish.thakkar@gmail.com", fileName, fileName, toDir, null);
            emailService.sendAttachmentMessage("shweta.jindal@haldiram.com", fileName, fileName, toDir, null);
        } else {
            LOGGER.error("skipping email: DeliverySpikeReport not found at disk");
        }

    }
}
