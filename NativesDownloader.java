package net.deftera.Util.Natives;

import org.apache.commons.lang.SystemUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sk.tomsik68.mclauncher.api.versions.IVersion;
import sk.tomsik68.mclauncher.api.versions.IVersionInstaller;
import sk.tomsik68.mclauncher.api.versions.IVersionLauncher;
import sk.tomsik68.mclauncher.api.versions.IVersionList;
import sk.tomsik68.mclauncher.backend.MinecraftLauncherBackend;
import sk.tomsik68.mclauncher.impl.common.Platform;
import sk.tomsik68.mclauncher.impl.versions.mcdownload.MCDownloadVersionList;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class NativesDownloader {
    public NativesDownloader(File versionFolder, String versionID, String versionFolderName) {
        try {

            File nativesFolder = new File(versionFolder.getAbsolutePath() + File.separator + versionFolderName + "-natives");
            if (!nativesFolder.exists()) {
                nativesFolder.mkdir();
            }
            File tempFolder = new File(versionFolder.getAbsolutePath() + "/temp");
            if (!tempFolder.exists()) {
                tempFolder.mkdir();
            }

            URL url = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
            Scanner scanner = new Scanner(url.openStream());
            String jsonString = "";
            while (scanner.hasNext()) {
                jsonString = jsonString + scanner.next();
            }
            System.out.println(jsonString);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            JSONArray jsonVersions = (JSONArray) json.get("versions");

            JSONObject versionInfo = null;

            for (Object object :
                    jsonVersions) {

                JSONObject jsonObject = (JSONObject) object;
                if(versionID.equals(jsonObject.get("id"))) {
                    versionInfo = jsonObject;
                    break;
                }


            }

            URL versionJSONURL = new URL((String) versionInfo.get("url"));




            System.out.println("Bogos Binted: " + versionJSONURL);
            Scanner versionInfoScanner = new Scanner(versionJSONURL.openStream());
            String versionInfoJsonString = "";
            while (versionInfoScanner.hasNext()) {
                versionInfoJsonString = versionInfoJsonString + versionInfoScanner.next();
            }

            JSONObject versionJson = (JSONObject) parser.parse(versionInfoJsonString);
            JSONArray librariesArray = (JSONArray) versionJson.get("libraries");

            for (Object object:
                 librariesArray) {

                JSONObject jsonObject = (JSONObject) object;
                String jsonObjectName = (String) jsonObject.get("name");
                if(jsonObjectName.contains("natives") && jsonObjectName.contains(Platform.getCurrentPlatform().getMinecraftName())) {
                    URL downloadURL = new URL((String) ((JSONObject) ((JSONObject) jsonObject.get("downloads")).get("artifact")).get("url"));
                    System.out.println("Download URL:" + downloadURL);

                    try (BufferedInputStream in = new BufferedInputStream(new URL(downloadURL.toString()).openStream());
                         FileOutputStream fileOutputStream = new FileOutputStream(tempFolder + File.separator + jsonObjectName)) {
                        byte dataBuffer[] = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                            fileOutputStream.write(dataBuffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] buffer = new byte[1024];
                    ZipInputStream zis = new ZipInputStream(new FileInputStream(tempFolder + File.separator + jsonObjectName));
                    ZipEntry zipEntry = zis.getNextEntry();
                    while (zipEntry != null) {
                        String filePath = nativesFolder.getAbsolutePath() + File.separator + zipEntry.getName();
                        if(!zipEntry.isDirectory()) {
                            FileOutputStream fos = new FileOutputStream(filePath);
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                        }else {
                            File dir = new File(filePath);
                            dir.mkdir();
                        }
                        zis.closeEntry();
                        zipEntry = zis.getNextEntry();
                    }
                    zis.closeEntry();
                    zis.close();




                    System.out.println("Downlaod URL " + downloadURL);

                }

            }

            Files
                    .walk(tempFolder.toPath()) // Traverse the file tree in depth-first order
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            System.out.println("Deleting: " + path);
                            Files.delete(path);  //delete each file or directory
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });




            System.out.println("Version info json: " + ((JSONObject)((JSONObject)((JSONObject) librariesArray.get(0)).get("downloads")).get("artifact")).get("url"));


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
