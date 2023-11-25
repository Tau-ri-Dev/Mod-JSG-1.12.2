package tauri.dev.jsg.loader;

import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.loader.model.ModelLoader;
import tauri.dev.jsg.loader.texture.TextureLoader;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.function.Predicate;

public class ReloadListener implements ISelectiveResourceReloadListener {

    public static class LoadingStats{
        public static int errors = 0;
        public static int warnings = 0;
        public static int loadedTextures = 0;
        public static int notLoadedTextures = 0;
        public static int loadedModels = 0;
        public static int notLoadedModels = 0;
        public static boolean loadedAnimatedEHs = false;
        public static boolean loadedNewKawoosh = false;
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        OriginsLoader.NOT_LOADED_ORIGINS.clear();
        if (resourcePredicate.test(VanillaResourceType.MODELS)) {
            try {
                ModelLoader.reloadModels();
            } catch (IOException e) {
                JSG.error("Failed reloading models");
                e.printStackTrace();
            }
        }

        if (resourcePredicate.test(VanillaResourceType.TEXTURES)) {
            try {
                TextureLoader.reloadTextures(resourceManager);
            } catch (IOException e) {
                JSG.error("Failed reloading textures");
                e.printStackTrace();
            }
        }
    }

}
