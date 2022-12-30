package tauri.dev.jsg.renderer.props;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.renderer.AncientRenderer;
import tauri.dev.jsg.tileentity.props.AncientSignTile;
import tauri.dev.jsg.util.FacingToRotation;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import java.awt.*;

@SideOnly(Side.CLIENT)
public class AncientSignRenderer extends TileEntitySpecialRenderer<AncientSignTile> {

    public static final double ONE_CHAR_X = AncientRenderer.ONE_DIGIT_X;
    public static final double ONE_CHAR_Y = ONE_CHAR_X * 2 + (ONE_CHAR_X / 2);

    @Override
    public void render(@Nonnull AncientSignTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5, 0.5, 0.5);

        IBlockState blockState = te.getWorld().getBlockState(te.getPos());
        EnumFacing facing = blockState.getValue(JSGProps.FACING_HORIZONTAL);

        GlStateManager.pushMatrix();
        int rot = FacingToRotation.getIntRotation(facing, true);
        GlStateManager.rotate(rot, 0, 1, 0);
        GlStateManager.translate(0, 0, -0.5);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.007, 0.05, 0.001);
        GlStateManager.scale(0.01, 0.01, 0.01);

        Color color = new Color(te.color);

        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, ((AncientSignTile.LINES * ONE_CHAR_Y) / 2), 0);

        GlStateManager.pushMatrix();

        GlStateManager.translate(0, -(10.625/2), 0);

        String[] lines = te.ancientText;

        int i = 0;
        for (String l : lines) {
            GlStateManager.pushMatrix();

            if (te.lineBeingEdited == i)
                l = "- " + l + " -";

            double startX = -((ONE_CHAR_X * l.length()) / 2);
            GlStateManager.translate(startX, 0, 0);
            AncientRenderer.renderString(l, false);
            GlStateManager.popMatrix();
            GlStateManager.translate(0, -ONE_CHAR_Y, 0);
            i++;
        }

        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        GlStateManager.color(1, 1, 1);
    }
}
