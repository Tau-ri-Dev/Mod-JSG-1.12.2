package mrjake.aunis.renderer.energy;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.tileentity.energy.ZPMHubTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class ZPMHubRenderer<S extends ZPMHubRendererState> extends TileEntitySpecialRenderer<ZPMHubTile> {


    @Override
    public void render(ZPMHubTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        @SuppressWarnings("unchecked") S rendererState = (S) te.getRendererStateClient();

        if (rendererState != null) {
            IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
            if (state.getBlock() != AunisBlocks.ZPM_HUB) return;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.scale(0.05, 0.05, 0.05);
            // todo: waiting on Haralds model redone
            //GlStateManager.rotate(rendererState.horizontalRotation, 0, 1, 0);
            GlStateManager.translate(-2, 0, 0);

            ElementEnum.ZPM_HUB.bindTextureAndRender(BiomeOverlayEnum.NORMAL);

            GlStateManager.popMatrix();
        }
    }
}
