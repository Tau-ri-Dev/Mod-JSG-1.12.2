package mrjake.aunis.gui.mainmenu.screens.options;

import com.google.common.collect.Lists;
import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.base.AunisGuiButton;
import mrjake.aunis.gui.base.AunisGuiSlider;
import mrjake.aunis.gui.mainmenu.screens.options.resourcepacks.AunisGuiResourcePackSelected;
import mrjake.aunis.gui.mainmenu.screens.options.resourcepacks.AunisGuiResourcePacksAvailable;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import mrjake.aunis.sound.AunisSoundHelperClient;
import mrjake.aunis.sound.SoundPositionedEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class AunisResourcePacksOptions extends GuiScreenResourcePacks {

    public AunisResourcePacksOptions(GuiScreen parentScreenIn, BiomeOverlayEnum overlay) {
        super(parentScreenIn);
        this.parentScreen = parentScreenIn;
        this.overlay = overlay;
    }
    @SubscribeEvent
    public static void onSounds(PlaySoundEvent event) {
        event.setResultSound(null);
    }

    // ------------------------------------------
    // DEFINE VARIABLES

    protected static float animationStage = 0;
    protected final float ringAnimationStepSetting = 0.3f;
    protected float ringAnimationStep = 0.0f;
    protected float ringAnimationSpeed = 1.0f;
    protected final boolean speedUpGate = true;

    protected final GuiScreen parentScreen;
    protected static List<ResourcePackListEntry> availableResourcePacks;
    protected static List<ResourcePackListEntry> selectedResourcePacks;
    protected static AunisGuiResourcePacksAvailable availableResourcePacksList;
    protected static AunisGuiResourcePackSelected selectedResourcePacksList;
    protected boolean changed;
    protected int eventButton;
    protected int touchValue;
    protected long lastMouseEvent;

    protected BiomeOverlayEnum overlay;
    protected float screenCenterHeight = (((float) height) / 2f);
    protected float screenCenterWidth = ((float) width) / 2f;
    protected List<GuiButton> aunisButtonList = new ArrayList<>();
    protected List<GuiButton> aunisButtonSliders = new ArrayList<>();
    protected static final ResourceLocation BACKGROUND_TEXTURE = AunisConfig.mainMenuConfig.disableAunisMainMenu ? null : new ResourceLocation(Aunis.MOD_ID, "textures/gui/mainmenu/background.jpg");

    // render kawoosh and event horizon
    protected float kawooshState = 0f;
    protected float gateZoom = 0.0f;
    protected float gatePos = 0.0f;
    protected boolean isUnloading = false;

    /**
     * ------------------------------------------
     * showVersionAlert indexes:
     * <p>
     * 0 -> Version is good
     * 1 -> Alert is open
     * 2 -> Alert closed by client
     * <p>
     * ------------------------------------------
     */
    // ------------------------------------------

    // slow down and speed up gate ring
    public void updateRingSpeed(){
        if(!AunisConfig.mainMenuConfig.gateRotation) return;
        if (!speedUpGate) {
            if (ringAnimationSpeed > 0.0f) ringAnimationSpeed -= 0.05f;
        } else {
            if (ringAnimationSpeed < 1.0f) ringAnimationSpeed += 0.05f;
        }
        ringAnimationStep = ringAnimationStepSetting * ringAnimationSpeed;
        animationStage += ringAnimationStep;
    }

    // play sound
    public void updateSound() {
        AunisSoundHelperClient.playPositionedSoundClientSide(new BlockPos(1, 0, 0), SoundPositionedEnum.MAINMENU_MUSIC, AunisConfig.mainMenuConfig.playMusic);
        AunisSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 0, 1), SoundPositionedEnum.MAINMENU_RING_ROLL,
                (AunisConfig.mainMenuConfig.gateRotation && kawooshState == 0 && ringAnimationSpeed > 0.0f));
    }

    // update ring rotation and overlay
    public void updateAnimation() {
        this.screenCenterHeight = (((float) height) / 2f);
        this.screenCenterWidth = ((float) width) / 2f;

        if(gateZoom == 0.0f) gateZoom = this.width / 6f;
        if(gatePos == 0.0f) gatePos = (this.width - 54f);

        float step = 8f;
        if(!isUnloading) {
            if (this.gatePos < 54f) this.gatePos += step * 4f;

            if (this.gatePos > 57f) this.gatePos -= step * 4f;
        }
        else{
            if (this.gatePos < (this.width - 54f)) this.gatePos += step * 4f;

            if (this.gatePos+((step*4) + 25) >= (this.width - 54f)) this.mc.displayGuiScreen(this.parentScreen);
        }
        if (animationStage > 360) animationStage = 0f;
        updateRingSpeed();
    }
    // RENDER GUI
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // ------------------------------
        // ANIMATIONS AND SOUNDS

        updateAnimation();
        updateSound();

        // ------------------------------
        // DRAWING BACKGROUND

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawScaledCustomSizeModalRect(0, 0, 0, 0, width, height, width, height, width, height);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING WHOLE GATE MODEL

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.translate(this.gatePos, screenCenterHeight, 0f);
        GlStateManager.scale(this.gateZoom, this.gateZoom, this.gateZoom);
        GlStateManager.rotate(-180f, 0f, 0f, 1f);
        GlStateManager.rotate(180f, 0f, 1f, 0f);

        // ------------------------------
        // DRAWING GATE

        GlStateManager.pushMatrix();
        ElementEnum.MILKYWAY_GATE_MAINMENU.bindTextureAndRender(this.overlay);
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING RING

        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, -3f);
        // ring rotation animation
        GlStateManager.rotate(animationStage, 0, 0, 1);
        // -----
        ElementEnum.MILKYWAY_RING_MAINMENU.bindTextureAndRender(this.overlay);
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING CHEVRONS

        for (int i = 0; i < 9; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 0.03f);
            GlStateManager.rotate(ChevronEnum.valueOf(i).rotation, 0, 0, 1);
            ElementEnum.MILKYWAY_CHEVRON_FRAME_MAINMENU.bindTextureAndRender(this.overlay);
            GlStateManager.translate(0, 0, 0);
            ElementEnum.MILKYWAY_CHEVRON_LIGHT_MAINMENU.bindTextureAndRender(this.overlay);
            GlStateManager.translate(0, 0, 0);
            ElementEnum.MILKYWAY_CHEVRON_MOVING_MAINMENU.bindTextureAndRender(this.overlay);
            GlStateManager.popMatrix();
        }
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING BUTTONS

        if(!isUnloading) {
            availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
            selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
            drawCenteredString(this.fontRenderer, I18n.format("resourcePack.title"), this.width / 2, 16, 16777215);
            drawCenteredString(this.fontRenderer, I18n.format("resourcePack.folderInfo"), this.width / 2 - 102, this.height - 48, 8421504);
            GlStateManager.pushMatrix();
            for (GuiButton guiButton : this.aunisButtonList) {
                ((AunisGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            for (GuiButton guiButton : this.aunisButtonSliders) {
                ((AunisGuiSlider) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            for (GuiLabel guiLabel : this.labelList) {
                guiLabel.drawLabel(this.mc, mouseX, mouseY);
            }

            GlStateManager.popMatrix();
        }
    }

    protected void frame(int x, int y, int w, int h, int thickness, int borderColor, int background) {
        // Up
        drawRect(x, y, x + w, y + thickness, borderColor);
        // Down
        drawRect(x, y + h - thickness, x + w, y + h, borderColor);
        // Left
        drawRect(x, y + thickness, x + thickness, y + h - thickness, borderColor);
        // Right
        drawRect(x + w - thickness, y + thickness, x + w, y + h - thickness, borderColor);
        // Background
        drawRect(x + thickness, y + thickness, x + w - thickness, y + h - thickness, background);
    }

    @Override
    public void initGui()
    {
        screenCenterHeight = (((float) height) / 2f);
        screenCenterWidth = ((float) width) / 2f;

        aunisButtonList.clear();

        aunisButtonList.add(new AunisGuiButton(2, this.width / 2 - 205, this.height - 38, I18n.format("resourcePack.openFolder")));
        aunisButtonList.add(new AunisGuiButton(1, this.width / 2 + 5, this.height - 38, I18n.format("gui.done")));

        if (!this.changed)
        {
            availableResourcePacks = Lists.<ResourcePackListEntry>newArrayList();
            selectedResourcePacks = Lists.<ResourcePackListEntry>newArrayList();
            ResourcePackRepository resourcepackrepository = this.mc.getResourcePackRepository();
            resourcepackrepository.updateRepositoryEntriesAll();
            List<ResourcePackRepository.Entry> list = Lists.newArrayList(resourcepackrepository.getRepositoryEntriesAll());
            list.removeAll(resourcepackrepository.getRepositoryEntries());

            for (ResourcePackRepository.Entry resourcepackrepository$entry : list)
            {
                availableResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry));
            }

            ResourcePackRepository.Entry resourcepackrepository$entry2 = resourcepackrepository.getResourcePackEntry();

            if (resourcepackrepository$entry2 != null)
            {
                selectedResourcePacks.add(new ResourcePackListEntryServer(this, resourcepackrepository.getServerResourcePack()));
            }

            for (ResourcePackRepository.Entry resourcepackrepository$entry1 : Lists.reverse(resourcepackrepository.getRepositoryEntries()))
            {
                selectedResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry1));
            }

            selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
        }

        availableResourcePacksList = new AunisGuiResourcePacksAvailable(this.mc, 200, this.height, availableResourcePacks);
        availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        availableResourcePacksList.registerScrollButtons(7, 8);
        selectedResourcePacksList = new AunisGuiResourcePackSelected(this.mc, 200, this.height, selectedResourcePacks);
        selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
        selectedResourcePacksList.registerScrollButtons(7, 8);
        super.initGui();
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState())
        {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0)
            {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        }
        else if (k != -1)
        {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0)
            {
                return;
            }

            this.eventButton = -1;
            this.mouseReleased(i, j, k);
        }
        else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
        {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
        selectedResourcePacksList.handleMouseInput();
        availableResourcePacksList.handleMouseInput();
    }

    @Override
    public boolean hasResourcePackEntry(ResourcePackListEntry resourcePackEntry)
    {
        return selectedResourcePacks.contains(resourcePackEntry);
    }

    @Override
    public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry resourcePackEntry)
    {
        return this.hasResourcePackEntry(resourcePackEntry) ? selectedResourcePacks : availableResourcePacks;
    }

    @Override
    public List<ResourcePackListEntry> getAvailableResourcePacks()
    {
        return availableResourcePacks;
    }

    @Override
    public List<ResourcePackListEntry> getSelectedResourcePacks()
    {
        return selectedResourcePacks;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException{
        if (button.enabled){
            if (button.id == 2){
                File file1 = this.mc.getResourcePackRepository().getDirResourcepacks();
                OpenGlHelper.openFile(file1);
            }
            else if (button.id == 1)
            {
                if (this.changed){
                    List<ResourcePackRepository.Entry> list = Lists.<ResourcePackRepository.Entry>newArrayList();

                    for (ResourcePackListEntry resourcepacklistentry : selectedResourcePacks)
                    {
                        if (resourcepacklistentry instanceof ResourcePackListEntryFound)
                        {
                            list.add(((ResourcePackListEntryFound)resourcepacklistentry).getResourcePackEntry());
                        }
                    }

                    Collections.reverse(list);
                    mc.getResourcePackRepository().setRepositories(list);
                    mc.gameSettings.resourcePacks.clear();
                    mc.gameSettings.incompatibleResourcePacks.clear();

                    for (ResourcePackRepository.Entry resourcepackrepository$entry : list)
                    {
                        this.mc.gameSettings.resourcePacks.add(resourcepackrepository$entry.getResourcePackName());

                        if (resourcepackrepository$entry.getPackFormat() != 3)
                        {
                            this.mc.gameSettings.incompatibleResourcePacks.add(resourcepackrepository$entry.getResourcePackName());
                        }
                    }

                    mc.gameSettings.saveOptions();
                    mc.refreshResources();
                }

                isUnloading = true;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0)
        {
            for (GuiButton guibutton : this.aunisButtonList) {
                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.aunisButtonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.aunisButtonList));
                }
            }
        }
        availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
        selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void markChanged(){
        this.changed = true;
    }
}
