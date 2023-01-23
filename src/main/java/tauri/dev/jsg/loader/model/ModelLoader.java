package tauri.dev.jsg.loader.model;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.origins.OriginsLoader;
import tauri.dev.jsg.loader.FolderLoader;
import tauri.dev.jsg.loader.ReloadListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelLoader {

    public static final String MODELS_PATH = "assets/jsg/models/tesr";
    private static final Map<ResourceLocation, OBJModel> LOADED_MODELS = new HashMap<>();

    public static OBJModel getModel(ResourceLocation resourceLocation) {
        return LOADED_MODELS.get(resourceLocation);
    }

    public static void reloadModels() throws IOException {
        LOADED_MODELS.clear();

        List<String> modelPaths = FolderLoader.getAllFiles(MODELS_PATH, ".obj");

        for (String poo : JSGConfig.originsConfig.additionalOrigins) {
            int i = Integer.parseInt(poo.split(":")[0]);
            String s = "assets/jsg/models/tesr/milkyway/origin_" + i + ".obj";
            if (!modelPaths.contains(s)) {
                modelPaths.add(s);
            }
            s = "assets/jsg/models/tesr/milkyway/origin_" + i + "_light.obj";
            if (!modelPaths.contains(s)) {
                modelPaths.add(s);
            }
            s = "assets/jsg/models/tesr/milkyway/ring/origin_" + i + ".obj";
            if (!modelPaths.contains(s)) {
                modelPaths.add(s);
            }
        }

        ProgressBar progressBar = ProgressManager.push("JSG - General models", modelPaths.size());

        long start = System.currentTimeMillis();

        JSG.info("Started loading models...");
        for (String modelPath : modelPaths) {
            String modelResourcePath = modelPath.replaceFirst("assets/jsg/", "");
            if (JSGConfig.debugConfig.logTexturesLoading)
                JSG.info("Loading model: " + modelResourcePath);
            progressBar.step(modelResourcePath.replaceFirst("models/", ""));

            InputStream stream = JSG.class.getClassLoader().getResourceAsStream(modelPath);
            OBJModel model = OBJLoader.loadModel(stream);
            if (model == null){
                ReloadListener.LoadingStats.notLoadedModels++;
                continue;
            }
            LOADED_MODELS.put(new ResourceLocation(JSG.MOD_ID, modelResourcePath), model);
            ReloadListener.LoadingStats.loadedModels++;
        }

        OriginsLoader.loadModels(LOADED_MODELS);

        JSG.info("Loaded " + modelPaths.size() + " models in " + (System.currentTimeMillis() - start) + " ms");

        ProgressManager.pop(progressBar);
    }

    public static ResourceLocation getModelResource(String model) {
        return new ResourceLocation(JSG.MOD_ID, "models/tesr/" + model);
    }
}
