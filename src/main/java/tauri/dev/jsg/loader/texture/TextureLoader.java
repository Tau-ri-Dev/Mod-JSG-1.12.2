package tauri.dev.jsg.loader.texture;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import org.apache.commons.io.IOUtils;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.origins.OriginsLoader;
import tauri.dev.jsg.loader.FolderLoader;
import tauri.dev.jsg.loader.ReloadListener;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureLoader {

    public static final String TEXTURES_PATH = "assets/jsg/textures/tesr";
    private static final int EH_ANIMATED_TEXTURE_SUB_TEXTURES = 185;
    private static final Map<ResourceLocation, Texture> LOADED_TEXTURES = new HashMap<>();

    public static Texture getTexture(ResourceLocation resourceLocation) {
        return LOADED_TEXTURES.get(resourceLocation);
    }

    /**
     * Checks if the texture is loaded. If not, it probably doesn't exist.
     *
     * @return True if the texture exists and it's loaded, False otherwise.
     */
    public static boolean isNotTextureLoaded(ResourceLocation resourceLocation) {
        return !LOADED_TEXTURES.containsKey(resourceLocation);
    }

    public static void reloadTextures(IResourceManager resourceManager) throws IOException {
        for (Texture texture : LOADED_TEXTURES.values())
            texture.deleteTexture();

        OriginsLoader.registerTextures(LOADED_TEXTURES);

        // ----------------------------------
        // INIT
        List<String> texturePaths = new ArrayList<>();
        List<String> ehPaths = new ArrayList<>();
        for (String texturePath : FolderLoader.getAllFiles(TEXTURES_PATH, ".png", ".jpg")) {
            texturePath = texturePath.replaceFirst("assets/jsg/", "");
            switch (texturePath) {
                case "textures/tesr/event_horizon_animated_unstable.jpg":
                case "textures/tesr/event_horizon_animated.jpg":
                case "textures/tesr/event_horizon_animated_kawoosh.jpg":
                case "textures/tesr/event_horizon_animated_kawoosh_unstable.jpg":
                    ehPaths.add(texturePath);
                    break;
                default:
                    texturePaths.add(texturePath);
                    break;
            }
        }
        // ----------------------------------

        // ----------------------------------
        // LOAD NORMAL TEXTURES
        ProgressBar progressBar = ProgressManager.push("JSG - General textures", texturePaths.size());
        long start = System.currentTimeMillis();
        JSG.info("Started loading textures...");
        for (String texturePath : texturePaths) {
            loadTexture(progressBar, texturePath, resourceManager);
        }
        JSG.info("Loaded " + texturePaths.size() + " textures in " + (System.currentTimeMillis() - start) + " ms");
        ProgressManager.pop(progressBar);
        // ----------------------------------

        // ----------------------------------
        // LOAD EVENT HORIZONS
        if (!JSGConfig.Stargate.eventHorizon.disableAnimatedEventHorizon && !JSGConfig.General.devConfig.enableDevMode) {
            progressBar = ProgressManager.push("JSG - Animated textures", ehPaths.size());
            start = System.currentTimeMillis();
            JSG.info("Started loading event horizon textures...");
            for (String texturePath : ehPaths) {
                switch (texturePath) {
                    case "textures/tesr/event_horizon_animated_kawoosh.jpg":
                    case "textures/tesr/event_horizon_animated_kawoosh_unstable.jpg":
                        loadEH(progressBar, texturePath, resourceManager);
                        break;
                    default:
                        loadTexture(progressBar, texturePath, resourceManager);
                        break;
                }
            }
            JSG.info("Loaded " + ehPaths.size() + " textures in " + (System.currentTimeMillis() - start) + " ms");
            ProgressManager.pop(progressBar);
        } else {
            if (JSGConfig.General.debug.logTexturesLoading)
                JSG.info("Skipping loading EH textures!");
        }
        // ----------------------------------
    }

    private static void loadTexture(ProgressBar progressBar, String texturePath, IResourceManager resourceManager) {
        progressBar.step(texturePath.replaceFirst("textures/tesr/", ""));
        ResourceLocation resourceLocation = new ResourceLocation(JSG.MOD_ID, texturePath);
        IResource resource = null;
        try {
            resource = resourceManager.getResource(resourceLocation);
            if (JSGConfig.General.debug.logTexturesLoading)
                JSG.info("Loading texture: " + texturePath);
            BufferedImage bufferedImage = TextureUtil.readBufferedImage(resource.getInputStream());
            LOADED_TEXTURES.put(resourceLocation, new Texture(bufferedImage));
            ReloadListener.LoadingStats.loadedTextures++;
        } catch (IOException e) {
            JSG.error("Failed to load texture " + texturePath);
            e.printStackTrace();
            ReloadListener.LoadingStats.notLoadedTextures++;
        } finally {
            IOUtils.closeQuietly(resource);
        }
    }

    private static void loadEH(ProgressBar progressBar, String texturePath, IResourceManager resourceManager) {
        ReloadListener.LoadingStats.loadedAnimatedEHs = true;
        progressBar.step(texturePath.replaceFirst("textures/tesr/", ""));
        ResourceLocation resourceLocation = new ResourceLocation(JSG.MOD_ID, texturePath);
        IResource resource = null;
        try {
            resource = resourceManager.getResource(resourceLocation);
            if (JSGConfig.General.debug.logTexturesLoading)
                JSG.info("Loading texture: " + texturePath);

            BufferedImage bufferedImage = TextureUtil.readBufferedImage(resource.getInputStream());

            LOADED_TEXTURES.put(resourceLocation, new Texture(bufferedImage));
            ReloadListener.LoadingStats.loadedTextures++;

            if(!JSGConfig.Stargate.eventHorizon.disableNewKawoosh) {

                ProgressBar subProgressBar = ProgressManager.push("JSG - Event Horizon Sub-Textures", EH_ANIMATED_TEXTURE_SUB_TEXTURES);

                final int onePiece = bufferedImage.getWidth() / 14;

                for (int i = 0; i < EH_ANIMATED_TEXTURE_SUB_TEXTURES; i++) {
                    int texIndex = (i % EH_ANIMATED_TEXTURE_SUB_TEXTURES);
                    int x = texIndex % 14;
                    int y = texIndex / 14;
                    String subPath = (texturePath + "_" + x + "." + y);

                    subProgressBar.step(x + ":" + y);
                    if (JSGConfig.General.debug.logTexturesLoading)
                        JSG.info("Loading sub-texture: " + subPath);

                    BufferedImage texturePart = bufferedImage.getSubimage(x * onePiece, y * onePiece, onePiece, onePiece);
                    LOADED_TEXTURES.put(new ResourceLocation(JSG.MOD_ID, subPath), new Texture(texturePart));
                    ReloadListener.LoadingStats.loadedTextures++;
                }

                ReloadListener.LoadingStats.loadedNewKawoosh = true;
                ProgressManager.pop(subProgressBar);
            }
        } catch (IOException e) {
            JSG.error("Failed to load texture " + texturePath);
            e.printStackTrace();
            ReloadListener.LoadingStats.notLoadedTextures++;
        } finally {
            IOUtils.closeQuietly(resource);
        }
    }

    public static ResourceLocation getTextureResource(String texture) {
        return new ResourceLocation(JSG.MOD_ID, "textures/tesr/" + texture);
    }

    public static ResourceLocation getBlockTexture(IBlockState blockState) {

        Minecraft minecraft = Minecraft.getMinecraft();
        BlockRendererDispatcher ren = minecraft.getBlockRendererDispatcher();
        String blockTexture = ren.getModelForState(blockState).getQuads(blockState, EnumFacing.NORTH, 0).get(0).getSprite().getIconName();
        String domain = "minecraft";
        String path = blockTexture;
        int domainSeparator = blockTexture.indexOf(':');

        if (domainSeparator >= 0) {
            path = blockTexture.substring(domainSeparator + 1);

            if (domainSeparator > 1) {
                domain = blockTexture.substring(0, domainSeparator);
            }
        }

        String resourcePath = "textures/" + path + ".png";  // base path and PNG are hardcoded in Minecraft
        return new ResourceLocation(domain.toLowerCase(), resourcePath);
    }
}
