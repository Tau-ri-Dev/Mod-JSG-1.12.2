package mrjake.aunis.renderer.energy;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.tileentity.energy.ZPMTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class ZPMRenderer<S extends ZPMRendererState> extends TileEntitySpecialRenderer<ZPMTile> {


    @Override
    public void render(ZPMTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        @SuppressWarnings("unchecked") S rendererState = (S) te.getRendererStateClient();

        if (rendererState != null) {
            IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
            if (state.getBlock() != AunisBlocks.ZPM) return;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            //todo(Mine): fix possition and scale

            GlStateManager.scale(0.05, 0.05, 0.05);
            GlStateManager.scale(1.3, 1.3, 1.3);

            GlStateManager.translate(0.5, 0.5, 0.5);

            int powerLevel = (int) Math.round((float) rendererState.charge/2);
            powerLevel = (5 - powerLevel); // can be 0 - 4

            if(powerLevel > 4){
                powerLevel = 4;
            }
            if(powerLevel < 0){
                powerLevel = 0;
            }

            rendererDispatcher.renderEngine.bindTexture(new ResourceLocation(Aunis.ModID, "textures/tesr/zpm/item/zpm_" + powerLevel + ".png"));
            ModelLoader.getModel(new ResourceLocation(Aunis.ModID, "models/tesr/zpm/zpm.obj")).render();

            //ElementEnum.ZPM.bindTextureAndRender(BiomeOverlayEnum.NORMAL);

            GlStateManager.popMatrix();
        }
    }
}
