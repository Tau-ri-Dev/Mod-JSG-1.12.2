package tauri.dev.jsg.loader;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ProgressManager;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.JSGConfigUtil;
import tauri.dev.jsg.loader.model.OBJLoader;
import tauri.dev.jsg.loader.model.OBJModel;
import tauri.dev.jsg.loader.texture.Texture;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OriginsLoader {
    public static final String MODELS_PATH = "assets/models/origins/";
    public static final String TEXTURES_PATH = "assets/textures/origins/";
    public static final String LOADER_PATH = "assets/loader/";

    public static final String RING_END = "_ring.obj";
    public static final String DHD_END = "_dhd.obj";
    public static final String DHD_LIGHT_END = "_dhd_light.obj";
    public static final String TEXTURE_END = ".png";

    public static final String BASE = "origin_";

    public enum EnumOriginFileType {
        MODEL_RING,
        MODEL_DHD,
        MODEL_DHD_LIGHT,
        TEXTURE
    }

    public static File getOriginFile(EnumOriginFileType fileType, int originId) {
        switch (fileType) {
            case MODEL_RING:
                return new File(JSG.modConfigDir, "jsg/" + MODELS_PATH + BASE + originId + RING_END);
            case MODEL_DHD:
                return new File(JSG.modConfigDir, "jsg/" + MODELS_PATH + BASE + originId + DHD_END);
            case MODEL_DHD_LIGHT:
                return new File(JSG.modConfigDir, "jsg/" + MODELS_PATH + BASE + originId + DHD_LIGHT_END);
            default:
                return new File(JSG.modConfigDir, "jsg/" + TEXTURES_PATH + BASE + originId + TEXTURE_END);
        }
    }

    public static final ArrayList<Integer> NOT_LOADED_ORIGINS = new ArrayList<>();
    public static final int DEFAULT_ORIGIN_ID = 5;
    public static final int MOD_POINT_OF_ORIGINS_COUNT = 6;

    public static List<Integer> getAllOrigins() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < MOD_POINT_OF_ORIGINS_COUNT; i++) {
            list.add(i);
        }
        for (String s : JSGConfig.Stargate.pointOfOrigins.additionalOrigins) {
            int id = Integer.parseInt(s.split(":")[0]);
            list.add(id);
        }
        return list;
    }

    private static void checkDirectory() {
        try {
            File models = new File(JSG.modConfigDir, "jsg/" + MODELS_PATH);
            File textures = new File(JSG.modConfigDir, "jsg/" + TEXTURES_PATH);
            File loader = new File(JSG.modConfigDir, "jsg/" + LOADER_PATH);
            if (!models.exists())
                Files.createDirectories(models.toPath());
            if (!textures.exists())
                Files.createDirectories(textures.toPath());
            if (!loader.exists())
                Files.createDirectories(loader.toPath());
        } catch (Exception e) {
            JSG.error("Error while creating folders for custom resources!", e);
        }
    }

    public static void registerTextures(Map<ResourceLocation, Texture> texturesArray) throws IOException {
        checkDirectory();
        if (JSGConfig.Stargate.pointOfOrigins.additionalOrigins.length < 1) return;

        ProgressManager.ProgressBar progressBar = ProgressManager.push("JSG - Custom PoO models", JSGConfig.Stargate.pointOfOrigins.additionalOrigins.length);
        for (String s : JSGConfig.Stargate.pointOfOrigins.additionalOrigins) {
            int id = Integer.parseInt(s.split(":")[0]);
            progressBar.step("Origin " + id);
            if (!getOriginFile(EnumOriginFileType.TEXTURE, id).exists()) {
                NOT_LOADED_ORIGINS.add(id);
                JSG.error("Origin texture not found! [" + id + "]");
                continue;
            }
            texturesArray.put(getResource(EnumOriginFileType.TEXTURE, id), new Texture(TextureUtil.readBufferedImage(Files.newInputStream(getOriginFile(EnumOriginFileType.TEXTURE, id).toPath()))));
        }
        ProgressManager.pop(progressBar);
    }

    public static void loadModels(Map<ResourceLocation, OBJModel> modelsArray) throws IOException {
        checkDirectory();
        if (JSGConfig.Stargate.pointOfOrigins.additionalOrigins.length < 1) return;

        ProgressManager.ProgressBar progressBar = ProgressManager.push("JSG - Custom PoO models", JSGConfig.Stargate.pointOfOrigins.additionalOrigins.length);
        for (String s : JSGConfig.Stargate.pointOfOrigins.additionalOrigins) {
            int id = Integer.parseInt(s.split(":")[0]);
            progressBar.step("Origin " + id);
            if (!getOriginFile(EnumOriginFileType.MODEL_DHD, id).exists() || !getOriginFile(EnumOriginFileType.MODEL_RING, id).exists() || !getOriginFile(EnumOriginFileType.MODEL_DHD_LIGHT, id).exists()) {
                NOT_LOADED_ORIGINS.add(id);
                JSG.error("Origin model not found! [" + id + "]");
                continue;
            }
            modelsArray.put(getResource(EnumOriginFileType.MODEL_DHD, id), OBJLoader.loadModel(Files.newInputStream(getOriginFile(EnumOriginFileType.MODEL_DHD, id).toPath())));
            modelsArray.put(getResource(EnumOriginFileType.MODEL_RING, id), OBJLoader.loadModel(Files.newInputStream(getOriginFile(EnumOriginFileType.MODEL_RING, id).toPath())));
            modelsArray.put(getResource(EnumOriginFileType.MODEL_DHD_LIGHT, id), OBJLoader.loadModel(Files.newInputStream(getOriginFile(EnumOriginFileType.MODEL_DHD_LIGHT, id).toPath())));
        }
        ProgressManager.pop(progressBar);
    }

    public static ResourceLocation getResource(EnumOriginFileType fileType, int originId) {
        if (NOT_LOADED_ORIGINS.contains(originId))
            originId = DEFAULT_ORIGIN_ID;

        if (fileType != EnumOriginFileType.TEXTURE)
            return new ResourceLocation(JSG.MOD_ID, "models/tesr/milkyway/" + (fileType == EnumOriginFileType.MODEL_RING ? "ring/" : "") + "origin_" + originId + (fileType == EnumOriginFileType.MODEL_DHD_LIGHT ? "_light" : "") + ".obj");
        return new ResourceLocation(JSG.MOD_ID, "textures/gui/symbol/milkyway/origin_" + originId + ".png");
    }

    public static boolean loadOriginsToConfig(boolean rewrite) {
        checkDirectory();
        File loaderFile = new File(JSG.modConfigDir, "jsg/" + LOADER_PATH + "origins.json");
        try {
            Type typeOfHashMap = new TypeToken<Map<String, List<String>>>() {
            }.getType();
            Map<String, List<String>> map = new GsonBuilder().create().fromJson(new FileReader(loaderFile), typeOfHashMap);
            List<String> list = map.get("list");
            int size = list.size();
            if(!rewrite){
                size += JSGConfig.Stargate.pointOfOrigins.additionalOrigins.length;
                for(String s : JSGConfig.Stargate.pointOfOrigins.additionalOrigins){
                    if(!list.contains(s))
                        list.add(s);
                    else
                        size--;
                }
            }
            String[] toConfig = new String[size];
            int i = 0;
            for (String s : list) {
                toConfig[i] = s;
                i++;
            }
            JSGConfig.Stargate.pointOfOrigins.additionalOrigins = toConfig;
            JSGConfigUtil.reloadConfig();
            return true;
        } catch (Exception e) {
            JSG.warn("Error ", e);
        }
        return false;
    }
}
