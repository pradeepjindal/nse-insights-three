package org.pra.nse.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class DownloadUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadUtils.class);

    public void downloadFile(String fromUrl, String toDir, Supplier<String> inputFileSupplier, Consumer<String> outputFileConsumer) {
        String outputDirAndFileName = inputFileSupplier.get();
        LOGGER.info("FROM URL: " + fromUrl);
        LOGGER.info("TO   DIR: " + outputDirAndFileName);


//        try {
//            FileUtils.copyURLToFile(
//                    new URL(fromUrl),
//                    new File(outputDirAndFileName),
//                    10000,
//                    10000);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        AsyncHttpClient client = Dsl.asyncHttpClient();
//        try(FileOutputStream stream = new FileOutputStream(outputDirAndFileName)) {
//
//            client.prepareGet(fromUrl).execute(new AsyncCompletionHandler<FileOutputStream>() {
//                @Override
//                public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
//                    stream.getChannel().write(bodyPart.getBodyByteBuffer());
//                    return State.CONTINUE;
//                }
//                @Override
//                public FileOutputStream onCompleted(Response response) throws Exception {
//                    return stream;
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(fromUrl).openStream());
             FileOutputStream fileOS = new FileOutputStream(outputDirAndFileName)) {

            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }

            //unzip(outputDirAndFileName);
            outputFileConsumer.accept(outputDirAndFileName);

        } catch (IOException e) {
            LOGGER.warn("Error while downloading file: {}", e.getMessage());
            //LOGGER.warn("Error while downloading file: {}", e);
        }


//        try(InputStream in = new URL(fromUrl).openStream()) {
//            Files.copy(in, Paths.get(outputDirAndFileName), StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        try (ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(fromUrl).openStream());
//             FileOutputStream fileOutputStream = new FileOutputStream(outputDirAndFileName)) {
//
//            FileChannel fileChannel = fileOutputStream.getChannel();
//            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void downloadFileOld(String fromUrl, String toDir, Supplier<String> inputFileSupplier, Consumer<String> outputFileConsumer) {
        String outputDirAndFileName = inputFileSupplier.get();
        LOGGER.info("FROM URL: " + fromUrl);
        LOGGER.info("TO   DIR: " + outputDirAndFileName);
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(fromUrl).openStream());
             FileOutputStream fileOS = new FileOutputStream(outputDirAndFileName)) {
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
            //unzip(outputDirAndFileName);
            outputFileConsumer.accept(outputDirAndFileName);
        } catch (IOException e) {
            LOGGER.warn("Error while downloading file: {}", e.getMessage());
            //LOGGER.warn("Error while downloading file: {}", e);
        }
    }
}
