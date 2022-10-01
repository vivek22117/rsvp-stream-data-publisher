package com.dd.position.simulator.utility;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class GzipUtility {

    public static byte[] compressData(byte[] bytes) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
                gzipOutputStream.write(bytes);
            }
            return outputStream.toByteArray();
        } catch (IOException ex) {
            log.error("Failed to zip the file.." + ex.getMessage());
            return null;
        }
    }

    public static String decompressData(byte[] compressData) {

        if (!isZipped(compressData)) {
            return new String(compressData);
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(compressData)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            builder.append(line);
                        }
                        return builder.toString();
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to decompress the file.." + ex.getMessage());
        }
    }

    private static boolean isZipped(byte[] compressData) {
        return (compressData[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressData[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    public static byte[] serializeData(String rsvpEventRecord) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream output = new ObjectOutputStream(bos)
        ) {
            output.writeObject(rsvpEventRecord);
            log.debug("Object has been serialized");
            return Base64.getEncoder().encode(bos.toByteArray());
        } catch (Exception ex) {
            log.error("Cannot perform output", ex);
            return null;
        }
    }

    public static String deserializeData(String data) {
        byte[] decode = Base64.getDecoder().decode(data);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(decode);
             ObjectInputStream out = new ObjectInputStream(bis)) {

            return (String) out.readObject();
        } catch (Exception ex) {
            log.error("Failed to deserialize data...", ex);
            return null;
        }
    }
}
