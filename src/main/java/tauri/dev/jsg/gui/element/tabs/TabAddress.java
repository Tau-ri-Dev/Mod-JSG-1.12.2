package tauri.dev.jsg.gui.element.tabs;

import tauri.dev.jsg.gui.element.GuiHelper;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.network.SymbolMilkyWayEnum;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.items.SlotItemHandler;

import java.awt.*;
import java.util.Objects;

public class TabAddress extends Tab {

    // Gate's address
    private final StargateClassicBaseTile gateTile;
    private final SymbolTypeEnum symbolType;
    private final int progressColor;
    private int maxSymbols;

    protected TabAddress(TabAddressBuilder builder) {
        super(builder);

        this.gateTile = builder.gateTile;
        this.symbolType = builder.symbolType;
        this.progressColor = builder.progressColor;
        this.maxSymbols = 6;
    }

    public static TabAddressBuilder builder() {
        return new TabAddressBuilder();
    }

    public void setMaxSymbols(int maxSymbols) {
        this.maxSymbols = maxSymbols;
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.render(fontRenderer, mouseX, mouseY);

        // Draw page slot
        if (isVisible() && gateTile.getStargateAddress(symbolType) != null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(bgTexLocation);
            GlStateManager.color(1, 1, 1, 1);
            Gui.drawModalRectWithCustomSizedTexture(guiLeft + currentOffsetX + 105, guiTop + defaultY + 86, 6, 174, 18, 18, textureSize, textureSize);

            int shadow = 2;
            float color = 1.0f;

            switch (symbolType) {
                case UNIVERSE:
                    color = 0.0f;
                    break;

                default:
                    break;
            }
            for (int i = 0; i < maxSymbols; i++) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(Objects.requireNonNull(gateTile.getStargateAddress(symbolType)).get(i).getIconResource(SymbolMilkyWayEnum.getOriginId(BiomeOverlayEnum.updateBiomeOverlay(gateTile.getWorld(), gateTile.getPos(), gateTile.getSupportedOverlays()), gateTile.getWorld().provider.getDimension())));

                SymbolCoords symbolCoords = getSymbolCoords(i);
                GuiHelper.drawTexturedRectWithShadow(symbolCoords.x, symbolCoords.y, shadow, shadow, symbolType.iconWidht, symbolType.iconHeight, color);
            }

            GlStateManager.enableBlend();

            Minecraft.getMinecraft().getTextureManager().bindTexture(bgTexLocation);
            int progress = gateTile.getPageProgress();
            Color c = new Color(progressColor);
            float red = c.getRed();
            float green = c.getGreen();
            float blue = c.getBlue();
            GlStateManager.color(red, green, blue);
            Gui.drawModalRectWithCustomSizedTexture(guiLeft + currentOffsetX + 97, guiTop + defaultY + 86 + (18 - progress), 0, 174 + (18 - progress), 6, progress, textureSize, textureSize);

            GlStateManager.disableBlend();
        }
    }

    @Override
    public void renderFg(GuiScreen screen, FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.renderFg(screen, fontRenderer, mouseX, mouseY);

        if (isVisible() && isOpen() && gateTile.getStargateAddress(symbolType) != null) {
            for (int i = 0; i < maxSymbols; i++) {
                SymbolCoords symbolCoords = getSymbolCoords(i);

                if (GuiHelper.isPointInRegion(symbolCoords.x, symbolCoords.y, symbolType.iconWidht, symbolType.iconHeight, mouseX, mouseY)) {
                    screen.drawHoveringText(gateTile.getStargateAddress(symbolType).get(i).localize(), mouseX - guiLeft, mouseY - guiTop);
                }
            }
        }
    }

    public SlotTab createSlot(SlotItemHandler slot) {
        return new SlotTab(slot, (slotTab) -> {
            slotTab.xPos = currentOffsetX + 106;
            slotTab.yPos = defaultY + 87;
        });
    }

    public SymbolCoords getSymbolCoords(int symbol) {
        switch (symbolType) {
            case UNIVERSE:
                return new SymbolCoords(guiLeft + currentOffsetX + 24 + 16 * (symbol % 6), guiTop + defaultY + 20 + 45 * (symbol / 6));

            case PEGASUS:
                return new SymbolCoords(guiLeft + currentOffsetX + 29 + 34 * (symbol % 3), guiTop + defaultY + 20 + 28 * (symbol / 3));

            default:
                return new SymbolCoords(guiLeft + currentOffsetX + 29 + 31 * (symbol % 3), guiTop + defaultY + 20 + 28 * (symbol / 3));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // Builder

    public static class SymbolCoords {
        public final int x;
        public final int y;

        public SymbolCoords(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class TabAddressBuilder extends TabBuilder {

        // Gate's TileEntity reference
        private StargateClassicBaseTile gateTile;
        private SymbolTypeEnum symbolType;
        private int progressColor;

        public TabAddressBuilder setGateTile(StargateClassicBaseTile gateTile) {
            this.gateTile = gateTile;
            return this;
        }

        public TabAddressBuilder setSymbolType(SymbolTypeEnum symbolType) {
            this.symbolType = symbolType;
            return this;
        }

        public TabAddressBuilder setProgressColor(int color) {
            this.progressColor = color;
            return this;
        }

        @Override
        public TabAddress build() {
            return new TabAddress(this);
        }
    }
}
