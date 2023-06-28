package tauri.dev.jsg.renderer.props;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfigUtil;
import tauri.dev.jsg.item.props.DecorPropItem;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.tileentity.props.DecorPropTile;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class DecorPropRenderer extends TileEntitySpecialRenderer<DecorPropTile> {

    @Override
    public void render(@Nonnull DecorPropTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState bs = te.getWorld().getBlockState(te.getPos());

        GlStateManager.pushMatrix();

        int meta = bs.getValue(JSGProps.PROP_VARIANT);
        DecorPropItem.PropVariants propVariant = DecorPropItem.PropVariants.byId(meta);
        JSG.info("render1!");

        if (propVariant == null) return;
        ElementEnum[] models = propVariant.models;

        JSGConfigUtil.rescaleToConfig();
        JSG.info("render0!");

        for (ElementEnum model : models) {
            model.bindTextureAndRender();
            JSG.info("render!");
        }

        GlStateManager.popMatrix();
    }
}
