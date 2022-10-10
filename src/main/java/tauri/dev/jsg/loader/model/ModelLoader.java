package tauri.dev.jsg.loader.model;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.loader.FolderLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;

import java.io.IOException;
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
		ProgressBar progressBar = ProgressManager.push("Just Stargate Mod - Loading models", modelPaths.size());
		
		long start = System.currentTimeMillis();

		JSG.logger.info("Started loading models...");
		for (String modelPath : modelPaths) {
			if(JSGConfig.debugConfig.logTexturesLoading)
				JSG.logger.info("Loading model: " + modelPath);
			String modelResourcePath = modelPath.replaceFirst("assets/jsg/", "");
			progressBar.step(modelResourcePath.replaceFirst("models/", ""));
			LOADED_MODELS.put(new ResourceLocation(JSG.MOD_ID, modelResourcePath), OBJLoader.loadModel(modelPath));
		}
		
		JSG.logger.info("Loaded "+modelPaths.size()+" models in "+(System.currentTimeMillis()-start)+" ms");
		
		ProgressManager.pop(progressBar);
	}
	
	public static ResourceLocation getModelResource(String model) {
		return new ResourceLocation(JSG.MOD_ID, "models/tesr/" + model);
	}
}
