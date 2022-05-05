package mrjake.aunis.loader.model;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.FolderLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelLoader {
	
	public static final String MODELS_PATH = "assets/aunis/models/tesr";
	private static final Map<ResourceLocation, OBJModel> LOADED_MODELS = new HashMap<>();
	
	public static OBJModel getModel(ResourceLocation resourceLocation) {
		return LOADED_MODELS.get(resourceLocation);
	}

	public static void reloadModels() throws IOException {
		LOADED_MODELS.clear();
		
		List<String> modelPaths = FolderLoader.getAllFiles(MODELS_PATH, ".obj");
		ProgressBar progressBar = ProgressManager.push("Aunis: Resurrection - Loading models", modelPaths.size());
		
		long start = System.currentTimeMillis();

		Aunis.logger.info("Started loading models...");
		for (String modelPath : modelPaths) {
			if(AunisConfig.debugConfig.logTexturesLoading)
				Aunis.logger.info("Loading model: " + modelPath);
			String modelResourcePath = modelPath.replaceFirst("assets/aunis/", "");
			progressBar.step(modelResourcePath.replaceFirst("models/", ""));
			LOADED_MODELS.put(new ResourceLocation(Aunis.MOD_ID, modelResourcePath), OBJLoader.loadModel(modelPath));
		}
		
		Aunis.logger.info("Loaded "+modelPaths.size()+" models in "+(System.currentTimeMillis()-start)+" ms");
		
		ProgressManager.pop(progressBar);
	}
	
	public static ResourceLocation getModelResource(String model) {
		return new ResourceLocation(Aunis.MOD_ID, "models/tesr/" + model);
	}
}
