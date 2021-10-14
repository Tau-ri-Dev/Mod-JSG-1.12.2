package mrjake.aunis.gui.mainmenu.screens.bindings;

import java.util.Arrays;

import mrjake.aunis.Aunis;
import mrjake.aunis.gui.AunisGuiButton;
import mrjake.aunis.gui.mainmenu.screens.options.AunisBindingOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

import static net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect;

@SideOnly(Side.CLIENT)
public class AunisGuiKeyBindingList extends GuiListExtended {
    public final AunisBindingOptions controlsScreen;
    public final Minecraft mc;
    public final GuiListExtended.IGuiListEntry[] listEntries;
    public int maxListLabelWidth;

    public static final ResourceLocation BACKGROUND1 = new ResourceLocation(Aunis.ModID,"textures/gui/mainmenu/background1.jpg");
    public static final ResourceLocation BACKGROUND2 = new ResourceLocation(Aunis.ModID,"textures/gui/mainmenu/background2.jpg");

    public AunisGuiKeyBindingList(AunisBindingOptions controls, Minecraft mcIn) {
        super(mcIn, controls.width + 45, controls.height, 63, controls.height - 32, 20);
        this.controlsScreen = controls;
        this.mc = mcIn;
        KeyBinding[] akeybinding = (KeyBinding[]) ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        this.listEntries = new GuiListExtended.IGuiListEntry[akeybinding.length + KeyBinding.getKeybinds().size()];
        Arrays.sort((Object[]) akeybinding);
        int i = 0;
        String s = null;

        for (KeyBinding keybinding : akeybinding) {
            String s1 = keybinding.getKeyCategory();

            if (!s1.equals(s)) {
                s = s1;
                this.listEntries[i++] = new AunisGuiKeyBindingList.CategoryEntry(s1);
            }

            int j = mcIn.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));

            if (j > this.maxListLabelWidth) {
                this.maxListLabelWidth = j;
            }

            this.listEntries[i++] = new AunisGuiKeyBindingList.KeyEntry(keybinding);
        }
    }

    protected int getSize() {
        return this.listEntries.length;
    }

    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.listEntries[index];
    }

    protected int getScrollBarX() {
        return super.getScrollBarX() + 35;
    }

    public int getListWidth() {
        return super.getListWidth() + 32;
    }

    @SideOnly(Side.CLIENT)
    public class CategoryEntry implements GuiListExtended.IGuiListEntry {
        private final String labelText;
        private final int labelWidth;

        public CategoryEntry(String name) {
            this.labelText = I18n.format(name);
            this.labelWidth = AunisGuiKeyBindingList.this.mc.fontRenderer.getStringWidth(this.labelText);
        }

        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            if (AunisGuiKeyBindingList.this.mc.currentScreen != null)
                AunisGuiKeyBindingList.this.mc.fontRenderer.drawString(this.labelText, AunisGuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2, y + slotHeight - AunisGuiKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT - 1, 16777215);
        }

        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            return false;
        }

        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        }

        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
        }
    }

    @SideOnly(Side.CLIENT)
    public class KeyEntry implements GuiListExtended.IGuiListEntry {
        private final KeyBinding keybinding;
        private final String keyDesc;
        private final AunisGuiButton btnChangeKeyBinding;
        private final AunisGuiButton btnReset;

        private KeyEntry(KeyBinding name) {
            this.keybinding = name;
            this.keyDesc = I18n.format(name.getKeyDescription());
            this.btnChangeKeyBinding = new AunisGuiButton(0, 0, 0, 95, 20, I18n.format(name.getKeyDescription()));
            this.btnReset = new AunisGuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
        }

        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            boolean flag = AunisGuiKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
            AunisGuiKeyBindingList.this.mc.fontRenderer.drawString(this.keyDesc, x + 90 - AunisGuiKeyBindingList.this.maxListLabelWidth, y + slotHeight / 2 - AunisGuiKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
            this.btnReset.x = x + 210;
            this.btnReset.y = y;
            this.btnReset.enabled = !this.keybinding.isSetToDefaultValue();
            this.btnReset.drawButton(AunisGuiKeyBindingList.this.mc, mouseX, mouseY, partialTicks);
            this.btnChangeKeyBinding.x = x + 105;
            this.btnChangeKeyBinding.y = y;
            this.btnChangeKeyBinding.displayString = this.keybinding.getDisplayName();
            boolean flag1 = false;
            boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G

            if (this.keybinding.getKeyCode() != 0) {
                for (KeyBinding keybinding : AunisGuiKeyBindingList.this.mc.gameSettings.keyBindings) {
                    if (keybinding != this.keybinding && keybinding.conflicts(this.keybinding)) {
                        flag1 = true;
                        keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
                    }
                }
            }

            if (flag) {
                this.btnChangeKeyBinding.displayString = TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.btnChangeKeyBinding.displayString + TextFormatting.WHITE + " <";
            } else if (flag1) {
                this.btnChangeKeyBinding.displayString = (keyCodeModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + this.btnChangeKeyBinding.displayString;
            }

            this.btnChangeKeyBinding.drawButton(AunisGuiKeyBindingList.this.mc, mouseX, mouseY, partialTicks);
        }

        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (this.btnChangeKeyBinding.mousePressed(AunisGuiKeyBindingList.this.mc, mouseX, mouseY)) {
                AunisGuiKeyBindingList.this.controlsScreen.buttonId = this.keybinding;
                return true;
            } else if (this.btnReset.mousePressed(AunisGuiKeyBindingList.this.mc, mouseX, mouseY)) {
                this.keybinding.setToDefault();
                AunisGuiKeyBindingList.this.mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            } else {
                return false;
            }
        }

        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            this.btnChangeKeyBinding.mouseReleased(x, y);
            this.btnReset.mouseReleased(x, y);
        }

        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
        }
    }

    @Override
    public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
        if (this.visible) {
            this.mouseX = mouseXIn;
            this.mouseY = mouseYIn;
            this.drawBackground();
            int i = this.getScrollBarX();
            int j = i + 6;
            this.bindAmountScrolled();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            // Forge: background rendering moved into separate method.
            int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int l = this.top + 4 - (int) this.amountScrolled;

            if (this.hasListHeader) {
                this.drawListHeader(k, l, tessellator);
            }

            this.drawSelectionBox(k, l, mouseXIn, mouseYIn, partialTicks);
            GlStateManager.disableDepth();

            // TODO: complete header nad footer of all menus like this (an also redone this)
            this.overlayBackground(0, this.top + 5, 255, 255);
            this.overlayBackground(this.bottom - 5, this.height, 255, 255);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture2D();
            int j1 = this.getMaxScroll();

            if (j1 > 0) {
                int k1 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                k1 = MathHelper.clamp(k1, 32, this.bottom - this.top - 8);
                int l1 = (int) this.amountScrolled * (this.bottom - this.top - k1) / j1 + this.top;

                if (l1 < this.top) {
                    l1 = this.top;
                }
            }

            this.renderDecorations(mouseXIn, mouseYIn);
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }

    @Override
    protected void drawSelectionBox(int insideLeft, int insideTop, int mouseXIn, int mouseYIn, float partialTicks)
    {
        int i = this.getSize();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for (int j = 0; j < i; ++j)
        {
            int k = insideTop + j * this.slotHeight + this.headerPadding;
            int l = this.slotHeight - 4;

            if (k > (this.bottom + 58) || k + l < (this.top + 58))
            {
                this.updateItemPos(j, insideLeft, k, partialTicks);
            }

            if (this.showSelectionBox && this.isSelected(j))
            {
                int i1 = this.left + (this.width / 2 - this.getListWidth() / 2);
                int j1 = this.left + this.width / 2 + this.getListWidth() / 2;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableTexture2D();
                GlStateManager.enableTexture2D();
            }

            this.drawSlot(j, insideLeft, k, l, mouseXIn, mouseYIn, partialTicks);
        }
    }
}

