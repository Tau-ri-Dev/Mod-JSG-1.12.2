package tauri.dev.jsg.loader.texture;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;

import java.awt.image.BufferedImage;

public class Texture {

    private final int textureId;

    public Texture(BufferedImage bufferedImage) {
        this.textureId = TextureUtil.glGenTextures();
        TextureUtil.uploadTextureImageAllocate(textureId, bufferedImage, false, false);
    }

    public void deleteTexture() {
        TextureUtil.deleteTexture(textureId);
    }

    public void bindTexture() {
        GlStateManager.bindTexture(textureId);
    }
}