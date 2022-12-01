package tauri.dev.jsg.config.craftings;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.JSG;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CraftingConfig {

    private final Map<String, Boolean> entries = new HashMap<>();
    private final String configId;

    public CraftingConfig(String configId) {
        this.configId = configId;
    }

    public void addKey(@Nullable ResourceLocation key) {
        if (key == null) return;
        addKey(key.getResourceDomain() + ":" + key.getResourcePath());
    }

    public void addKey(String key) {
        entries.put(key, true);
    }

    // -------------------------------------------------
    // STATIC

    private static final Map<String, Map<String, Boolean>> CONFIG_MAP = new HashMap<>();
    private static File configDir;
    private static File configFile;

    public static void addConfig(CraftingConfig config) {
        CONFIG_MAP.put(config.configId, config.entries);
    }

    private static void rewrite() {
        try {
            if (configFile == null) return;
            FileWriter writer = new FileWriter(configFile);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(CONFIG_MAP));
            writer.close();
        } catch (Exception ignored) {
        }
    }

    public static void reload() {
        load(configDir);
    }

    public static boolean isDisabled(String configKey, @Nullable ResourceLocation key) {
        if (key == null) return false;
        return isDisabled(configKey, key.getResourceDomain() + ":" + key.getResourcePath());
    }

    public static boolean isDisabled(String configKey, String key) {
        return !CONFIG_MAP.get(configKey).get(key);
    }

    public static void load(File configDir) {
        CraftingConfig.configDir = configDir;
        configFile = new File(configDir, "jsg/jsgDisabledCraftings_" + JSG.CRAFTINGS_CONFIG_VERSION + ".json");

        try {
            Type typeOfHashMap = new TypeToken<Map<String, Map<String, Boolean>>>() {
            }.getType();
            Map<String, Map<String, Boolean>> tempMap = new GsonBuilder().create().fromJson(new FileReader(configFile), typeOfHashMap);
            if (tempMap.size() != CONFIG_MAP.size()) {
                rewrite();
                return;
            }
            CONFIG_MAP.clear();
            for (String configKey : tempMap.keySet()) {
                Map<String, Boolean> map = new HashMap<>();
                for (String key : tempMap.get(configKey).keySet()) {
                    map.put(key, tempMap.get(configKey).get(key));
                }
                CONFIG_MAP.put(configKey, map);
            }
        } catch (FileNotFoundException exception) {
            rewrite();
            load(configDir);
        }
    }
}
