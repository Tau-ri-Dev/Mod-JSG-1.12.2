package tauri.dev.jsg.loader;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.loader.model.ModelLoader;
import tauri.dev.jsg.loader.texture.TextureLoader;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;

import java.io.IOException;
import java.util.function.Predicate;

public class ReloadListener implements ISelectiveResourceReloadListener {

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {		
    	try {
			if (resourcePredicate.test(VanillaResourceType.MODELS)) {
				ModelLoader.reloadModels();
			}
			
			if (resourcePredicate.test(VanillaResourceType.TEXTURES)) {
				TextureLoader.reloadTextures(resourceManager);
			}
    	}
    	
    	catch (IOException e) {
    		JSG.logger.error("Failed reloading resources");
    		e.printStackTrace();
    	}
	}

}
