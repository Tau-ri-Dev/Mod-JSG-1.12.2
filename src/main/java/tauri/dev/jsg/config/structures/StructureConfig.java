package tauri.dev.jsg.config.structures;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import tauri.dev.jsg.JSG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class StructureConfig {

    public static class StructureConfigEntry{
        public final boolean enabled;
        public final float chanceToSpawn;

        public StructureConfigEntry(boolean enabled, float chanceToSpawn){
            this.enabled = enabled;
            this.chanceToSpawn = chanceToSpawn;
        }

        @Override
        public String toString(){
            return "[enabled=" + enabled + ", chance=" + chanceToSpawn + "]";
        }
    }

    private final Map<String, StructureConfigEntry> entries = new HashMap<>();
    private final String configId;

    public StructureConfig(String configId) {
        this.configId = configId;
    }

    public void addKey(String key, boolean defaultValue, float defaultChance) {
        entries.put(key, new StructureConfigEntry(defaultValue, defaultChance));
    }

    // -------------------------------------------------
    // STATIC

    private static final Map<String, Map<String, StructureConfigEntry>> CONFIG_MAP = new HashMap<>();
    private static File configDir;
    private static File configFile;

    public static void addConfig(StructureConfig config) {
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

    public static boolean isEnabled(String configKey, String key) {
        return CONFIG_MAP.get(configKey).get(key).enabled;
    }

    public static float getChance(String configKey, String key){
        return CONFIG_MAP.get(configKey).get(key).chanceToSpawn;
    }

    public static void load(File configDir) {
        StructureConfig.configDir = configDir;
        configFile = new File(configDir, "jsg/jsgStructuresConfig_" + JSG.CONFIG_STRUCTURES_VERSION + ".json");

        try {
            Type typeOfHashMap = new TypeToken<Map<String, Map<String, StructureConfigEntry>>>() {
            }.getType();
            Map<String, Map<String, StructureConfigEntry>> tempMap = new GsonBuilder().create().fromJson(new FileReader(configFile), typeOfHashMap);
            if (tempMap.size() != CONFIG_MAP.size()) {
                rewrite();
                return;
            }
            CONFIG_MAP.clear();
            for (String configKey : tempMap.keySet()) {
                Map<String, StructureConfigEntry> map = new HashMap<>();
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
