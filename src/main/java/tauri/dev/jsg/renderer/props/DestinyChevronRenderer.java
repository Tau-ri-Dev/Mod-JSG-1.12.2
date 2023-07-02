package tauri.dev.jsg.renderer.props;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.tileentity.props.DestinyChevronTile;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.util.JSGTextureLightningHelper;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;

public class DestinyChevronRenderer extends TileEntitySpecialRenderer<DestinyChevronTile> {

    @Override
    public void render(@Nonnull DestinyChevronTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        boolean isActive = te.isActive;

        BiomeOverlayEnum overlay = te.overlay;
        if (overlay == null) overlay = BiomeOverlayEnum.NORMAL;

        ResourceLocation texture = TextureLoader.getTextureResource("universe/universe_chevron" + overlay.suffix + ".png");
        ResourceLocation textureLight = TextureLoader.getTextureResource("universe/universe_chevron_light" + (!isActive ? "_off" : "") + overlay.suffix + ".png");

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5, 0.08f, 0.5);

        IBlockState blockState = te.getWorld().getBlockState(te.getPos());
        EnumFacing facing = blockState.getValue(JSGProps.FACING_HORIZONTAL);

        GlStateManager.pushMatrix();
        int rot = FacingHelper.getIntRotation(facing, false);
        GlStateManager.rotate(rot, 0, 1, 0);
        GlStateManager.color(1, 1, 1);

        GlStateManager.pushMatrix();
        TextureLoader.getTexture(texture).bindTexture();
        ElementEnum.DESTINY_CHEVRON.render();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        if (isActive) {
            GlStateManager.disableLighting();
            JSGTextureLightningHelper.lightUpTexture(1f);
        }

        TextureLoader.getTexture(textureLight).bindTexture();
        ElementEnum.DESTINY_CHEVRON.render();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(@Nonnull DestinyChevronTile te) {
        return true;
    }
}
