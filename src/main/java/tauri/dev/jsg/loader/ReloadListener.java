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

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
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
