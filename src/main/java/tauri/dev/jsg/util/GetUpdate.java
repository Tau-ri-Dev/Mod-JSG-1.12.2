package tauri.dev.jsg.util;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class GetUpdate {
    public enum EnumUpdateResult{
        UP_TO_DATE,
        NEWER_AVAILABLE,
        ERROR,
        DISABLED
    }

    public static class UpdateResult {
        public final EnumUpdateResult result;
        public final String response;

        public UpdateResult(EnumUpdateResult result, String newestVersion){
            this.result = result;
            this.response = newestVersion;
        }
    }

    public static final String ERROR_STRING = "Error was occurred while updating JSG!";

    public static final String URL_BASE = "https://api.justsgmod.eu/?api=curseforge&version=" + JSG.MC_VERSION;

    public static final String DOWNLOAD_URL_USER = "https://mod.justsgmod.eu/";

    public static final String GET_NAME_URL = URL_BASE + "&t=name";

    public static UpdateResult checkForUpdate(){
        String currentVersion = JSG.MOD_VERSION.replace(JSG.MC_VERSION + "-", "");
        if(!JSGConfig.enableAutoUpdater) return new UpdateResult(EnumUpdateResult.DISABLED, currentVersion);
        String webData = getSiteContent(GET_NAME_URL);
        if(webData.equalsIgnoreCase(ERROR_STRING)) return new UpdateResult(EnumUpdateResult.ERROR, "Exit code: 1");
        String[] got = webData.split("-");
        if(got.length < 3) return new UpdateResult(EnumUpdateResult.ERROR, "Exit code: 2");
        String gotVersion = got[2];

        String[] currentVersionSplit = currentVersion.split("\\.");
        String[] gotVersionSplit = gotVersion.split("\\.");
        try {
            for (int i = 0; i < 4; i++) {
                if (gotVersionSplit.length < i + 1 || currentVersionSplit.length < i + 1)
                    continue;

                if (parseInt(currentVersionSplit[i]) < parseInt(gotVersionSplit[i])){
                    return new UpdateResult(EnumUpdateResult.NEWER_AVAILABLE, gotVersion);
                }

                if (parseInt(currentVersionSplit[i]) > parseInt(gotVersionSplit[i])){
                    return new UpdateResult(EnumUpdateResult.UP_TO_DATE, gotVersion);
                }
            }
        }
        catch(Exception e){
            JSG.warn("Error while checking for update!", e);
            return new UpdateResult(EnumUpdateResult.ERROR, "Exit code: 3");
        }
        return new UpdateResult(EnumUpdateResult.UP_TO_DATE, gotVersion);
    }

    public static void openWebsiteToClient(String url){
        try {
            Class<?> ocClass = Class.forName("java.awt.Desktop");
            Object object = ocClass.getMethod("getDesktop").invoke(null);
            ocClass.getMethod("browse", URI.class).invoke(object, new URI(url));
        } catch (Exception e) {
            JSG.error("Couldn't open link", e);
        }
    }

    private static String getSiteContent(String link) {
        URL Url;
        try {
            Url = new URL(link);
        } catch (MalformedURLException e1) {
            return ERROR_STRING;
        }
        HttpURLConnection Http;
        try {
            Http = (HttpURLConnection) Url.openConnection();
        } catch (IOException e1) {
            return ERROR_STRING;
        }
        if(Http == null) return ERROR_STRING;
        Map<String, List<String>> Header = Http.getHeaderFields();

        try {
            for (String header : Header.get(null)) {
                if (header.contains(" 302 ") || header.contains(" 301 ")) {
                    link = Header.get("Location").get(0);
                    try {
                        Url = new URL(link);
                    } catch (MalformedURLException e) {
                        return ERROR_STRING;
                    }
                    try {
                        Http = (HttpURLConnection) Url.openConnection();
                    } catch (IOException e) {
                        return ERROR_STRING;
                    }
                    Header = Http.getHeaderFields();
                }
            }
        }
        catch(Exception ignored){
            return ERROR_STRING;
        }

        InputStream Stream;
        try {
            Stream = Http.getInputStream();
        } catch (IOException e) {
            return ERROR_STRING;
        }
        String Response;
        try {
            Response = GetStringFromStream(Stream);
        } catch (IOException e) {
            return ERROR_STRING;
        }
        return Response;
    }

    private static String GetStringFromStream(InputStream Stream) throws IOException {
        if (Stream != null) {
            Writer Writer = new StringWriter();

            char[] Buffer = new char[2048];
            try {
                Reader Reader = new BufferedReader(new InputStreamReader(Stream, StandardCharsets.UTF_8));
                int counter;
                while ((counter = Reader.read(Buffer)) != -1) {
                    Writer.write(Buffer, 0, counter);
                }
            } finally {
                Stream.close();
            }
            return Writer.toString();
        } else {
            return ERROR_STRING;
        }
    }
}
