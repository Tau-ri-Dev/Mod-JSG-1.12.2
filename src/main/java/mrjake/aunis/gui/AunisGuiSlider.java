package mrjake.aunis.gui;

import mrjake.aunis.gui.mainmenu.screens.options.AunisAudioOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AunisGuiSlider extends AunisGuiButton
{
    private float sliderValue;
    public boolean dragging;
    protected int xpos = 0;
    protected int ypos = 0;
    private final GameSettings.Options options;
    private final float minValue;
    private final float maxValue;

    public AunisGuiSlider(int buttonId, int x, int y, GameSettings.Options optionIn)
    {
        this(buttonId, x, y, optionIn, 0.0F, 1.0F);
        this.xpos = x;
        this.ypos = y;
    }

    public AunisGuiSlider(int buttonId, int x, int y, GameSettings.Options optionIn, float minValueIn, float maxValue)
    {
        super(buttonId, x, y, 150, 20, "");
        this.sliderValue = 1.0F;
        this.options = optionIn;
        this.minValue = minValueIn;
        this.maxValue = maxValue;
        Minecraft minecraft = Minecraft.getMinecraft();
        this.sliderValue = optionIn.normalizeValue(minecraft.gameSettings.getOptionFloatValue(optionIn));
        this.displayString = minecraft.gameSettings.getKeyBinding(optionIn);
    }

    protected int getHoverState(boolean mouseOver)
    {
        return 0;
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            if (this.dragging)
            {
                this.sliderValue = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
                float f = this.options.denormalizeValue(this.sliderValue);
                mc.gameSettings.setOptionFloatValue(this.options, f);
                this.sliderValue = this.options.normalizeValue(f);
                this.displayString = mc.gameSettings.getKeyBinding(this.options);
            }
            drawRect(this.xpos + (int)(this.sliderValue * (float)(this.width - 8)), this.ypos, this.xpos + (int)(this.sliderValue * (float)(this.width - 8)) + 8, this.ypos + 20, GuiBase.FRAME_COLOR);
        }
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (super.mousePressed(mc, mouseX, mouseY))
        {
            this.sliderValue = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
            mc.gameSettings.setOptionFloatValue(this.options, this.options.denormalizeValue(this.sliderValue));
            this.displayString = mc.gameSettings.getKeyBinding(this.options);
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void mouseReleased(int mouseX, int mouseY)
    {
        this.dragging = false;
    }
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            int fgcolor = 0xCCCCCC;
            int bgcolor = 0xFF1D2026;

            if (!this.enabled) {
                fgcolor = 10526880;
            }

            else if (this.hovered) {
                fgcolor = 0xFFFFFF;
                bgcolor = 0xFF313640;
            }

            drawRect(x, y, x+width, y+height, GuiBase.FRAME_COLOR);
            drawRect(x+1, y+1, x+width-1, y+height-1, bgcolor);
            this.mouseDragged(mc, mouseX, mouseY);
            this.drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, fgcolor);
        }
    }

}
