package tauri.dev.jsg.config.stargate;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.power.stargate.StargateEnergyRequired;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StargateDimensionConfig {

    private static final Map<String, StargateDimensionConfigEntry> DEFAULTS_MAP = new HashMap<>();

    static {
        DEFAULTS_MAP.put("overworld", new StargateDimensionConfigEntry(0, 0, new ArrayList<String>(){{
            add("netherOv");
        }}));
        DEFAULTS_MAP.put("the_nether", new StargateDimensionConfigEntry(3686400, 1600, new ArrayList<String>(){{
            add("netherOv");
        }}, new HashMap<BiomeOverlayEnum, Integer>(){{
            put(BiomeOverlayEnum.NORMAL, 2);
        }}));
        DEFAULTS_MAP.put("the_end", new StargateDimensionConfigEntry(5529600, 2400, new ArrayList<>(), new HashMap<BiomeOverlayEnum, Integer>(){{
            put(BiomeOverlayEnum.NORMAL, 1);
        }}));
        DEFAULTS_MAP.put("moon.moon", new StargateDimensionConfigEntry(7372800, 3200, new ArrayList<>()));
        DEFAULTS_MAP.put("planet.mars", new StargateDimensionConfigEntry(11059200, 4800, new ArrayList<>(), new HashMap<BiomeOverlayEnum, Integer>(){{
            put(BiomeOverlayEnum.NORMAL, 4);
        }}));
        DEFAULTS_MAP.put("planet.venus", new StargateDimensionConfigEntry(12288000, 5334, new ArrayList<>(), new HashMap<BiomeOverlayEnum, Integer>(){{
            put(BiomeOverlayEnum.NORMAL, 4);
        }}));
        DEFAULTS_MAP.put("planet.asteroids", new StargateDimensionConfigEntry(14745600, 6400, new ArrayList<>()));
    }

    private static File dimensionConfigFile;
    private static Map<String, StargateDimensionConfigEntry> dimensionStringMap;
    private static Map<DimensionType, StargateDimensionConfigEntry> dimensionMap;

    public static StargateEnergyRequired getCost(DimensionType from, DimensionType to) {
        StargateDimensionConfigEntry reqFrom = dimensionMap.get(fixDimType(from));
        StargateDimensionConfigEntry reqTo = dimensionMap.get(fixDimType(to));

        if (reqFrom == null || reqTo == null) {
            JSG.error("Tried to get a cost of a non-existing dimension. This is a bug.");
            JSG.error("FromId: {}, FromName: {}, ToId: {}, ToName: {}, FromEntryNull: {}, ToEntryNull: {}", from.getId(), from.getName(), to.getId(), to.getName(), reqFrom == null, reqTo == null);
            JSG.error("JSG dimension entries:{}{}", System.lineSeparator(), dimensionMap.entrySet().stream()
                    .map(en -> en.getKey().getName() + " | " + en.getValue().toString())
                    .collect(Collectors.joining(System.lineSeparator()))
            );
            return new StargateEnergyRequired(0, 0);
        }

        int energyToOpen = Math.abs(reqFrom.energyToOpen - reqTo.energyToOpen);
        int keepAlive = Math.abs(reqFrom.keepAlive - reqTo.keepAlive);

        return new StargateEnergyRequired(energyToOpen, keepAlive);
    }

    public static boolean isGroupEqual(DimensionType from, DimensionType to) {
        StargateDimensionConfigEntry reqFrom = dimensionMap.get(fixDimType(from));
        StargateDimensionConfigEntry reqTo = dimensionMap.get(fixDimType(to));

        if (reqFrom == null || reqTo == null) {
            JSG.error("Tried to perform a group check for a non-existing dimension. This is a bug.");
            JSG.error("FromId: {}, FromName: {}, ToId: {}, ToName: {}, FromEntryNull: {}, ToEntryNull: {}", from.getId(), from.getName(), to.getId(), to.getName(), reqFrom == null, reqTo == null);
            JSG.error("JSG dimension entries:{}{}", System.lineSeparator(), dimensionMap.entrySet().stream()
                    .map(en -> en.getKey().getName() + " | " + en.getValue().toString())
                    .collect(Collectors.joining(System.lineSeparator()))
            );
            return false;
        }

        return reqFrom.isGroupEqual(reqTo);
    }

    public static int getOrigin(DimensionType dimIn, @Nullable BiomeOverlayEnum overlay) {
        if(overlay == null) overlay = BiomeOverlayEnum.NORMAL;
        DimensionType dim = fixDimType(dimIn);
        if(dim == null) return -1;
        if(dimensionMap == null){
            try{
                update();
            }catch (Exception ignored){}
            return -1;
        }
        StargateDimensionConfigEntry entry = dimensionMap.get(dim);
        if(entry == null) return -1;
        if(!entry.milkyWayOrigins.containsKey(overlay)) return -1;
        return entry.milkyWayOrigins.get(overlay);
    }

    private static DimensionType fixDimType(DimensionType type) {
        return type.getName().equals("Nether") && type.getId() == -1 ? DimensionType.NETHER : type;
    }

    public static boolean netherOverworld8thSymbol() {
        return !isGroupEqual(DimensionType.OVERWORLD, DimensionType.NETHER);
    }

    public static void load(File modConfigDir) {
        dimensionMap = null;
        dimensionConfigFile = new File(modConfigDir, "jsg/jsgDimensions_" + JSG.CONFIG_DIMENSIONS_VERSION + ".json");

        try {
            Type typeOfHashMap = new TypeToken<Map<String, StargateDimensionConfigEntry>>() {
            }.getType();
            dimensionStringMap = new GsonBuilder().create().fromJson(new FileReader(dimensionConfigFile), typeOfHashMap);
        } catch (FileNotFoundException exception) {
            dimensionStringMap = new HashMap<>();
        }
    }

    public static void update() throws IOException {
        if (dimensionMap == null) {
            dimensionMap = new HashMap<>();

            for (String dimName : dimensionStringMap.keySet()) {
                try {
                    dimensionMap.put(DimensionType.byName(dimName), dimensionStringMap.get(dimName));
                } catch (IllegalArgumentException ex) {
                    // Probably removed a mod
                    JSG.debug("DimensionType not found: " + dimName);
                }
            }
        }

        int originalSize = dimensionMap.size();

        for (DimensionType dimType : DimensionManager.getRegisteredDimensions().keySet()) {
            if (!dimensionMap.containsKey(dimType)) {
                // Biomes O' Plenty Nether fix
                if (dimType.getName().equals("Nether") && dimType.getId() == -1)
                    dimType = DimensionType.NETHER;

                if (DEFAULTS_MAP.containsKey(dimType.getName()))
                    dimensionMap.put(dimType, DEFAULTS_MAP.get(dimType.getName()));
                else
                    dimensionMap.put(dimType, new StargateDimensionConfigEntry(0, 0, null));
            }
        }

        if (originalSize != dimensionMap.size()) {
            FileWriter writer = new FileWriter(dimensionConfigFile);

            dimensionStringMap.clear();
            for (DimensionType dimType : dimensionMap.keySet()) {
                dimensionStringMap.put(dimType.getName(), dimensionMap.get(dimType));
            }

            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(dimensionStringMap));
            writer.close();
        }
    }
}
