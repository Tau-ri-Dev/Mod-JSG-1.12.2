package mrjake.aunis.gui.mainmenu.screens.loadingworld;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AunisGuiLoadingWorld extends LoadingScreenRenderer {
    private final Minecraft mc;
    private long systemTime = Minecraft.getSystemTime();
    private final Framebuffer framebuffer;
    protected float screenCenterHeight;
    protected float screenCenterWidth;
    protected int height;
    protected int width;

    public AunisGuiLoadingWorld(Minecraft mcIn) {
        super(mcIn);
        this.mc = mcIn;
        this.framebuffer = new Framebuffer(mcIn.displayWidth, mcIn.displayHeight, false);
        this.framebuffer.setFramebufferFilter(9728);
        this.screenCenterHeight = mcIn.displayHeight;
        this.screenCenterWidth = mcIn.displayWidth;
        this.height = mcIn.displayHeight;
        this.width = mcIn.displayWidth;
    }

    // ------------------------------------------
    // DEFINE VARIABLES
    protected static final ResourceLocation BACKGROUND_TEXTURE =
            AunisConfig.mainMenuConfig.disableAunisMainMenu ? null :
            new ResourceLocation(Aunis.MOD_ID, "textures/gui/mainmenu/background.jpg");

    @Override
    public void setLoadingProgress(int progress) {
        long i = Minecraft.getSystemTime();

        if (i - this.systemTime >= 100L) {
            this.systemTime = i;
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int j = scaledresolution.getScaleFactor();
            int k = scaledresolution.getScaledWidth();
            int l = scaledresolution.getScaledHeight();

            if (OpenGlHelper.isFramebufferEnabled())
            {
                this.framebuffer.framebufferClear();
            }
            else
            {
                GlStateManager.clear(256);
            }

            this.framebuffer.bindFramebuffer(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);

            if (!OpenGlHelper.isFramebufferEnabled())
            {
                GlStateManager.clear(16640);
            }

            try
            {
                if (!net.minecraftforge.fml.client.FMLClientHandler.instance().handleLoadingScreen(scaledresolution)) //FML Don't render while FML's pre-screen is rendering
                {
                    this.screenCenterHeight = (((float) height) / 2f);
                    this.screenCenterWidth = ((float) width) / 2f;

                    // ------------------------------
                    // DRAWING BACKGROUND

                    GlStateManager.pushMatrix();
                    GlStateManager.enableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.enableAlpha();
                    this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                    Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, width, height, width, height, width, height);
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                    GlStateManager.disableTexture2D();
                    GlStateManager.popMatrix();

                    GlStateManager.popMatrix();
                }
            }
            catch (java.io.IOException e)
            {
                throw new RuntimeException(e);
            } //FML End
            this.framebuffer.unbindFramebuffer();

            if (OpenGlHelper.isFramebufferEnabled())
            {
                this.framebuffer.framebufferRender(k * j, l * j);
            }

            this.mc.updateDisplay();

            try
            {
                Thread.yield();
            }
            catch (Exception ignored) {
            }
        }
    }
}
