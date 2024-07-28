package tauri.dev.jsg.config.stargate;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.world.DimensionType;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.power.general.EnergyRequiredToOperate;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.util.DimensionsHelper;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StargateDimensionConfig {

    private static final Map<String, StargateDimensionConfigEntry> DEFAULTS_MAP = new HashMap<>();
    private static File dimensionConfigFile;
    private static Map<Integer, StargateDimensionConfigEntry> dimensionIntMap;
    private static Map<Integer, StargateDimensionConfigEntry> dimensionMap;

    static {
        DEFAULTS_MAP.put("overworld", new StargateDimensionConfigEntry("overworld", 0, new ArrayList<String>() {{
            add("netherOv");
        }}));
        DEFAULTS_MAP.put("the_nether", new StargateDimensionConfigEntry("the_nether", 5, new ArrayList<String>() {{
            add("netherOv");
        }}, new HashMap<BiomeOverlayEnum, Integer>() {{
            put(BiomeOverlayEnum.NORMAL, 2);
        }}));
        DEFAULTS_MAP.put("the_end", new StargateDimensionConfigEntry("the_end", 10, new ArrayList<>(), new HashMap<BiomeOverlayEnum, Integer>() {{
            put(BiomeOverlayEnum.NORMAL, 1);
        }}));
        DEFAULTS_MAP.put("moon.moon", new StargateDimensionConfigEntry("moon.moon", 15, new ArrayList<>()));
        DEFAULTS_MAP.put("planet.mars", new StargateDimensionConfigEntry("planet.mars", 40, new ArrayList<>(), new HashMap<BiomeOverlayEnum, Integer>() {{
            put(BiomeOverlayEnum.NORMAL, 4);
        }}));
        DEFAULTS_MAP.put("planet.venus", new StargateDimensionConfigEntry("planet.venus", 37, new ArrayList<>(), new HashMap<BiomeOverlayEnum, Integer>() {{
            put(BiomeOverlayEnum.NORMAL, 4);
        }}));
        DEFAULTS_MAP.put("planet.asteroids", new StargateDimensionConfigEntry("planet.asteroids", 63, new ArrayList<>()));
    }

    public static EnergyRequiredToOperate getCost(int fromDimId, int toDimId) {
        StargateDimensionConfigEntry reqFrom = dimensionMap.get(fromDimId);
        StargateDimensionConfigEntry reqTo = dimensionMap.get(toDimId);

        if (reqFrom == null || reqTo == null) {
            JSG.error("Tried to get a cost of a non-existing dimension. This is a bug.");
            JSG.error("FromId: {}, ToId: {}, FromEntryNull: {}, ToEntryNull: {}", fromDimId, toDimId, reqFrom == null, reqTo == null);
            JSG.error("JSG dimension entries:{}{}", System.lineSeparator(), dimensionMap.entrySet().stream()
                    .map(en -> en.getKey() + " | " + en.getValue().toString())
                    .collect(Collectors.joining(System.lineSeparator()))
            );
            return EnergyRequiredToOperate.free();
        }

        EnergyRequiredToOperate energyRequired = EnergyRequiredToOperate.stargate();
        double dist = Math.abs((double) reqFrom.distance - reqTo.distance);
        return energyRequired.mul(dist);
    }

    public static boolean isGroupEqual(int fromDimId, int toDimId) {
        StargateDimensionConfigEntry reqFrom = dimensionMap.get(fromDimId);
        StargateDimensionConfigEntry reqTo = dimensionMap.get(toDimId);

        if (reqFrom == null || reqTo == null) {
            JSG.error("Tried to perform a group check for a non-existing dimension. This is a bug.");
            JSG.error("FromId: {}, ToId: {}, FromEntryNull: {}, ToEntryNull: {}", fromDimId, toDimId, reqFrom == null, reqTo == null);
            JSG.error("JSG dimension entries:{}{}", System.lineSeparator(), dimensionMap.entrySet().stream()
                    .map(en -> en.getKey() + " | " + en.getValue().toString())
                    .collect(Collectors.joining(System.lineSeparator()))
            );
            return false;
        }

        return reqFrom.isGroupEqual(reqTo);
    }

    public static int getOrigin(DimensionType dimIn, @Nullable BiomeOverlayEnum overlay) {
        if (overlay == null) overlay = BiomeOverlayEnum.NORMAL;
        DimensionType dim = fixDimType(dimIn);
        if (dim == null) return -1;
        if (dimensionMap == null) {
            try {
                reload();
            } catch (Exception ignored) {
                return -1;
            }
        }
        if (dimensionMap == null)
            return -1;
        StargateDimensionConfigEntry entry = dimensionMap.get(dim.getId());
        if (entry == null) return -1;
        if (!entry.milkyWayOrigins.containsKey(overlay)) return -1;
        return entry.milkyWayOrigins.get(overlay);
    }

    private static DimensionType fixDimType(DimensionType type) {
        return type.getName().equals("Nether") && type.getId() == -1 ? DimensionType.NETHER : type;
    }

    public static boolean netherOverworld8thSymbol() {
        return !isGroupEqual(DimensionType.OVERWORLD.getId(), DimensionType.NETHER.getId());
    }

    public static void reload() throws IOException {
        load(null);
        update();
    }

    public static void load(File modConfigDir) {
        dimensionMap = null;
        if (modConfigDir != null)
            dimensionConfigFile = new File(modConfigDir, "jsg/jsgDimensions_" + JSG.CONFIG_DIMENSIONS_VERSION + ".json");

        try {
            Type typeOfHashMap = new TypeToken<Map<Integer, StargateDimensionConfigEntry>>() {
            }.getType();
            dimensionIntMap = new GsonBuilder().create().fromJson(new FileReader(dimensionConfigFile), typeOfHashMap);
        } catch (FileNotFoundException exception) {
            dimensionIntMap = new HashMap<>();
        }
    }

    public static void update() throws IOException {
        if (dimensionMap == null) {
            dimensionMap = new HashMap<>();

            for (Integer dimId : dimensionIntMap.keySet()) {
                try {
                    dimensionMap.put(dimId, dimensionIntMap.get(dimId));
                } catch (IllegalArgumentException ex) {
                    // Probably removed a mod
                    JSG.debug("DimensionType not found: " + dimId);
                }
            }
        }

        int originalSize = dimensionMap.size();

        for (Map.Entry<Integer, DimensionType> entry : DimensionsHelper.getRegisteredDimensions().entrySet()) {
            int dimId = entry.getKey();
            DimensionType dimType = entry.getValue();
            if (!dimensionMap.containsKey(dimId)) {
                // Biomes O' Plenty Nether fix
                if (dimType.getName().equals("Nether") && dimId == -1)
                    dimType = DimensionType.NETHER;

                if (DEFAULTS_MAP.containsKey(dimType.getName()))
                    dimensionMap.put(dimId, DEFAULTS_MAP.get(dimType.getName()));
                else
                    dimensionMap.put(dimId, new StargateDimensionConfigEntry(dimType.getName(), dimId * 5, null));
            }
        }

        if (originalSize != dimensionMap.size()) {
            FileWriter writer = new FileWriter(dimensionConfigFile);

            dimensionIntMap.clear();
            for (int dimId : dimensionMap.keySet()) {
                dimensionIntMap.put(dimId, dimensionMap.get(dimId));
            }

            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(dimensionIntMap));
            writer.close();
        }
    }
}
