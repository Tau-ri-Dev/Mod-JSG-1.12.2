package tauri.dev.jsg.gui.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.model.ModelLoader;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.stargate.ChevronEnum;
import tauri.dev.jsg.renderer.stargate.ChevronTextureList;
import tauri.dev.jsg.renderer.stargate.StargateMilkyWayRenderer;
import tauri.dev.jsg.renderer.stargate.StargatePegasusRenderer;
import tauri.dev.jsg.stargate.network.SymbolMilkyWayEnum;
import tauri.dev.jsg.stargate.network.SymbolPegasusEnum;
import tauri.dev.jsg.stargate.network.SymbolUniverseEnum;
import tauri.dev.jsg.util.math.NumberUtils;

import java.util.Random;

import static tauri.dev.jsg.stargate.StargateClassicSpinHelper.A_ANGLE_PER_TICK;

public enum EnumMainMenuGateType {
    MILKYWAY,
    UNIVERSE,
    PEGASUS;

    public static EnumMainMenuGateType random() {
        int i = new Random().nextInt(3);
        switch (i) {
            default:
                return MILKYWAY;
            case 1:
                return PEGASUS;
            case 2:
                return UNIVERSE;
        }
    }

    public void renderGate(int x, int y, float size, double tick) {
        GlStateManager.pushMatrix();

        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1);

        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(size, -size, size);
        GlStateManager.disableRescaleNormal();

        switch (this) {
            default:
                break;
            case MILKYWAY:
                renderMWGate(tick);
                break;
            case PEGASUS:
                renderPEGGate(tick);
                break;
            case UNIVERSE:
                renderUNIGate(tick);
                break;
        }

        GlStateManager.popMatrix();

        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
    }

    private void renderMWGate(double tick) {
        // Ring
        GlStateManager.pushMatrix();
        GlStateManager.translate(StargateMilkyWayRenderer.RING_LOC.x, StargateMilkyWayRenderer.RING_LOC.z, StargateMilkyWayRenderer.RING_LOC.y);
        GlStateManager.rotate((float) (-(tick * (A_ANGLE_PER_TICK/2)) % 360), 0, 0, 1);
        GlStateManager.translate(-StargateMilkyWayRenderer.RING_LOC.x, -StargateMilkyWayRenderer.RING_LOC.z, -StargateMilkyWayRenderer.RING_LOC.y);

        ElementEnum.MILKYWAY_RING.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        ModelLoader.getModel(((SymbolMilkyWayEnum) SymbolMilkyWayEnum.getOrigin()).getModelResource(BiomeOverlayEnum.NORMAL, 0, false, false, 5)).render();

        GlStateManager.popMatrix();

        // Chevrons
        GlStateManager.pushMatrix();
        ChevronTextureList chevrons = new ChevronTextureList("milkyway/chevron", 7, true);
        chevrons.initClient();
        for (ChevronEnum chevron : ChevronEnum.values()) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(chevron.rotation, 0, 0, 1);
            TextureLoader.getTexture(chevrons.get(BiomeOverlayEnum.NORMAL, chevron, false)).bindTexture();
            ElementEnum.MILKYWAY_CHEVRON_MOVING.render();
            ElementEnum.MILKYWAY_CHEVRON_LIGHT.render();
            ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
            ElementEnum.MILKYWAY_CHEVRON_BACK.render();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();

        // Gate
        GlStateManager.pushMatrix();
        ElementEnum.MILKYWAY_GATE.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();
    }

    private void renderUNIGate(double tick) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.14f, 1.14f, 1.14f);
        GlStateManager.pushMatrix();
        GlStateManager.rotate((float) (-(tick * (A_ANGLE_PER_TICK/2)) % 360), 0, 0, 1);

        // Gate
        GlStateManager.pushMatrix();
        ElementEnum.UNIVERSE_GATE.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();

        // Chevrons
        GlStateManager.pushMatrix();
        ChevronTextureList chevrons = new ChevronTextureList("universe/universe_chevron", 9, true);
        chevrons.initClient();
        for (ChevronEnum chevron : ChevronEnum.values()) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(chevron.rotation, 0, 0, 1);
            TextureLoader.getTexture(chevrons.get(BiomeOverlayEnum.NORMAL, chevron, false)).bindTexture();
            ElementEnum.UNIVERSE_CHEVRON.render();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();

        // Symbols
        ElementEnum.UNIVERSE_CHEVRON.bindTexture(BiomeOverlayEnum.NORMAL);
        for (SymbolUniverseEnum symbol : SymbolUniverseEnum.values()) {
            if (symbol.modelResource != null) {
                float color = 0.25f;
                switch(symbol){
                    case G17:
                    case G10:
                    case G15:
                    case G20:
                    case G26:
                    case G28:
                    case G13:
                    case G18:
                    case G6:
                        color += 0.6f;
                        break;
                    default:
                        break;
                }
                GlStateManager.pushMatrix();
                GlStateManager.color(color, color, color);
                ModelLoader.getModel(symbol.modelResource).render();
                GlStateManager.popMatrix();
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1);
        GlStateManager.popMatrix();
    }

    private void renderPEGGate(double tick) {
        // Ring
        GlStateManager.pushMatrix();
        ElementEnum.PEGASUS_RING.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();

        // Gate
        GlStateManager.pushMatrix();
        ElementEnum.PEGASUS_GATE.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        int glyphsCount = (int) ((tick / 2) % (37*3));
        if(glyphsCount > 36) glyphsCount = 36;

        int chevronsCount = glyphsCount / 4;

        for (int i = -8; i < (glyphsCount - 8); i++) {
            int ii = (i % 36);

            if(ii < 0) ii = 36 + ii;

            ii = 36 - ii;

            renderPegasusGlyph(ii, ii);
        }
        GlStateManager.popMatrix();

        // Chevrons
        if(chevronsCount == 4) chevronsCount = 3;
        if(chevronsCount == 5) chevronsCount = 3;
        if(chevronsCount > 5) chevronsCount -= 2;
        GlStateManager.pushMatrix();
        ChevronTextureList chevrons = new ChevronTextureList("pegasus/chevron", chevronsCount, (chevronsCount == 7));
        chevrons.initClient();

        for (ChevronEnum chevron : ChevronEnum.values()) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(chevron.rotation, 0, 0, 1);
            TextureLoader.getTexture(chevrons.get(BiomeOverlayEnum.NORMAL, chevron, false)).bindTexture();
            ElementEnum.PEGASUS_CHEVRON_MOVING.render();
            ElementEnum.PEGASUS_CHEVRON_LIGHT.render();
            ElementEnum.PEGASUS_CHEVRON_FRAME.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
            ElementEnum.PEGASUS_CHEVRON_BACK.render();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    private double[] getPositionInRingAtIndex(double radius, int index) {
        double deg = ((360.0 / 36) * index);
        double rad = Math.toRadians(deg);
        return new double[]{radius * Math.cos(rad), radius * Math.sin(rad), deg};
    }

    private void renderPegasusGlyph(int glyphId, int slot) {
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();

        GlStateManager.color(1, 1, 1);

        double[] slotPos = getPositionInRingAtIndex((StargatePegasusRenderer.GATE_DIAMETER / 2) - 0.85, slot);

        // Round is necessary here, since Minecraft doesn't handle many decimal places very well in this case,
        // so that the texture just ceases to exist.
        GlStateManager.translate(NumberUtils.round(slotPos[0], 3), NumberUtils.round(slotPos[1], 3), 0.205);
        GlStateManager.rotate(90, 1, 0, 0);

        String path = String.format("pegasus/%s.png", "glyphs");
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureLoader.getTextureResource(path));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        // The glyphs in the assets are arranged in a circle, so we extract those glyphs at certain positions.
        double radius = 0.94;
        // double[] uv = getPositionInRingAtIndex(radius, -glyphId);
        int textureSlot = SymbolPegasusEnum.valueOf(glyphId).textureSlot;
        double[] uv = getPositionInRingAtIndex(radius, -(textureSlot));
        double x = (uv[0] + radius) / 2;
        double y = (uv[1] + radius) / 2;

        double tileSize = 0.27;
        double uvSize = 0.0625;

        GlStateManager.rotate((360.0f / 36) * (slot - textureSlot), 0, 1, 0);

        buffer.pos(-tileSize, 0, -tileSize).tex(x, y).endVertex();
        buffer.pos(-tileSize, 0, tileSize).tex(x, y + uvSize).endVertex();
        buffer.pos(tileSize, 0, tileSize).tex(x + uvSize, y + uvSize).endVertex();
        buffer.pos(tileSize, 0, -tileSize).tex(x + uvSize, y).endVertex();

        tessellator.draw();

        buffer.setTranslation(0, 0, 0);
        GlStateManager.popMatrix();
    }
}
