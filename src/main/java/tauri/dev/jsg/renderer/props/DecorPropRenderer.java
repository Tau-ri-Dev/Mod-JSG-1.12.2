package tauri.dev.jsg.renderer.props;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.item.props.DecorPropItem;
import tauri.dev.jsg.tileentity.props.DecorPropTile;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.vector.Vector3f;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class DecorPropRenderer extends TileEntitySpecialRenderer<DecorPropTile> {

    @Override
    public void render(@Nonnull DecorPropTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState bs = te.getWorld().getBlockState(te.getPos());

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.pushMatrix();

        int meta = bs.getValue(JSGProps.PROP_VARIANT);
        DecorPropItem.PropVariants propVariant = DecorPropItem.PropVariants.byId(meta);

        if (propVariant == null) return;
        DecorPropItem.PropModel[] models = propVariant.models;

        GlStateManager.translate(0.5, 0, 0.5);

        DecorPropItem.PropModelRenderFunction r = propVariant.runnableWhileRendering;
        if (r != null) {
            r.runOnRender(te.getWorld(), propVariant, te);
        }

        for (DecorPropItem.PropModel model : models) {
            GlStateManager.pushMatrix();
            Vector3f t = model.translation;
            GlStateManager.translate(t.x, t.y, t.z);
            GlStateManager.scale(model.size, model.size, model.size);
            model.element.bindTextureAndRender();
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }
}
