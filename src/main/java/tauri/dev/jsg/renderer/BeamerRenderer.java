package tauri.dev.jsg.renderer;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import tauri.dev.jsg.beamer.BeamerModeEnum;
import tauri.dev.jsg.beamer.BeamerRoleEnum;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.tileentity.BeamerTile;
import tauri.dev.jsg.util.FluidColors;

import javax.annotation.Nonnull;

public class BeamerRenderer extends TileEntitySpecialRenderer<BeamerTile> {

    @Override
    public void render(@Nonnull BeamerTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        if (JSGConfig.debugConfig.renderBoundingBoxes) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            te.getRenderBoxForDisplay().render();
            GlStateManager.popMatrix();
        }

        if (te.getMode() != BeamerModeEnum.NONE && te.beamRadiusClient > 0) {
            //long tick = te.getWorld().getTotalWorldTime();

            GlStateManager.alphaFunc(516, 0.1F);
            this.bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
            GlStateManager.rotate(180 - te.getFacing().getHorizontalAngle(), 0, 1, 0);

            int a = te.beamLengthClient;
            int b = te.beamOffsetFromTargetXClient;
            int c = te.beamOffsetFromTargetYClient;

            double t1 = ((double) b) / ((double) a);
            double angY = Math.toDegrees(Math.atan(t1));

            double beamerLengthTemp = (Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)));

            double t2 = ((double) c) / (beamerLengthTemp);
            double angX = Math.toDegrees(Math.atan(t2));

            double beamerLength = ((Math.sqrt(Math.pow(beamerLengthTemp, 2) + Math.pow(c, 2))) - te.beamLengthTargetClient);

            if (beamerLength < 0) beamerLength = -beamerLength;

            GlStateManager.rotate(((float) angY) * ((te.getFacing() == EnumFacing.SOUTH || te.getFacing() == EnumFacing.WEST) ? -1 : 1), 0, 1, 0);
            GlStateManager.rotate(-90 + (((float) angX) * -1), 1, 0, 0);

            /*if (tick % 40 == 0) {
                JSG.info("beamerLength: " + beamerLength);
            }*/

            float[] colors = te.getMode().colors;

            if (te.getMode() == BeamerModeEnum.FLUID && tauri.dev.jsg.config.JSGConfig.beamerConfig.enableFluidBeamColorization) {
//				FluidTank tank = (FluidTank) te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

                if (te.lastFluidTransferred != null) {
                    FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(te.lastFluidTransferred);

                    if (fluidColors != null) {
                        colors = fluidColors.colors;
                    }
                }
            }

            float mul = 1;
            BeamerRenderer.renderBeamSegment(-0.5, -0.3, -0.5, partialTicks * mul, (te.getRole() == BeamerRoleEnum.TRANSMIT ? 1 : -1), getWorld().getTotalWorldTime() * mul, 0, beamerLength + 0.2D, colors, (te.beamRadiusClient * 0.75), te.beamRadiusClient + 0.05f);
            GlStateManager.popMatrix();
            GlStateManager.enableCull();
        }
    }

    @Override
    public boolean isGlobalRenderer(@Nonnull BeamerTile te) {
        return true;
    }


    /**
     * This method is copy of Minecraft Beamer Render method
     */

    //private static VertexFormat JSG_VERTEX_COLOR_POSITION = new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3);
    public static void renderBeamSegment(double x, double y, double z, double partialTicks, double textureScale, double totalWorldTime, double yOffset, double height, float[] colors, double beamRadius, double glowRadius) {
        double i = yOffset + height;
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        double d0 = totalWorldTime + partialTicks;
        double d1 = height < 0 ? d0 : -d0;
        double d2 = MathHelper.frac(d1 * 0.2D - (double) MathHelper.floor(d1 * 0.1D));
        float f = colors[0];
        float f1 = colors[1];
        float f2 = colors[2];
        double d3 = d0 * 0.025D * -1.5D;
        double d4 = 0.5D + Math.cos(d3 + 2.356194490192345D) * beamRadius;
        double d5 = 0.5D + Math.sin(d3 + 2.356194490192345D) * beamRadius;
        double d6 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * beamRadius;
        double d7 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * beamRadius;
        double d8 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * beamRadius;
        double d9 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * beamRadius;
        double d10 = 0.5D + Math.cos(d3 + 5.497787143782138D) * beamRadius;
        double d11 = 0.5D + Math.sin(d3 + 5.497787143782138D) * beamRadius;
        double d13 = -1.0D + d2;
        double d14 = -1.0D + d2;
        double d15 = height * textureScale * (0.5D / beamRadius) + d14;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x + d4, y + i, z + d5).tex(1.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d4, y + yOffset, z + d5).tex(1.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d6, y + yOffset, z + d7).tex(0.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d6, y + i, z + d7).tex(0.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d10, y + i, z + d11).tex(1.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d10, y + yOffset, z + d11).tex(1.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d8, y + yOffset, z + d9).tex(0.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d8, y + i, z + d9).tex(0.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d6, y + i, z + d7).tex(1.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d6, y + yOffset, z + d7).tex(1.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d10, y + yOffset, z + d11).tex(0.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d10, y + i, z + d11).tex(0.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d8, y + i, z + d9).tex(1.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d8, y + yOffset, z + d9).tex(1.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d4, y + yOffset, z + d5).tex(0.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        bufferbuilder.pos(x + d4, y + i, z + d5).tex(0.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(false);
        d3 = 0.5D - glowRadius;
        d4 = 0.5D - glowRadius;
        d5 = 0.5D + glowRadius;
        d6 = 0.5D - glowRadius;
        d7 = 0.5D - glowRadius;
        d8 = 0.5D + glowRadius;
        d9 = 0.5D + glowRadius;
        d10 = 0.5D + glowRadius;
        d14 = height * textureScale + d13;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x + d3, y + i, z + d4).tex(1.0D, d14).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d3, y + yOffset, z + d4).tex(1.0D, d13).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d5, y + yOffset, z + d6).tex(0.0D, d13).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d5, y + i, z + d6).tex(0.0D, d14).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d9, y + i, z + d10).tex(1.0D, d14).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d9, y + yOffset, z + d10).tex(1.0D, d13).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d7, y + yOffset, z + d8).tex(0.0D, d13).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d7, y + i, z + d8).tex(0.0D, d14).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d5, y + i, z + d6).tex(1.0D, d14).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d5, y + yOffset, z + d6).tex(1.0D, d13).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d9, y + yOffset, z + d10).tex(0.0D, d13).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d9, y + i, z + d10).tex(0.0D, d14).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d7, y + i, z + d8).tex(1.0D, d14).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d7, y + yOffset, z + d8).tex(1.0D, d13).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d3, y + yOffset, z + d4).tex(0.0D, d13).color(f, f1, f2, 0.25F).endVertex();
        bufferbuilder.pos(x + d3, y + i, z + d4).tex(0.0D, d14).color(f, f1, f2, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }
}
