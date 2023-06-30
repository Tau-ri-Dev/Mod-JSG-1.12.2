package tauri.dev.jsg.renderer.props;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.tileentity.props.DestinyBearingTile;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.util.JSGTextureLightningHelper;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;

public class DestinyBearingRenderer extends TileEntitySpecialRenderer<DestinyBearingTile> {

    @Override
    public void render(@Nonnull DestinyBearingTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        boolean isActive = te.isActive;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5, 0.5, 0.5);

        IBlockState blockState = te.getWorld().getBlockState(te.getPos());
        EnumFacing facing = blockState.getValue(JSGProps.FACING_HORIZONTAL);

        GlStateManager.pushMatrix();
        int rot = FacingHelper.getIntRotation(facing, false);
        GlStateManager.rotate(rot, 0, 1, 0);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 1.2f, 0);
        GlStateManager.scale(4.2f, 4.2f, 4.2f);
        GlStateManager.color(1, 1, 1);
        ElementEnum.DESTINY_BEARING_BODY.bindTextureAndRender();
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1);
        if (isActive) {
            GlStateManager.disableLighting();
            JSGTextureLightningHelper.lightUpTexture(1f);
            ElementEnum.DESTINY_BEARING_ON.bindTexture(BiomeOverlayEnum.NORMAL);
        } else
            ElementEnum.DESTINY_BEARING_OFF.bindTexture(BiomeOverlayEnum.NORMAL);

        ElementEnum.DESTINY_BEARING_ON.render();

        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(@Nonnull DestinyBearingTile te) {
        return true;
    }
}
