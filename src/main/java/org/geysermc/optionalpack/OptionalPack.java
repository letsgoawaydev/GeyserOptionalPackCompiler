package org.geysermc.optionalpack;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class OptionalPack {
    public static final Path TEMP_PATH = Path.of("temp-pack/");

    public static final Path WORKING_PATH = Path.of("temp-pack/pack/");
    public static void main(String[] args) {
        try {
            log("Extracting pre-made optional pack data to folder...");
            extractOptionalPackDataToFolder();

            log("Downloading client.jar from Mojang...");
            InputStream in = HTTP.request("https://launcher.mojang.com/v1/objects/37fd3c903861eeff3bc24b71eed48f828b5269c8/client.jar");
            Path jarFile = Path.of("client.jar");
            Files.copy(in, jarFile, StandardCopyOption.REPLACE_EXISTING);

            ZipFile clientJar = new ZipFile(jarFile.toFile());
            JavaAssetRetriever.extract(clientJar);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // there are probably better ways to do this, but this is the way im doing it
    private static void extractOptionalPackDataToFolder() throws Exception {
        File f = new File(OptionalPack.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI());
        //     Files.copy(new FileInputStream(f), Path.of("secondary.jar"), StandardCopyOption.REPLACE_EXISTING);
        ZipFile jar = new ZipFile(f);

        unzipPack(f, TEMP_PATH);
    }

    private static void unzipPack(File file, Path destDir) {
        File dir = destDir.toFile();
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(file);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                if (!ze.isDirectory()) {
                    String fileName = ze.getName();
                    File newFile = new File(destDir + File.separator + fileName);
                    //create directories for sub directories in zip
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                //close this ZipEntry

                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void log(String message) {
        System.out.println(message);
    }
}