package tauri.dev.jsg.util.updater;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;

import java.net.URI;
import java.net.URL;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class GetUpdate {
    public enum EnumUpdateResult {
        UP_TO_DATE,
        NEWER_AVAILABLE,
        ERROR,
        DISABLED
    }

    public static class UpdateResult {
        public EnumUpdateResult result;
        public final String response;

        public UpdateResult(EnumUpdateResult result, String newestVersion) {
            this.result = result;
            this.response = newestVersion;
        }
    }

    public static final String ERROR_STRING = "Error was occurred while updating JSG!";

    public static final String URL_BASE = "https://justsgmod.eu/api/?api=curseforge&version=" + JSG.MC_VERSION;

    public static final String GET_NAME_URL = URL_BASE + "&t=name";
    public static final String GET_DOWNLOAD_URL = URL_BASE + "&t=url";

    public static final String DOWNLOAD_URL_USER = getSiteContent(GET_DOWNLOAD_URL);

    public static UpdateResult checkForUpdate() {
        String currentVersion = JSG.MOD_VERSION.replace(JSG.MC_VERSION + "-", "");
        if (!JSGConfig.General.enableAutoUpdater) return new UpdateResult(EnumUpdateResult.DISABLED, currentVersion);
        String webData = getSiteContent(GET_NAME_URL);
        if (webData.equalsIgnoreCase(ERROR_STRING)) return new UpdateResult(EnumUpdateResult.ERROR, "Exit code: 1");
        String[] got = webData.split("-");
        if (got.length < 3) return new UpdateResult(EnumUpdateResult.ERROR, "Exit code: 2");
        String gotVersion = got[2];

        String[] currentVersionSplit = currentVersion.split("\\.");
        String[] gotVersionSplit = gotVersion.split("\\.");
        try {
            for (int i = 0; i < 4; i++) {
                if (gotVersionSplit.length < i + 1 || currentVersionSplit.length < i + 1)
                    continue;

                if (parseInt(currentVersionSplit[i]) < parseInt(gotVersionSplit[i])) {
                    return new UpdateResult(EnumUpdateResult.NEWER_AVAILABLE, gotVersion);
                }

                if (parseInt(currentVersionSplit[i]) > parseInt(gotVersionSplit[i])) {
                    return new UpdateResult(EnumUpdateResult.UP_TO_DATE, gotVersion);
                }
            }
        } catch (Exception e) {
            JSG.warn("Error while checking for update!", e);
            return new UpdateResult(EnumUpdateResult.ERROR, "Exit code: 3");
        }
        return new UpdateResult(EnumUpdateResult.UP_TO_DATE, gotVersion);
    }

    public static void openWebsiteToClient(String url) {
        try {
            Class<?> ocClass = Class.forName("java.awt.Desktop");
            Object object = ocClass.getMethod("getDesktop").invoke(null);
            ocClass.getMethod("browse", URI.class).invoke(object, new URI(url));
        } catch (Exception e) {
            JSG.error("Couldn't open link", e);
        }
    }

    public static String getSiteContent(String link) {
        try {
            //Instantiating the URL class
            URL url = new URL(link);
            //Retrieving the contents of the specified page
            Scanner sc = new Scanner(url.openStream());
            //Instantiating the StringBuffer class to hold the result
            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) {
                sb.append(sc.next());
                //System.out.println(sc.next());
            }
            //Retrieving the String from the String Buffer object
            String result = sb.toString();
            System.out.println(result);
            //Removing the HTML tags
            result = result.replaceAll("<[^>]*>", "");
            return result;
        }
        catch (Exception e){
            JSG.error("Error while getting data from " + link);
            JSG.error("Site content Exception", e);
        }
        return ERROR_STRING;
    }
}
