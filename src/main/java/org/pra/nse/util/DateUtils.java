package org.pra.nse.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {

    public static LocalDate toLocalDate(Date utilDate) {
        //new java.sql.Date(date.getTime()).toLocalDate();
        Instant.ofEpochMilli(utilDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date toSqlDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    public static Date toUtilDate(LocalDate localDate) {
        return java.util.Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static String transformDate(String oldFormat) {
        return oldFormat.substring(4,8)
                + "-" + oldFormat.substring(2,4)
                + "-" + oldFormat.substring(0, 2);
    }

    public static LocalDate toLocalDate(String date_yyyyMMdd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //convert String to LocalDate
        return LocalDate.parse(date_yyyyMMdd, formatter);
    }

    public static String extractDate(String inputString) {
        //String input = "John Doe at:2016-01-16 Notes:This is a test";
        //String regex = " at:(\\d{4}-\\d{2}-\\d{2}) Notes:";
        String regex = "\\d{2}[A-Z]{3}\\d{4}";
        Matcher m = Pattern.compile(regex).matcher(inputString);
        if (m.find()) {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                    // case insensitive to parse JAN and FEB
                    .parseCaseInsensitive()
                    // add pattern
                    .appendPattern("ddMMMyyyy")
                    // create formatter (use English Locale to parse month names)
                    .toFormatter(Locale.ENGLISH);
            return LocalDate.parse(m.group(0), dtf).toString();
        } else {
            // Bad input
            return "";
        }
    }

    public static  String getDateStringFromPath(String fileNameWithPth) {
        int count=0;
        String[] allMatches = new String[2];
        String regex = "\\d{4}-\\d{2}-\\d{2}"; //2019-12-31
        Matcher m = Pattern.compile(regex).matcher(fileNameWithPth);
        while (m.find()) {
            allMatches[count] = m.group();
        }
        return allMatches[0];
    }

    public static  LocalDate getLocalDateFromPath(String fileNameWithPth) {
        String dateStr = getDateStringFromPath(fileNameWithPth);
        return (dateStr == null ? null : toLocalDate(dateStr));
    }

}
