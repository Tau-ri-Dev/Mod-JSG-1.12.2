package mrjake.aunis.renderer.energy;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.tileentity.energy.ZPMTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class ZPMRenderer<S extends ZPMRendererState> extends TileEntitySpecialRenderer<ZPMTile> {


    @Override
    public void render(ZPMTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        @SuppressWarnings("unchecked") S rendererState = (S) te.getRendererStateClient();

        if (rendererState != null) {
            IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
            if (state.getBlock() != AunisBlocks.ZPM) return;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.scale(1, 1, 1);
            GlStateManager.translate(0.315, 0.3, 0.431);

            ElementEnum.ZPM.bindTextureAndRender(BiomeOverlayEnum.NORMAL);

            GlStateManager.popMatrix();
        }
    }
}
