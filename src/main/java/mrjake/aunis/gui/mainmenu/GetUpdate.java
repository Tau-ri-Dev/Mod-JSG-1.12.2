package mrjake.aunis.gui.mainmenu;

import mrjake.aunis.sound.StargateSoundEventEnum;
import mrjake.aunis.stargate.EnumScheduledTask;
import mrjake.aunis.state.stargate.StargateRendererActionState;
import mrjake.aunis.tileentity.util.ScheduledTask;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class GetUpdate {

    public static final String ERROR_STRING = "Error was occurred while updating Aunis!";

    public static final String GET_NAME_URL = "https://amazingworlds.eu/curseapi/1.12.2/?t=name";
    public static final String GET_DOWNLOAD_URL = "https://amazingworlds.eu/curseapi/1.12.2/?t=url";
    public static final String GET_SIZE_URL = "https://amazingworlds.eu/curseapi/1.12.2/?t=size";

    public static final String MINECRAFT_ROOT = System.getenv("APPDATA") + "/.minecraft/mods/1.12.2/";

    public static double percentOfFileDownloaded = 0;

    public static String checkForUpdate(String currentVersion){
        String gotVersion = getSiteContent(GET_NAME_URL).split("-")[2];
        if(gotVersion.equals(ERROR_STRING)) return "false";

        String[] currentVersionSplit = currentVersion.split("\\.");
        String[] gotVersionSplit = gotVersion.split("\\.");
        try {
            for (int i = 0; i < 4; i++) {
                if (gotVersionSplit.length < i + 1 || currentVersionSplit.length < i + 1)
                    continue;

                if (parseInt(gotVersionSplit[i]) > parseInt(currentVersionSplit[i]))
                    return gotVersion;
            }
        }
        catch(Exception ignored){}


        return "false"; // must return string, because if true -> returns version
    }

    public static boolean updateMod(String currentVersion, String alphaTag){
        return false;
        //if(checkForUpdate(currentVersion).equals("false") || checkForUpdate(currentVersion).equals(ERROR_STRING)) return false;
        // TODO(Mine): Do file download
        /*
        try {
            String[] url = getSiteContent(GET_NAME_URL).split("/");
            StringBuilder url_final = new StringBuilder();
            for(int i = 0; i < url.length; i++) {
                if (url[i].equals("files"))
                    url[i] = "download";
                url_final.append(url[i]);
            }

            try (InputStream in = URI.create(String.valueOf(url_final)).toURL().openStream()) {
                Files.copy(in, Paths.get(MINECRAFT_ROOT + "aunis-1.12.2-" + currentVersion + alphaTag + ".jar"));
                deleteQuietly(new File(MINECRAFT_ROOT + "aunis-1.12.2-" + currentVersion + alphaTag + ".jar"));
            }

            //copyURLToFile(new URL(GET_DOWNLOAD_URL), new File(MINECRAFT_ROOT + url_final), 1000000000, 1000000000);
            return true;
        }
        catch(Exception ignored){}
        return false;*/
    }

    public static void updatePercents() {
        String[] url = getSiteContent(GET_NAME_URL).split("/");
        StringBuilder url_final = new StringBuilder();
        for(int i = 0; i < url.length; i++) {
            if (url[i].equals("files"))
                url[i] = "download";
            url_final.append(url[i]);
        }
        try {
            long downloadedFileSize = Files.size(Paths.get(MINECRAFT_ROOT + url_final));
            final long targetFileSize = Long.parseLong(getSiteContent(GET_SIZE_URL));
            percentOfFileDownloaded = (double) (downloadedFileSize/targetFileSize)*100;
        }
        catch(Exception ignored){}
    }

    public static double getPercents(){
        updatePercents();
        return percentOfFileDownloaded;
    }


    public static String getSiteContent(String link) {
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
                Reader Reader = new BufferedReader(new InputStreamReader(Stream, "UTF-8"));
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
