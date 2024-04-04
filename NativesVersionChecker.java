package net.deftera.Util.Natives;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NativesVersionChecker {
    public boolean checker(String versionID) {

        try {
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

            for (Object object :
                    jsonVersions) {

                JSONObject jsonObject = (JSONObject) object;
                if (jsonObject.get("id").equals(versionID)) {
                    return true;
                }
                if(jsonObject.get("id").equals("1.19-pre1")) {
                    return false;
                }


            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
