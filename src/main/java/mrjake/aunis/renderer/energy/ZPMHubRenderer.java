package mrjake.aunis.renderer.energy;

import mrjake.aunis.AunisProps;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.BlockRenderer;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.tileentity.energy.ZPMHubTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class ZPMHubRenderer<S extends ZPMHubRendererState> extends TileEntitySpecialRenderer<ZPMHubTile> {

    private static final BlockPos ZERO_BLOCKPOS = new BlockPos(0, 0, 0);

    @Override
    public void render(ZPMHubTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ZPMHubRendererState rendererState = te.getRendererStateClient();

        if (rendererState != null) {
            IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
            if (state.getBlock() != AunisBlocks.ZPM_HUB) return;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            if (state.getValue(AunisProps.SNOWY)) {
                BlockRenderer.render(getWorld(), ZERO_BLOCKPOS, Blocks.SNOW_LAYER.getDefaultState(), te.getPos());
            }

            GlStateManager.translate(0.5, 0, 0.5);
            GlStateManager.rotate(rendererState.horizontalRotation, 0, 1, 0);

            ElementEnum.ZPM_HUB.bindTextureAndRender(BiomeOverlayEnum.NORMAL);

            GlStateManager.popMatrix();
        }
    }
}
