package org.pra.nse.util;

import org.pra.nse.ApCo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class NseFileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseFileUtils.class);

    public void createFolder(String outputPathAndFileName) {
        String dataDir = ApCo.BASE_DATA_DIR + File.separator + ApCo.PRA_DATA_DIR_NAME;
        File folder = new File(dataDir);
        File[] listOfFiles = folder.listFiles();
        if(null == folder.listFiles()) {
            createDataDir(dataDir);
        }
    }

    public void createDataDir(String dataDir) {
        File newFolder = new File(dataDir);
        boolean created =  newFolder.mkdir();
        LOGGER.info("creating folder: " + dataDir);
        if(created)
            LOGGER.info("Folder was created!");
        else
            LOGGER.info("Unable to create folder");
    }

    public boolean isFileExist(String filePathAndName) {
        return new File(filePathAndName).exists();
    }

    public void unzip(String outputDirAndFileName) throws IOException {
        int lastIndex = outputDirAndFileName.lastIndexOf("\\");
        File destDir = new File(outputDirAndFileName.substring(0, lastIndex));
        byte[] buffer = new byte[1024];
        ZipInputStream zis = null;
        zis = new ZipInputStream(new FileInputStream(outputDirAndFileName));
        ZipEntry zipEntry;
        zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile;
            newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }
    public void unzipNew(String outputDirAndFileName, String filePrefix) throws IOException {
        int lastIndex = outputDirAndFileName.lastIndexOf(File.separator);
        File destDir = new File(outputDirAndFileName.substring(0, lastIndex));
        byte[] buffer = new byte[1024];
        ZipInputStream zis = null;
        zis = new ZipInputStream(new FileInputStream(outputDirAndFileName));
        ZipEntry zipEntry;
        zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            String csvFilePathAndName = destDir + File.separator + filePrefix + DateUtils.extractDate(zipEntry.getName()) + ApCo.PRA_DATA_FILE_EXT;
            File csvFile = new File(csvFilePathAndName);
            FileOutputStream fos = new FileOutputStream(csvFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }
    public void unzipNew2(String outputDirAndFileName, String filePrefix) throws IOException {
        int lastIndex = outputDirAndFileName.lastIndexOf(File.separator);
        File destDir = new File(outputDirAndFileName.substring(0, lastIndex));
        byte[] buffer = new byte[1024];
        ZipInputStream zis = null;
        zis = new ZipInputStream(new FileInputStream(outputDirAndFileName));
        ZipEntry zipEntry;
        zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            String csvFilePathAndName = destDir + File.separator
                    + filePrefix + DateUtils.extractDate(zipEntry.getName())
                    + ApCo.PRA_DATA_FILE_EXT;
            File csvFile = new File(csvFilePathAndName);
            FileOutputStream fos = new FileOutputStream(csvFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public List<String> constructFileNames(LocalDate fromDate, String fileNameDateFormat, String filePrefix, String fileSuffix) {
        List<String> fileNameList = new ArrayList<>();
        LocalDate rollingDate = fromDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fileNameDateFormat);
        LocalDate todayDate = LocalDate.now();
        int weekends = 0;
        while(rollingDate.compareTo(todayDate) < 1) {
            //LOGGER.info(localDate);
            //LOGGER.info(localDate.getDayOfWeek());
            if(rollingDate.equals(LocalDate.of(2019,10,27))) {
                LOGGER.info("Deepawali Found - {}", rollingDate);
                calcAndAddFileName(fileNameList, filePrefix, formatter, fileSuffix, rollingDate);
            } else if(DateUtils.isWeekend(rollingDate)) {
                weekends++;
            } else {
                calcAndAddFileName(fileNameList, filePrefix, formatter, fileSuffix, rollingDate);
            }
            rollingDate = rollingDate.plusDays(1);
        }
        //fileNamesToBeDownloaded.forEach(fileName -> LOGGER.info(fileName));
        LOGGER.info("Total File Count ({}): {}", filePrefix, fileNameList.size());
//        fileNameList.forEach(name-> {
//            String s = extractDate(name);
//            System.out.println(s);
//        });
        return fileNameList;
    }

    public List<String> fetchFileNames(String dirPathAndName, String filePrefix, String fileSuffix) {
        File folder = new File(dirPathAndName);
        File[] listOfFiles = folder.listFiles();
        if(null == folder.listFiles()) {
            createDataDir(dirPathAndName);
            listOfFiles = folder.listFiles();
        }
        List<String> existingFiles = new ArrayList<>();
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                //LOGGER.info("File " + listOfFiles[i].getName());
                existingFiles.add(listOfFile.getName());
            } else if (listOfFile.isDirectory()) {
                LOGGER.info("Directory " + listOfFile.getName());
            }
        }
//        existingFiles.forEach(name-> {
//            String s = extractDate(name);
//            System.out.println(s);
//        });
        return existingFiles;
    }

    public List<String> constructFileDownloadUrlWithYearAndMonth(String baseUrl, List<String> filesToBeDownloaded) {
        List<String> filesUrl;
        filesUrl = filesToBeDownloaded.stream().map(fileName -> {
            //LOGGER.info(fileName);
            return baseUrl + "/" + fileName.substring(4, 7) + "/" + fileName;
        }).collect(Collectors.toList());
        //String newUrl = baseUrl + "/" + localDate.getMonth().name().substring(0,3) + "/fo" + formatter.format(localDate).toUpperCase() + "bhav.csv.zip";
        //filesUrl.forEach(LOGGER::info);
        return filesUrl;
    }
    public List<String> constructFileDownloadUrl(String baseUrl, List<String> filesToBeDownloaded) {
        List<String> filesUrl;
        filesUrl = filesToBeDownloaded.stream().map(fileName -> {
            //LOGGER.info(fileName);
            return baseUrl + "/" + fileName;
        }).collect(Collectors.toList());
        //String newUrl = baseUrl + "/" + localDate.getMonth().name().substring(0,3) + "/fo" + formatter.format(localDate).toUpperCase() + "bhav.csv.zip";
        //filesUrl.forEach(url -> LOGGER.info(url));
        return filesUrl;
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName().replace("bhav",""));
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }

    public List<LocalDate> getFilesToBeComputed(LocalDate downloadFromDate, Supplier<String> filePrefixSupplier) {
        String dataDir = ApCo.BASE_DATA_DIR + File.separator + ApCo.PRA_DATA_DIR_NAME;
        String filePrefix = filePrefixSupplier.get();
        List<String> filesToBeDownloaded = constructFileNames(
                downloadFromDate,
                ApCo.PRA_FILE_NAME_DATE_FORMAT,
                filePrefix + "-",
                ApCo.PRA_DATA_FILE_EXT);
        filesToBeDownloaded.removeAll(fetchFileNames(dataDir, null, null));
        List<LocalDate> localDates = new ArrayList<>();
        TreeMap<LocalDate, LocalDate> localDateMap = new TreeMap<>();
        filesToBeDownloaded.forEach( file -> {
            String date = file.replace(filePrefix + "-", "");
            date = date.replace(ApCo.PRA_DATA_FILE_EXT, "");
            LocalDate localDate = LocalDate.of(
                    Integer.valueOf(date.substring(0,4)),
                    Integer.valueOf(date.substring(5,7)),
                    Integer.valueOf(date.substring(8,10)));
            localDates.add(localDate);
            localDateMap.put(localDate, localDate);
            //localDates.add(LocalDate.of(date.substring(0,4), date.substring(5,7), date.substring(8,10)));
        });
        return localDates;
        //return (List<LocalDate>)localDateMap.values();
    }

    private void calcAndAddFileName(List<String> fileNameList, String filePrefix, DateTimeFormatter formatter, String fileSuffix, LocalDate localDate) {
        //LOGGER.info(localDate.getDayOfWeek());
        String newFileName = filePrefix + formatter.format(localDate).toUpperCase() + fileSuffix;
        //LOGGER.info(newFileName);
        fileNameList.add(newFileName);
    }

}
