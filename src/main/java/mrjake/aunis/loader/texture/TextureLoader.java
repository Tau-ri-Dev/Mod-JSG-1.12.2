package mrjake.aunis.loader.texture;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.FolderLoader;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureLoader {

	public static final String TEXTURES_PATH = "assets/aunis/textures/tesr";
	private static final Map<ResourceLocation, Texture> LOADED_TEXTURES = new HashMap<>();
	
	public static Texture getTexture(ResourceLocation resourceLocation) {
		return LOADED_TEXTURES.get(resourceLocation);
	}
	
	/**
	 * Checks if the texture is loaded. If not, it probably doesn't exist.
	 * 
	 * @return True if the texture exists and it's loaded, False otherwise.
	 */
	public static boolean isTextureLoaded(ResourceLocation resourceLocation) {
		return LOADED_TEXTURES.containsKey(resourceLocation);
	}
	
	public static void reloadTextures(IResourceManager resourceManager) throws IOException {		
		for (Texture texture : LOADED_TEXTURES.values())
			texture.deleteTexture();
		
		List<String> texturePaths = FolderLoader.getAllFiles(TEXTURES_PATH, ".png", ".jpg");
		ProgressBar progressBar = ProgressManager.push("Aunis: Resurrection - Loading textures", texturePaths.size());
		
		long start = System.currentTimeMillis();

		Aunis.logger.info("Started loading textures...");

		for (String texturePath : texturePaths) {
			texturePath = texturePath.replaceFirst("assets/aunis/", "");
			progressBar.step(texturePath.replaceFirst("textures/", ""));
			
			if (AunisConfig.horizonConfig.disableAnimatedEventHorizon){
				switch (texturePath){
					case "textures/tesr/event_horizon_animated_unstable.jpg":
					case "textures/tesr/event_horizon_animated.jpg":
					case "textures/tesr/event_horizon_animated_kawoosh.jpg":
					case "textures/tesr/event_horizon_animated_kawoosh_unstable.jpg":
						if(AunisConfig.debugConfig.logTexturesLoading)
							Aunis.logger.info("Skipping: " + texturePath);
						continue;
				}
			}
						
			ResourceLocation resourceLocation = new ResourceLocation(Aunis.MOD_ID, texturePath);
			IResource resource = null;
			
			try {
				resource = resourceManager.getResource(resourceLocation);
				if(AunisConfig.debugConfig.logTexturesLoading)
					Aunis.logger.info("Loading texture: " + texturePath);
				BufferedImage bufferedImage = TextureUtil.readBufferedImage(resource.getInputStream());
				LOADED_TEXTURES.put(resourceLocation, new Texture(bufferedImage, false));
				
				/*
				Shit that lags PCs

				if (texturePath.equals("textures/tesr/event_horizon_animated.jpg")){
					LOADED_TEXTURES.put(new ResourceLocation(Aunis.ModID, texturePath+"_desaturated"), new Texture(bufferedImage, true));
				}*/
			}
			
			catch (IOException e) {
				Aunis.logger.error("Failed to load texture " + texturePath);
				e.printStackTrace();
			}
			
			finally {
	            IOUtils.closeQuietly((Closeable)resource);
			}
		}
		
		Aunis.logger.info("Loaded "+texturePaths.size()+" textures in "+(System.currentTimeMillis()-start)+" ms");
		
		ProgressManager.pop(progressBar);
	}

	public static ResourceLocation getTextureResource(String texture) {
		return new ResourceLocation(Aunis.MOD_ID, "textures/tesr/" + texture);
	}	
}
