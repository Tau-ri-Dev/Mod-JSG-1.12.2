package tauri.dev.jsg.util;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import tauri.dev.jsg.config.JSGConfig;

import java.util.ArrayList;

public class JSGTextureLightningHelper {

    protected static boolean disabled = false;
    public static void enable(){
        disabled = false;
    }
    public static void disable(){
        disabled = true;
    }

    public static void lightUpTexture(World world, BlockPos pos, float lightIntensity) {
        if(disabled) return;
        lightUpTexture(world, new ArrayList<BlockPos>() {{
            add(pos);
        }}, lightIntensity);
    }

    public static void lightUpTexture(World world, ArrayList<BlockPos> poses, float lightIntensity) {
        if(disabled) return;
        if (!JSGConfig.General.visual.renderEmissive) return;
        final int count = poses.size();
        int skyLight = 0;
        int blockLight = 0;

        for (BlockPos pos : poses) {
            skyLight += world.getLightFor(EnumSkyBlock.SKY, pos);
            blockLight += world.getLightFor(EnumSkyBlock.BLOCK, pos);
        }

        skyLight /= count;
        blockLight /= count;
        float i = (lightIntensity * 15);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, Math.max(blockLight * 16, i), Math.max(skyLight * 16, i));
    }

    /**
     * Set light of texture
     *
     * @param lightIntensity Is 0-1F when 0 is the lowest light
     */
    public static void lightUpTexture(float lightIntensity) {
        if(disabled) return;
        if (!JSGConfig.General.visual.renderEmissive) return;
        RenderHelper.enableStandardItemLighting();
        if (lightIntensity > 1) lightIntensity = 1;
        if (lightIntensity < 0) lightIntensity = 0;
        int i = Math.round(lightIntensity * 15);
        if (i < 1) return;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i * 16, i * 16);
    }

    /**
     * Reset the light of the texture to light of other things in the world
     *
     * @param world Is world of the tile entity
     * @param pos   Is pos of the tile entity
     */
    public static void resetLight(World world, BlockPos pos) {
        if(disabled) return;
        resetLight(world, new ArrayList<BlockPos>() {{
            add(pos);
        }});
    }

    public static void resetLight(World world, ArrayList<BlockPos> poses) {
        if(disabled) return;
        //if (!Loader.isModLoaded("optifine")) return;
        final int count = poses.size();
        int skyLight = 0;
        int blockLight = 0;

        for (BlockPos pos : poses) {
            skyLight += world.getLightFor(EnumSkyBlock.SKY, pos);
            blockLight += world.getLightFor(EnumSkyBlock.BLOCK, pos);
        }

        skyLight /= count;
        blockLight /= count;
        RenderHelper.enableStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (blockLight * 16), (skyLight * 16));
    }
}
