package mrjake.aunis.renderer.energy;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.tileentity.energy.ZPMHubTile;
import mrjake.aunis.tileentity.energy.ZPMTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class ZPMHubRenderer extends TileEntitySpecialRenderer<ZPMHubTile> {


    @Override
    public void render(ZPMHubTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ZPMHubRendererState rendererState = (ZPMHubRendererState) te.getRendererStateClient();

        if (rendererState != null) {
            IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
            if (state.getBlock() != AunisBlocks.ZPM_HUB) return;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            double scale = 0.027;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(18.0, 23.5, 20);

            ElementEnum.ZPM_HUB.bindTextureAndRender(BiomeOverlayEnum.NORMAL);

            // TODO(Mine): do this shitty shit
            for(int i = 0; i < 3; i++) {
                if(AunisConfig.devConfig.zpmHubZpmsX.length >= i+1) {
                    double zx = AunisConfig.devConfig.zpmHubZpmsX[i];
                    double zy = AunisConfig.devConfig.zpmHubZpmsY[i];
                    double zz = AunisConfig.devConfig.zpmHubZpmsZ[i];
                    renderZPM(x, y, z, zx, zy, zz, i);
                }
            }

            GlStateManager.popMatrix();
        }
    }

    public void renderZPM(double x, double y, double z, double xx, double xy, double xz, int powerLevel) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        double scale = AunisConfig.devConfig.zpmsSize;
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(xx, xy, xz);
        if(powerLevel > 4){
            powerLevel = 4;
        }
        if(powerLevel < 0){
            powerLevel = 0;
        }
        rendererDispatcher.renderEngine.bindTexture(new ResourceLocation(Aunis.ModID, "textures/tesr/zpm/item/zpm_" + powerLevel + ".png"));
        ModelLoader.getModel(new ResourceLocation(Aunis.ModID, "models/tesr/zpm/zpm.obj")).render();
        GlStateManager.popMatrix();
    }
}
