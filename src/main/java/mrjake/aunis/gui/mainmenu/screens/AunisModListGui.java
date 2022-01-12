package mrjake.aunis.gui.mainmenu.screens;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Strings;
import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.base.AunisGuiButton;
import mrjake.aunis.gui.mainmenu.screens.modlist.AunisGuiScrollingList;
import mrjake.aunis.gui.mainmenu.screens.modlist.AunisGuiSlotModList;
import mrjake.aunis.gui.mainmenu.screens.options.AunisLanguageOptions;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import mrjake.aunis.sound.AunisSoundHelperClient;
import mrjake.aunis.sound.SoundPositionedEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.WHITE;

/**
 * @author cpw
 *
 */
public class AunisModListGui extends GuiModList
{
    private enum SortType implements Comparator<ModContainer>
    {
        NORMAL(24),
        A_TO_Z(25){ @Override protected int compare(String name1, String name2){ return name1.compareTo(name2); }},
        Z_TO_A(26){ @Override protected int compare(String name1, String name2){ return name2.compareTo(name1); }};

        private final int buttonID;

        SortType(int buttonID)
        {
            this.buttonID = buttonID;
        }

        @Nullable
        public static SortType getTypeForButton(GuiButton button)
        {
            for (SortType t : values())
            {
                if (t.buttonID == button.id)
                {
                    return t;
                }
            }
            return null;
        }

        protected int compare(String name1, String name2){ return 0; }

        @Override
        public int compare(ModContainer o1, ModContainer o2)
        {
            String name1 = StringUtils.stripControlCodes(o1.getName()).toLowerCase();
            String name2 = StringUtils.stripControlCodes(o2.getName()).toLowerCase();
            return compare(name1, name2);
        }
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

    protected AunisLanguageOptions.List list;

    protected BiomeOverlayEnum overlay;
    protected float screenCenterHeight = (((float) height) / 2f);
    protected float screenCenterWidth = ((float) width) / 2f;
    protected java.util.List<GuiButton> aunisButtonList = new ArrayList<>();
    protected static final ResourceLocation BACKGROUND_TEXTURE = AunisConfig.mainMenuConfig.disableAunisMainMenu ? null : new ResourceLocation(Aunis.ModID, "textures/gui/mainmenu/background.jpg");

    // render kawoosh and event horizon
    protected float kawooshState = 0f;
    protected float gateZoom = 0.0f;
    protected float gatePos = 0.0f;
    protected float gatePosY = 0.0f;
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

    protected GuiScreen mainMenu;
    protected AunisGuiSlotModList modList;
    protected ArrayList<ModContainer> mods;
    protected AunisGuiScrollingList modInfo;
    protected ModContainer selectedMod;
    protected int selected = -1;
    protected int listWidth;
    protected AunisGuiButton configModButton;
    protected AunisGuiButton disableModButton;
    protected int eventButton;
    protected int touchValue;
    protected long lastMouseEvent;

    protected int buttonMargin = 1;
    protected int numButtons = SortType.values().length;

    protected String lastFilterText = "";

    protected GuiTextField search;
    protected boolean sorted = false;
    protected SortType sortType = SortType.NORMAL;

    public AunisModListGui(GuiScreen mainMenu, BiomeOverlayEnum overlay){
        super(mainMenu);
        this.mainMenu = mainMenu;
        this.mods = new ArrayList<>();
        this.overlay = overlay;
        FMLClientHandler.instance().addSpecialModEntries(mods);
        // Add child mods to their parent's list
        for (ModContainer mod : Loader.instance().getModList())
        {
            if (mod.getMetadata() != null && mod.getMetadata().parentMod == null && !Strings.isNullOrEmpty(mod.getMetadata().parent))
            {
                String parentMod = mod.getMetadata().parent;
                ModContainer parentContainer = Loader.instance().getIndexedModList().get(parentMod);
                if (parentContainer != null)
                {
                    mod.getMetadata().parentMod = parentContainer;
                    parentContainer.getMetadata().childMods.add(mod);
                    continue;
                }
            }
            else if (mod.getMetadata() != null && mod.getMetadata().parentMod != null)
            {
                continue;
            }
            mods.add(mod);
        }
    }

    public Minecraft getMinecraftInstance()
    {
        return mc;
    }

    public FontRenderer getFontRenderer()
    {
        return fontRenderer;
    }

    @Override
    public void initGui()
    {
        aunisButtonList.clear();
        int slotHeight = 35;
        for (ModContainer mod : mods)
        {
            listWidth = Math.max(listWidth,fontRenderer.getStringWidth(mod.getName()) + 10);
            listWidth = Math.max(listWidth,fontRenderer.getStringWidth(mod.getVersion()) + 5 + slotHeight);
        }
        listWidth = Math.min(listWidth, 150);
        this.modList = new AunisGuiSlotModList(this, mods, listWidth, slotHeight);
        this.aunisButtonList.add(new AunisGuiButton(6, ((modList.right + this.width) / 2) - 100, this.height - 38, I18n.format("gui.done")));
        configModButton = new AunisGuiButton(20, 10, this.height - 49, this.listWidth, 20, "Config");
        disableModButton = new AunisGuiButton(21, 10, this.height - 27, this.listWidth, 20, "Disable");
        this.aunisButtonList.add(configModButton);
        this.aunisButtonList.add(disableModButton);

        search = new GuiTextField(0, getFontRenderer(), 12, modList.bottom + 17, modList.listWidth - 4, 14);
        search.setFocused(true);
        search.setCanLoseFocus(true);

        int width = (modList.listWidth / numButtons);
        int x = 10, y = 10;
        AunisGuiButton normalSort = new AunisGuiButton(SortType.NORMAL.buttonID, x, y, width - buttonMargin, 20, I18n.format("fml.menu.mods.normal"));
        normalSort.enabled = false;
        aunisButtonList.add(normalSort);
        x += width + buttonMargin;
        aunisButtonList.add(new AunisGuiButton(SortType.A_TO_Z.buttonID, x, y, width - buttonMargin, 20, "A-Z"));
        x += width + buttonMargin;
        aunisButtonList.add(new AunisGuiButton(SortType.Z_TO_A.buttonID, x, y, width - buttonMargin, 20, "Z-A"));

        updateCache();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException{
        if (button.enabled)
        {
            AunisModListGui.SortType type = AunisModListGui.SortType.getTypeForButton(button);

            if (type != null)
            {
                for (GuiButton b : aunisButtonList)
                {
                    if (AunisModListGui.SortType.getTypeForButton(b) != null)
                    {
                        b.enabled = true;
                    }
                }
                button.enabled = false;
                sorted = false;
                sortType = type;
                this.mods = modList.getMods();
            }
            else
            {
                switch (button.id)
                {
                    case 6:{
                        isUnloading = true;
                        return;
                    }
                    case 20:{
                        try{
                            IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(selectedMod);
                            GuiScreen newScreen = guiFactory.createConfigGui(this);
                            this.mc.displayGuiScreen(newScreen);
                        }
                        catch (Exception e){
                            FMLLog.log.error("There was a critical issue trying to build the config GUI for {}", selectedMod.getModId(), e);
                        }
                    }
                }
            }
        }
    }

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

        if(gateZoom == 0.0f) gateZoom = ((((float) height) / 10f) - 3);
        if(gatePos == 0.0f) gatePos = ((float) width) / 2f;
        if(gatePosY == 0.0f) gatePosY = ((float) width) / 2f;

        float step = 8f;
        if(!isUnloading) {
            if (this.gatePos < (this.width - 54f)) this.gatePos += step * 4f;
            if (this.gatePosY < 54f) this.gatePosY += step * 4f;
            if (this.gateZoom < (this.width / 6f)) this.gateZoom += step;

            if (this.gatePos > (this.width - 57f)) this.gatePos -= step * 4f;
            if (this.gatePosY > 57f) this.gatePosY -= step * 4f;
            if (this.gateZoom > ((this.width / 6f) + 5f)) this.gateZoom -= step;
        }
        else{
            if (this.gatePos > 0.0f) this.gatePos -= step * 4f;
            if (this.gatePosY > 0.0f) this.gatePosY -= step * 4f;
            if (this.gateZoom > 0.0f) this.gateZoom -= step;

            if (this.gateZoom+step <= 0.0f) this.mc.displayGuiScreen(this.mainMenu);
        }
        if (animationStage > 360) animationStage = 0f;
        updateRingSpeed();
    }

    // RENDER GUI
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
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
        GlStateManager.translate(this.gatePos, gatePosY, 0f);
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
            GlStateManager.pushMatrix();
            GlStateManager.enableTexture2D();
            this.modList.drawScreen(mouseX, mouseY, partialTicks);
            if (this.modInfo != null)
                this.modInfo.drawScreen(mouseX, mouseY, partialTicks);

            int left = ((this.width - this.listWidth - 38) / 2) + this.listWidth + 30;
            this.drawCenteredString(this.fontRenderer, "Mod List", left, 16, 0xFFFFFF);

            for (GuiButton guiButton : this.aunisButtonList) {
                ((AunisGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            for (GuiLabel guiLabel : this.labelList) {
                guiLabel.drawLabel(this.mc, mouseX, mouseY);
            }

            String text = I18n.format("fml.menu.mods.search");
            int x = ((10 + modList.right) / 2) - (getFontRenderer().getStringWidth(text) / 2);
            getFontRenderer().drawString(text, x, modList.bottom + 5, 0xFFFFFF);
            search.drawTextBox();
            GlStateManager.disableTexture2D();
            GlStateManager.popMatrix();
        }
    }

    private void updateCache(){
        configModButton.visible = false;
        disableModButton.visible = false;
        modInfo = null;

        if (selectedMod == null)
            return;

        ResourceLocation logoPath = null;
        Dimension logoDims = new Dimension(0, 0);
        List<String> lines = new ArrayList<>();
        ForgeVersion.CheckResult vercheck = ForgeVersion.getResult(selectedMod);

        String logoFile = selectedMod.getMetadata().logoFile;
        if (!logoFile.isEmpty())
        {
            TextureManager tm = mc.getTextureManager();
            IResourcePack pack = FMLClientHandler.instance().getResourcePackFor(selectedMod.getModId());
            try
            {
                BufferedImage logo = null;
                if (pack != null)
                {
                    logo = pack.getPackImage();
                }
                else
                {
                    InputStream logoResource = getClass().getResourceAsStream(logoFile);
                    if (logoResource != null)
                        logo = TextureUtil.readBufferedImage(logoResource);
                }
                if (logo != null)
                {
                    logoPath = tm.getDynamicTextureLocation("modlogo", new DynamicTexture(logo));
                    logoDims = new Dimension(logo.getWidth(), logo.getHeight());
                }
            }
            catch (IOException ignored) { }
        }

        if (!selectedMod.getMetadata().autogenerated)
        {
            disableModButton.visible = true;
            disableModButton.enabled = true;
            disableModButton.packedFGColour = 0;
            ModContainer.Disableable disableable = selectedMod.canBeDisabled();
            if (disableable == ModContainer.Disableable.RESTART)
            {
                disableModButton.packedFGColour = 0xFF3377;
            }
            else if (disableable != ModContainer.Disableable.YES)
            {
                disableModButton.enabled = false;
            }

            IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(selectedMod);
            configModButton.visible = true;
            configModButton.enabled = false;
            if (guiFactory != null)
            {
                configModButton.enabled = guiFactory.hasConfigGui();
            }
            lines.add(selectedMod.getMetadata().name);
            lines.add(String.format("Version: %s (%s)", selectedMod.getDisplayVersion(), selectedMod.getVersion()));
            lines.add(String.format("Mod ID: '%s' Mod State: %s", selectedMod.getModId(), Loader.instance().getModState(selectedMod)));

            if (!selectedMod.getMetadata().credits.isEmpty())
            {
                lines.add("Credits: " + selectedMod.getMetadata().credits);
            }

            lines.add("Authors: " + selectedMod.getMetadata().getAuthorList());
            lines.add("URL: " + selectedMod.getMetadata().url);

            if (selectedMod.getMetadata().childMods.isEmpty())
                lines.add("No child mods for this mod");
            else
                lines.add("Child mods: " + selectedMod.getMetadata().getChildModList());

            if (vercheck.status == ForgeVersion.Status.OUTDATED || vercheck.status == ForgeVersion.Status.BETA_OUTDATED)
                lines.add("Update Available: " + (vercheck.url == null ? "" : vercheck.url));

            lines.add(null);
            lines.add(selectedMod.getMetadata().description);
        }
        else
        {
            lines.add(WHITE + selectedMod.getName());
            lines.add(WHITE + "Version: " + selectedMod.getVersion());
            lines.add(WHITE + "Mod State: " + Loader.instance().getModState(selectedMod));
            if (vercheck.status == ForgeVersion.Status.OUTDATED || vercheck.status == ForgeVersion.Status.BETA_OUTDATED)
                lines.add("Update Available: " + (vercheck.url == null ? "" : vercheck.url));

            lines.add(null);
            lines.add(RED + "No mod information found");
            lines.add(RED + "Ask your mod author to provide a mod mcmod.info file");
        }

        if ((vercheck.status == ForgeVersion.Status.OUTDATED || vercheck.status == ForgeVersion.Status.BETA_OUTDATED) && vercheck.changes.size() > 0)
        {
            lines.add(null);
            lines.add("Changes:");
            for (Map.Entry<ComparableVersion, String> entry : vercheck.changes.entrySet())
            {
                lines.add("  " + entry.getKey() + ":");
                lines.add(entry.getValue());
                lines.add(null);
            }
        }

        modInfo = new Info(this.width - this.listWidth - 30, lines, logoPath, logoDims);
    }

    public class Info extends AunisGuiScrollingList{
        @Nullable
        private final ResourceLocation logoPath;
        private final Dimension logoDims;
        private final List<ITextComponent> lines;

        public Info(int width, List<String> lines, @Nullable ResourceLocation logoPath, Dimension logoDims)
        {
            super(AunisModListGui.this.getMinecraftInstance(),
                    width,
                    AunisModListGui.this.height,
                    32, AunisModListGui.this.height - 88 + 4,
                    AunisModListGui.this.listWidth + 20, 60,
                    AunisModListGui.this.width,
                    AunisModListGui.this.height);
            this.lines    = resizeContent(lines);
            this.logoPath = logoPath;
            this.logoDims = logoDims;

            this.setHeaderInfo(true, getHeaderHeight());
        }

        @Override protected int getSize() { return 0; }
        @Override protected void elementClicked(int index, boolean doubleClick) { }
        @Override protected boolean isSelected(int index) { return false; }
        @Override protected void drawBackground() {}
        @Override protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) { }

        private List<ITextComponent> resizeContent(List<String> lines)
        {
            List<ITextComponent> ret = new ArrayList<>();
            for (String line : lines)
            {
                if (line == null)
                {
                    ret.add(null);
                    continue;
                }

                ITextComponent chat = ForgeHooks.newChatWithLinks(line, false);
                int maxTextLength = this.listWidth - 8;
                if (maxTextLength >= 0){
                    ret.addAll(GuiUtilRenderComponents.splitText(chat, maxTextLength, AunisModListGui.this.fontRenderer, false, true));
                }
            }
            return ret;
        }

        private int getHeaderHeight()
        {
            int height = 0;
            if (logoPath != null)
            {
                double scaleX = logoDims.width / 200.0;
                double scaleY = logoDims.height / 65.0;
                double scale = 1.0;
                if (scaleX > 1 || scaleY > 1)
                {
                    scale = 1.0 / Math.max(scaleX, scaleY);
                }
                logoDims.width *= scale;
                logoDims.height *= scale;

                height += logoDims.height;
                height += 10;
            }
            height += (lines.size() * 10);
            if (height < this.bottom - this.top - 8) height = this.bottom - this.top - 8;
            return height;
        }


        @Override
        protected void drawHeader(int entryRight, int relativeY, Tessellator tess)
        {
            int top = relativeY;

            if (logoPath != null)
            {
                GlStateManager.enableBlend();
                AunisModListGui.this.mc.renderEngine.bindTexture(logoPath);
                BufferBuilder wr = tess.getBuffer();
                int offset = (this.left + this.listWidth/2) - (logoDims.width / 2);
                wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                wr.pos(offset,                  top + logoDims.height, zLevel).tex(0, 1).endVertex();
                wr.pos(offset + logoDims.width, top + logoDims.height, zLevel).tex(1, 1).endVertex();
                wr.pos(offset + logoDims.width, top,                   zLevel).tex(1, 0).endVertex();
                wr.pos(offset,                  top,                   zLevel).tex(0, 0).endVertex();
                tess.draw();
                GlStateManager.disableBlend();
                top += logoDims.height + 10;
            }

            for (ITextComponent line : lines)
            {
                if (line != null)
                {
                    GlStateManager.enableBlend();
                    AunisModListGui.this.fontRenderer.drawStringWithShadow(line.getFormattedText(), this.left + 4, top, 0xFFFFFF);
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                }
                top += 10;
            }
        }

        @Override
        protected void clickHeader(int x, int y)
        {
            int offset = y;
            if (logoPath != null) {
                offset -= logoDims.height + 10;
            }
            if (offset <= 0)
                return;

            int lineIdx = offset / 10;
            if (lineIdx >= lines.size())
                return;

            ITextComponent line = lines.get(lineIdx);
            if (line != null)
            {
                int k = -4;
                for (ITextComponent part : line) {
                    if (!(part instanceof TextComponentString))
                        continue;
                    k += AunisModListGui.this.fontRenderer.getStringWidth(((TextComponentString)part).getText());
                    if (k >= x)
                    {
                        AunisModListGui.this.handleComponentClick(part);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void updateScreen()
    {
        search.updateCursorCounter();

        if (!search.getText().equals(lastFilterText))
        {
            reloadMods();
            sorted = false;
        }

        if (!sorted)
        {
            reloadMods();
            mods.sort(sortType);
            selected = modList.selectedIndex = mods.indexOf(selectedMod);
            sorted = true;
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException
    {
        if (button == 0)
        {
            for (int i = 0; i < this.aunisButtonList.size(); ++i)
            {
                GuiButton guibutton = this.aunisButtonList.get(i);

                if (guibutton.mousePressed(this.mc, x, y))
                {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.aunisButtonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.aunisButtonList));
                }
            }
        }
        search.mouseClicked(x, y, button);
        if (button == 1 && x >= search.x && x < search.x + search.width && y >= search.y && y < search.y + search.height) {
            search.setText("");
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

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
        if (this.modInfo != null)
            this.modInfo.handleMouseInput(mouseX, mouseY);
        this.modList.handleMouseInput(mouseX, mouseY);
    }

    @Override
    public void selectModIndex(int index)
    {
        if (index == this.selected)
            return;
        this.selected = index;
        this.selectedMod = (index >= 0 && index <= mods.size()) ? mods.get(selected) : null;

        updateCache();
    }

    @Override
    protected void keyTyped(char c, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
        search.textboxKeyTyped(c, keyCode);
    }

    @Override
    public boolean modIndexSelected(int index)
    {
        return index == selected;
    }

    private void reloadMods()
    {
        ArrayList<ModContainer> mods = modList.getMods();
        mods.clear();
        for (ModContainer m : Loader.instance().getActiveModList())
        {
            // If it passes the filter, and is not a child mod
            if (m.getName().toLowerCase().contains(search.getText().toLowerCase()) && m.getMetadata().parentMod == null)
            {
                mods.add(m);
            }
        }
        this.mods = mods;
        lastFilterText = search.getText();
    }
}
