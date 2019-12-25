package org.pra.nse;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ApCo {
    public static final LocalDate DOWNLOAD_FROM_DATE = LocalDate.of(2019,12,1);
    public static final LocalDate EMAIL_FROM_DATE = LocalDate.of(2019,12,1);
    public static final LocalTime DAILY_DOWNLOAD_TIME = LocalTime.of(18,0,0,0);

    public static final String BASE_DATA_DIR = System.getProperty("user.home");

    //https://www.nseindia.com/content/historical/EQUITIES/2019/SEP/cm10SEP2019bhav.csv.zip
    public static final String CM_BASE_URL = "https://www.nseindia.com/content/historical/EQUITIES/2019";
    public static final String FO_BASE_URL = "https://www.nseindia.com/content/historical/DERIVATIVES/2019";
    public static final String DM_BASE_URL = "https://www.nseindia.com/archives/equities/mto";
    //https://www.nseindia.com/content/nsccl/fao_participant_vol_13092019.csv
    public static final String BP_BASE_URL = "https://www.nseindia.com/content/nsccl";

    public static final String CM_DIR_NAME = "pra-nse-cm";
    public static final String FM_DIR_NAME = "pra-nse-fm";
    public static final String DM_DIR_NAME = "pra-nse-dm";
    public static final String BP_DIR_NAME = "pra-nse-bp";

    public static final String NSE_CM_FILE_NAME_DATE_REGEX = "\\d{2}[A-Z]{3}\\d{4}";
    public static final String NSE_FM_FILE_NAME_DATE_REGEX = "\\d{2}[A-Z]{3}\\d{4}";
    public static final String NSE_DM_FILE_NAME_DATE_REGEX = "\\d{2}\\d{2}\\d{4}";
    public static final String NSE_BP_FILE_NAME_DATE_REGEX = "\\d{2}\\d{2}\\d{4}";

    public static final String PRA_FILE_NAME_DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
    public static final String DEFAULT_FILE_NAME_DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";

    public static final String NSE_CM_FILE_NAME_DATE_FORMAT = "ddMMMyyyy";
    public static final String NSE_FM_FILE_NAME_DATE_FORMAT = "ddMMMyyyy";
    public static final String NSE_DM_FILE_NAME_DATE_FORMAT = "ddMMyyyy";
    public static final String NSE_BP_FILE_NAME_DATE_FORMAT = "ddMMyyyy";

    public static final String PRA_FILE_NAME_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_FILE_NAME_DATE_FORMAT = "yyyy-MM-dd";

    public static final String PRA_CM_DATA_DATE_FORMAT = "dd-MMM-yyyy";
    public static final String PRA_FM_DATA_DATE_FORMAT = "dd-MMM-yyyy";
    public static final String PRA_DM_DATA_DATE_FORMAT = "yyyy-MM-dd";

    public static final String PRA_DATA_DIR_NAME = "pra-nse-computed-data";
    public static final String PRA_DATA_DATE_FORMAT = "dd-MMM-yyyy";

    public static final String NSE_CM_FILE_PREFIX = "cm";
    public static final String NSE_FM_FILE_PREFIX = "fo";
    public static final String NSE_DM_FILE_PREFIX = "MTO_";
    public static final String NSE_BP_FILE_PREFIX = "fao_participant_vol_";

    public static final String PRA_CM_FILE_PREFIX = "cm-";
    public static final String PRA_FM_FILE_PREFIX = "fo-";
    public static final String PRA_DM_FILE_PREFIX = "mt-";
    public static final String PRA_BP_FILE_PREFIX = "bp-";

    public static final String NSE_CM_FILE_SUFFIX = "bhav.csv.zip";
    public static final String NSE_FO_FILE_SUFFIX = "bhav.csv.zip";
    public static final String NSE_DM_FILE_EXT = ".DAT";
    public static final String NSE_BP_FILE_EXT = ".csv";
    public static final String PRA_DATA_FILE_EXT = ".csv";
    public static final String DEFAULT_FILE_EXT = ".csv";

    public static final String PRADEEP_FILE_NAME = "pradeepData";
    public static final String MANISH_FILE_NAME = "manishData";
    public static final String MANISH_FILE_NAME_B = "manishDataB";

    public static final DateTimeFormatter CM_DTF = DateTimeFormatter.ofPattern(ApCo.NSE_CM_FILE_NAME_DATE_FORMAT);
    public static final String CM_FILES_PATH = ApCo.BASE_DATA_DIR + File.separator + ApCo.CM_DIR_NAME;

    public static final DateTimeFormatter FM_DTF = DateTimeFormatter.ofPattern(ApCo.NSE_FM_FILE_NAME_DATE_FORMAT);
    public static final String FM_FILES_PATH = ApCo.BASE_DATA_DIR + File.separator + ApCo.FM_DIR_NAME;

    public static final DateTimeFormatter DM_DTF = DateTimeFormatter.ofPattern(ApCo.NSE_DM_FILE_NAME_DATE_FORMAT);
    public static final String DM_FILES_PATH = ApCo.BASE_DATA_DIR + File.separator + ApCo.DM_DIR_NAME;

    public static final DateTimeFormatter PRA_DTF = DateTimeFormatter.ofPattern(ApCo.PRA_FILE_NAME_DATE_FORMAT);
    public static final String PRA_FILES_PATH = ApCo.BASE_DATA_DIR + File.separator + ApCo.PRA_DATA_DIR_NAME;

    public static final DateTimeFormatter DEFAULT_DTF = DateTimeFormatter.ofPattern(ApCo.DEFAULT_FILE_NAME_DATE_FORMAT);
    public static final String DEFAULT_FILES_PATH = ApCo.BASE_DATA_DIR + File.separator + ApCo.BASE_DATA_DIR;
}
