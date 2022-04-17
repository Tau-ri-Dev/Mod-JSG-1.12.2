package mrjake.aunis.gui.container.transportrings;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.element.*;
import mrjake.aunis.gui.element.Tab.SlotTab;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.SetOpenTabToServer;
import mrjake.aunis.packet.transportrings.SaveRingsParametersToServer;
import mrjake.aunis.stargate.power.StargateClassicEnergyStorage;
import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.transportrings.SymbolTypeTransportRingsEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.SlotItemHandler;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TRGui extends GuiContainer implements TabbedContainerInterface {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Aunis.ModID, "textures/gui/container_stargate.png");
    private final List<GuiTextField> textFields = new ArrayList<>();
    private final List<TextFieldLabel> labels = new ArrayList<>();
    private final TRContainer container;
    private List<Tab> tabs;
    private TabTRAddress goauldAddressTab;
    private TabTRAddress oriAddressTab;
    private int energyStored;
    private int maxEnergyStored;
    private GuiTextField nameTextField;
    private GuiTextField distanceTextField;

    private boolean keyTyped = false;

    private BlockPos pos;

    public TRGui(BlockPos pos, TRContainer container) {
        super(container);
        this.container = container;

        this.xSize = 176;
        this.ySize = 168;

        this.pos = pos;
    }

    @Override
    public void initGui() {
        super.initGui();

        tabs = new ArrayList<Tab>();

        goauldAddressTab = (TabTRAddress) TabTRAddress.builder()
                .setTile(container.trTile)
                .setSymbolType(SymbolTypeTransportRingsEnum.GOAULD)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 2)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.transportrings.goauld_address"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(1, 7)
                .setIconSize(20, 18)
                .setIconTextureLocation(304, 0).build();

        oriAddressTab = (TabTRAddress) TabTRAddress.builder()
                .setTile(container.trTile)
                .setSymbolType(SymbolTypeTransportRingsEnum.ORI)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 2)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.transportrings.ori_address"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(1, 7)
                .setIconSize(20, 18)
                .setIconTextureLocation(304, 0).build();

        tabs.add(goauldAddressTab);
        tabs.add(oriAddressTab);

        container.inventorySlots.set(7, goauldAddressTab.createSlot((SlotItemHandler) container.getSlot(7)));
        container.inventorySlots.set(8, oriAddressTab.createSlot((SlotItemHandler) container.getSlot(8)));

        textFields.clear();
        int y = 14;
        int id = 0;
        nameTextField = new GuiTextField(++id,
                Minecraft.getMinecraft().fontRenderer, 50, y + 10,
                50, 10);
        nameTextField.setText(container.trTile.getRingsName());
        textFields.add(nameTextField);
        labels.add(new TextFieldLabel(50, y + 2, "tile.aunis.transportrings_block.rings_name"));

        y += 20;
        distanceTextField = new GuiTextField(++id,
                Minecraft.getMinecraft().fontRenderer, 50, y + 10,
                50, 10);
        distanceTextField.setText(container.trTile.getRingsDistance() + "");
        textFields.add(distanceTextField);
        labels.add(new TextFieldLabel(50, y + 2, "tile.aunis.transportrings_block.rings_distance"));
    }


    // todo(Mine): temporarily solution
    public void tryToUpdateInputs(){
        if(!keyTyped){
            try {
                String name = container.trTile.getRingsName();
                int distance = container.trTile.getRingsDistance();
                if (!(nameTextField.getText().equals(name)))
                    nameTextField.setText(name);
                if (Integer.parseInt(distanceTextField.getText()) != distance)
                    distanceTextField.setText(distance + "");
            }
            catch (Exception ignored){}
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        tryToUpdateInputs();

        boolean hasGoauldUpgrade = false;
        boolean hasOriUpgrade = false;

        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = container.getSlot(i).getStack();

            if (!itemStack.isEmpty()) {
                TransportRingsAbstractTile.TransportRingsUpgradeEnum upgrade = TransportRingsAbstractTile.TransportRingsUpgradeEnum.valueOf(itemStack.getItem());
                if (upgrade == null) continue;
                switch (upgrade) {
                    case GOAULD_UPGRADE:
                        hasGoauldUpgrade = true;
                        break;
                    case ORI_UPGRADE:
                        hasOriUpgrade = true;
                        break;
                }
            }
        }

        goauldAddressTab.setVisible(hasGoauldUpgrade);
        oriAddressTab.setVisible(hasOriUpgrade);

        Tab.updatePositions(tabs);

        StargateClassicEnergyStorage energyStorageInternal = (StargateClassicEnergyStorage) container.trTile.getCapability(CapabilityEnergy.ENERGY, null);
        energyStored = energyStorageInternal.getEnergyStoredInternally();
        maxEnergyStored = energyStorageInternal.getMaxEnergyStoredInternally();

        for (int i = 4; i < 7; i++) {
            IEnergyStorage energyStorage = container.getSlot(i).getStack().getCapability(CapabilityEnergy.ENERGY, null);

            if (energyStorage == null)
                continue;

            energyStored += energyStorage.getEnergyStored();
            maxEnergyStored += energyStorage.getMaxEnergyStored();
        }

        for (int i = 7; i < container.trTile.getSlotsCount(); i++)
            ((SlotTab) container.getSlot(i)).updatePos();


        super.drawScreen(mouseX, mouseY, partialTicks);

        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        for (Tab tab : tabs) {
            tab.render(fontRenderer, mouseX, mouseY);
        }

        mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GlStateManager.color(1, 1, 1, 1);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);

        // Draw cross on inactive capacitors
        for (int i = 0; i < 3 - container.trTile.getSupportedCapacitors(); i++) {
            drawModalRectWithCustomSizedTexture(guiLeft + 151 - 18 * i, guiTop + 40, 24, 175, 16, 16, 512, 512);
        }

        for (int i = container.trTile.getPowerTier(); i < 4; i++)
            drawModalRectWithCustomSizedTexture(guiLeft + 10 + 39 * i, guiTop + 61, 0, 168, 39, 6, 512, 512);

        int width = Math.round((energyStored / (float) AunisConfig.powerConfig.stargateEnergyStorage * 156));
        drawGradientRect(guiLeft + 10, guiTop + 61, guiLeft + 10 + width, guiTop + 61 + 6, 0xffcc2828, 0xff731616);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("gui.stargate.capacitors"), 112, 29, 4210752);

        String energyPercent = String.format("%.2f", energyStored / (float) maxEnergyStored * 100) + " %";
        fontRenderer.drawString(energyPercent, 168 - fontRenderer.getStringWidth(energyPercent) + 2, 71, 4210752);

        fontRenderer.drawString(I18n.format("gui.upgrades"), 7, 6, 4210752);
        // if (!container.trTile.getRings().isInGrid())
        //    fontRenderer.drawString(I18n.format("tile.aunis.transportrings_block.rings_not_in_grid"), 7, 6, 0xB36262);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        for (TextFieldLabel label : labels)
            fontRenderer.drawString(I18n.format(label.localized), label.x, label.y, 4210752);

        for (GuiTextField tf : textFields)
            tf.drawTextBox();

        for (Tab tab : tabs) {
            tab.renderFg(this, fontRenderer, mouseX, mouseY);
        }

        int transferred = container.trTile.getEnergyTransferedLastTick();
        TextFormatting transferredFormatting = TextFormatting.GRAY;
        String transferredSign = "";

        if (transferred > 0) {
            transferredFormatting = TextFormatting.GREEN;
            transferredSign = "+";
        } else if (transferred < 0) {
            transferredFormatting = TextFormatting.RED;
        }

        if (isPointInRegion(10, 61, 156, 6, mouseX, mouseY)) {
            List<String> power = Arrays.asList(
                    I18n.format("gui.transportrings.energyBuffer"),
                    TextFormatting.GRAY + String.format("%,d / %,d RF", energyStored, maxEnergyStored),
                    transferredFormatting + transferredSign + String.format("%,d RF/t", transferred));
            drawHoveringText(power, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (GuiTextField tf : textFields)
            tf.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);

        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);

            if (tab.isCursorOnTab(mouseX, mouseY)) {
                if (Tab.tabsInteract(tabs, i)) {
                    container.setOpenTabId(i);
                } else
                    container.setOpenTabId(-1);

                AunisPacketHandler.INSTANCE.sendToServer(new SetOpenTabToServer(container.getOpenTabId()));

                break;
            }
        }
    }

    @Override
    public List<Rectangle> getGuiExtraAreas() {
        return tabs.stream()
                .map(tab -> tab.getArea())
                .collect(Collectors.toList());
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (GuiTextField tf : textFields) {
            if(tf.textboxKeyTyped(typedChar, keyCode))
                keyTyped = true;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        for (GuiTextField tf : textFields)
            tf.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        saveData();
        super.onGuiClosed();
    }

    public void saveData() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        try {
            String name = nameTextField.getText();
            try {
                int distance = Integer.parseInt(distanceTextField.getText());

                if (distance >= -40 && distance <= 40) {
                    AunisPacketHandler.INSTANCE.sendToServer(new SaveRingsParametersToServer(pos, name, distance));
                    container.trTile.setRingsParams(name, distance);
                } else {
                    player.sendStatusMessage(new TextComponentTranslation("tile.aunis.transportrings_block.wrong_distance"), true);
                }
            } catch (NumberFormatException e) {
                player.sendStatusMessage(new TextComponentTranslation("tile.aunis.transportrings_block.wrong_distance"), true);
            }
        } catch (NumberFormatException e) {
            player.sendStatusMessage(new TextComponentTranslation("tile.aunis.transportrings_block.wrong_address"), true);
        }
    }
}
