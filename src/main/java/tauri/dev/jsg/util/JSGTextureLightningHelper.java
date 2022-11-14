package tauri.dev.jsg.util;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.config.JSGConfig;

public class JSGTextureLightningHelper {
    public static void lightUpTexture(World world, BlockPos pos, float lightIntensity){
        lightUpTexture((world.getCombinedLight(pos, (int) (lightIntensity * 15)))/15f);
    }

    /**
     * Set light of texture
     * @param lightIntensity Is 0-1F when 0 is the lowest light
     */
    public static void lightUpTexture(float lightIntensity){
        if(!JSGConfig.avConfig.renderEmissive) return;
        RenderHelper.enableStandardItemLighting();
        if(lightIntensity > 1) lightIntensity = 1;
        if(lightIntensity < 0) lightIntensity = 0;
        int i = Math.round(lightIntensity * 15);
        if(i < 1) return;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, ((float) ((i << 4) % 65536)), (float)0);
    }

    /**
     * Reset the light of the texture to light of other things in the world
     * @param world Is world of the tile entity
     * @param pos Is pos of the tile entity
     */
    public static void resetLight(World world, BlockPos pos){
        if(!JSGConfig.avConfig.renderEmissive) return;
        RenderHelper.enableStandardItemLighting();
        int i = world.getCombinedLight(pos, 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
    }
}
