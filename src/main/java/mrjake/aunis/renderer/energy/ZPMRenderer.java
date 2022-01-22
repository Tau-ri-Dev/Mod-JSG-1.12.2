package mrjake.aunis.renderer.energy;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.tileentity.BeamerTile;
import mrjake.aunis.tileentity.energy.ZPMTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class ZPMRenderer extends TileEntitySpecialRenderer<ZPMTile> {


    @Override
    public void render(ZPMTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ZPMRendererState rendererState = (ZPMRendererState) te.getRendererStateClient();

        if (rendererState != null) {
            IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
            if (state.getBlock() != AunisBlocks.ZPM) return;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            double scale = 0.035;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(14.5, 7.1, 14.5);

            /* //Used only in ZPMHub
            int powerLevel = Math.round((float) (te.getPowerLevel()/2));
            int powerLevel = 0;

            if(powerLevel > 4){
                powerLevel = 4;
            }
            if(powerLevel < 0){
                powerLevel = 0;
            }*/

            int powerLevel = 0;

            rendererDispatcher.renderEngine.bindTexture(new ResourceLocation(Aunis.ModID, "textures/tesr/zpm/item/zpm_" + powerLevel + ".png"));
            ModelLoader.getModel(new ResourceLocation(Aunis.ModID, "models/tesr/zpm/zpm.obj")).render();

            //ElementEnum.ZPM.bindTextureAndRender(BiomeOverlayEnum.NORMAL);

            GlStateManager.popMatrix();
        }
    }
}
