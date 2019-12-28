package org.pra.nse.util;

import org.pra.nse.ApCo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        return toLocalDate(date_yyyyMMdd, ApCo.DEFAULT_FILE_NAME_DATE_FORMAT);
    }

    public static LocalDate toLocalDate(String date, String format) {
        //if I send month NOV or Nov it works
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(format)
                .toFormatter(Locale.US);
        LocalDate dateTime = LocalDate.parse(date, formatter);
        return LocalDate.parse(date, formatter);
}

    public static String extractDate(String inputString) {
        return extractDate(inputString, "\\d{2}[A-Z]{3}\\d{4}", "ddMMMyyyy");
    }
    public static String extractDate(String inputString, String dateRegex, String dateFormat) {
        //String input = "John Doe at:2016-01-16 Notes:This is a test";
        //String regex = " at:(\\d{4}-\\d{2}-\\d{2}) Notes:";
        String regex = dateRegex;
        Matcher m = Pattern.compile(regex).matcher(inputString);
        if (m.find()) {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                    // case insensitive to parse JAN and Jan
                    .parseCaseInsensitive()
                    // add pattern
                    .appendPattern(dateFormat)
                    // create formatter (use English Locale to parse month names)
                    .toFormatter(Locale.ENGLISH);
            return LocalDate.parse(m.group(0), dtf).toString();
        } else {
            // Bad input
            return "";
        }
    }



    public static LocalDate getLocalDateFromPath(String fileNameWithPth) {
        return getLocalDateFromPath(fileNameWithPth, ApCo.DEFAULT_FILE_NAME_DATE_REGEX);
    }

    public static LocalDate getLocalDateFromPath(String fileNameWithPth, String dateRegex) {
        return getLocalDateFromPath(fileNameWithPth, dateRegex, ApCo.DEFAULT_FILE_NAME_DATE_FORMAT);
    }

    @org.jetbrains.annotations.Nullable
    public static LocalDate getLocalDateFromPath(String fileNameWithPth, String dateRegex, String dateFormat) {
        String dateStr = getDateStringFromPath(fileNameWithPth, dateRegex);
        return (dateStr == null ? null : toLocalDate(dateStr, dateFormat));
    }

    private static String getDateStringFromPath(String fileNameWithPth, String dateRegex) {
        dateRegex = null == dateRegex ? ApCo.DEFAULT_FILE_NAME_DATE_REGEX : dateRegex;
        int count=0;
        String[] allMatches = new String[2];
        //String regex = "\\d{4}-\\d{2}-\\d{2}"; //2019-12-31
        Matcher m = Pattern.compile(dateRegex).matcher(fileNameWithPth);
        while (m.find()) {
            allMatches[count] = m.group();
        }
        return allMatches[0];
    }

    public static boolean isWeekend(LocalDate date) {
        return "SATURDAY".equals(date.getDayOfWeek().name()) || "SUNDAY".equals(date.getDayOfWeek().name());
    }
}
