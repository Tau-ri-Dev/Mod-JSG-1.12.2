package tauri.dev.jsg.gui.admincontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.client.config.GuiUtils;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.base.JSGTextField;
import tauri.dev.jsg.gui.element.ArrowButton;
import tauri.dev.jsg.gui.element.GuiHelper;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.gui.entry.EntryActionToServer;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.util.BlockHelpers;

import java.util.*;

public class AddressesSection {

    public ArrayList<StargateEntry> entries = new ArrayList<>();

    public int guiTop;
    public int height;
    public int guiLeft;
    public int width;

    public static final int OFFSET = 15;

    public int scrolled = 0;
    public GuiAdminController guiBase;

    public AddressesSection(GuiAdminController baseGui) {
        this.guiBase = baseGui;
    }

    public void generateAddressEntries() {
        if (guiBase.stargateNetwork == null) {
            entries.clear();
            return;
        }
        if (entries.size() > 0) return;

        Map<StargateAddress, StargatePos> m = guiBase.stargateNetwork.getMap().get(Objects.requireNonNull(guiBase.gateTile).getSymbolType());
        for (StargateAddress a : m.keySet()) {
            StargateEntry e = new StargateEntry();
            e.pos = m.get(a);
            e.address = a;
            entries.add(e);
        }

        Map<StargatePos, Map<SymbolTypeEnum, StargateAddress>> notGeneratedMap = guiBase.stargateNetwork.getMapNotGenerated();
        for (StargatePos pos : notGeneratedMap.keySet()) {
            StargateEntry e = new StargateEntry();
            e.pos = pos;
            e.address = notGeneratedMap.get(pos).get(Objects.requireNonNull(guiBase.gateTile).getSymbolType());
            e.notGenerated = true;
            e.defaultName = "[NOT GENERATED GATE] ";
            entries.add(e);
        }
        sortEntries();
    }

    public ArrayList<ArrowButton> dialButtons = new ArrayList<>();
    public ArrayList<GuiTextField> entriesTextFields = new ArrayList<>();

    public void generateAddressEntriesBoxes(boolean reset) {
        int index = -1;
        if (!reset && dialButtons.size() > 0) return;
        if (!reset && entriesTextFields.size() > 0) return;
        dialButtons.clear();
        entriesTextFields.clear();
        if (entries.size() < 1) return;
        for (StargateEntry e : entries) {
            index++;
            StargatePos p = e.pos;
            String name = (p.getName() != null && !p.getName().equalsIgnoreCase("") ? p.getName() : BlockHelpers.blockPosToBetterString(p.gatePos));
            final int finalIndex = index;
            JSGTextField field = new JSGTextField(index, Minecraft.getMinecraft().fontRenderer, guiLeft, 0, 120, 20, e.defaultName + name);
            field.setText(e.defaultName + name);
            if (!e.notGenerated) {
                field.setActionCallback(() -> renameEntry(finalIndex));
            } else {
                field.setEnabled(false);
            }
            ArrowButton btn = (ArrowButton) new ArrowButton(index, guiLeft + 120 + 5, 0, ArrowButton.ArrowType.RIGHT).setFgColor(GuiUtils.getColorCode('a', true)).setActionCallback(() -> dialGate(finalIndex));
            if (e.pos.gatePos.equals(Objects.requireNonNull(guiBase.gateTile).getPos()) && e.pos.dimensionID == guiBase.gateTile.world().provider.getDimension()) {
                btn.setEnabled(false);
                btn.setActionCallback(() -> {
                });
            }

            entriesTextFields.add(field);
            dialButtons.add(btn);
        }
    }

    public void sortEntries() {
        ArrayList<StargateEntry> newList = new ArrayList<>();
        for (StargateEntry e : entries) {
            if (!e.pos.getName().equals(""))
                newList.add(e);
        }
        for (StargateEntry e : entries) {
            if (e.pos.getName().equals(""))
                newList.add(e);
        }
        entries = newList;
    }

    public void dialGate(int index) {
        try {
            EnumHand hand = guiBase.getHand();
            StargateEntry entry = entries.get(index);
            StargatePos pos = entry.pos;
            if (guiBase.gateTile == null || guiBase.imaginaryGateTile == null){
                guiBase.notifer.setText("Linked gate is NULL!", Notifier.EnumAlertType.ERROR, 5);
                return;
            }
            if (!guiBase.imaginaryGateTile.getStargateState().idle() && !guiBase.imaginaryGateTile.getStargateState().engaged()){
                guiBase.notifer.setText("Stargate is busy!", Notifier.EnumAlertType.WARNING, 5);
                return;
            }

            if(!guiBase.imaginaryGateTile.getStargateState().engaged())
                guiBase.notifer.setText("Dialing gate " + (pos.getName().equals("") ? DimensionManager.getProviderType(pos.dimensionID).getName() : pos.getName()), Notifier.EnumAlertType.INFO, 5);
            else
                guiBase.notifer.setText("Closing gate...", Notifier.EnumAlertType.INFO, 5);

            int symbolsCount = Objects.requireNonNull(guiBase.gateTile).getMinimalSymbolsToDial(pos.getGateSymbolType(), pos);

            JSGPacketHandler.INSTANCE.sendToServer(new EntryActionToServer(hand, new StargateAddressDynamic(entry.address), symbolsCount, guiBase.gateTile.getPos()));
        }
        catch (Exception e){
            JSG.error("Error ", e);
            guiBase.notifer.setText("Unknown error! (" + e.getMessage() + ")", Notifier.EnumAlertType.ERROR, 5);
        }
    }

    public void renameEntry(int index) {
        try {
            EnumHand hand = guiBase.getHand();
            GuiTextField field = entriesTextFields.get(index);
            JSGPacketHandler.INSTANCE.sendToServer(new EntryActionToServer(hand, field.getText(), entries.get(index).pos));
            guiBase.notifer.setText("Gate renamed to " + field.getText(), Notifier.EnumAlertType.INFO, 5);
        } catch (Exception e) {
            JSG.error(e);
            guiBase.notifer.setText("Unknown error! (" + e.getMessage() + ")", Notifier.EnumAlertType.ERROR, 5);
        }
    }

    public void updateY() {
        for (GuiTextField f : entriesTextFields) {
            f.y = (scrolled + (f.getId() * 23)) + OFFSET + guiTop;
        }
        for (ArrowButton f : dialButtons) {
            f.y = (scrolled + (f.id * 23)) + OFFSET + guiTop;
        }
    }

    public void renderEntries() {
        updateY();

        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.pushMatrix();
        for (GuiTextField f : entriesTextFields) {
            if (canNotRenderEntry(f.y)) continue;
            f.drawTextBox();
        }
        for (ArrowButton b : dialButtons) {
            if (canNotRenderEntry(b.y)) continue;
            b.drawButton(Minecraft.getMinecraft(), guiBase.mouseX, guiBase.mouseY, guiBase.partialTicks);
        }
        GlStateManager.popMatrix();
    }

    public void renderFg() {
        // Render tooltips
        for (GuiTextField f : entriesTextFields) {
            StargateEntry e = entries.get(f.getId());
            if (!canNotRenderEntry(f.y) && GuiHelper.isPointInRegion(f.x, f.y, f.width, f.height, guiBase.mouseX, guiBase.mouseY)) {
                Util.drawHoveringText(Arrays.asList(
                        "Type: " + e.pos.getGateSymbolType().toString(),
                        "Pos: " + e.pos.gatePos.toString(),
                        "Dim: " + e.pos.dimensionID + " (" + DimensionManager.getProviderType(e.pos.dimensionID).getName() + ")"
                ), guiBase.mouseX, guiBase.mouseY, guiBase.width, guiBase.height, -1, guiBase.mc.fontRenderer);
            }
        }
        for (ArrowButton f : dialButtons) {
            if (!canNotRenderEntry(f.y) && f.enabled && GuiHelper.isPointInRegion(f.x, f.y, f.width, f.height, guiBase.mouseX, guiBase.mouseY)) {
                Util.drawHoveringText(Collections.singletonList("Dial this address"), guiBase.mouseX, guiBase.mouseY, guiBase.width, guiBase.height, -1, guiBase.mc.fontRenderer);
            }
        }
    }

    // ----------------------------------------------------------
    protected static final int SCROLL_AMOUNT = 5;

    public void scroll(int k) {
        if (k == 0) return;
        if (k < 0) k = -1;
        if (k > 0) k = 1;
        if (canContinueScrolling(k)) {
            scrolled += (SCROLL_AMOUNT * k);
        }
    }

    public boolean canContinueScrolling(int k) {
        int top = guiTop + OFFSET;
        int bottom = guiTop + height - OFFSET;
        if (entriesTextFields.size() < 1 && dialButtons.size() < 1) return false;

        boolean isTop = ((entriesTextFields.size() > 0 && entriesTextFields.get(0).getId() < dialButtons.get(0).id) ? entriesTextFields.get(0).y > top : dialButtons.get(0).y > top);
        boolean isBottom = ((entriesTextFields.size() > 0 && entriesTextFields.get(entriesTextFields.size() - 1).getId() >= dialButtons.get(dialButtons.size() - 1).id) ? entriesTextFields.get(entriesTextFields.size() - 1).y < bottom : dialButtons.get(dialButtons.size() - 1).y < bottom);

        return (!isTop && k == 1) || (!isBottom && k == -1);
    }

    public boolean canNotRenderEntry(int y) {
        int top = guiTop + OFFSET;
        int bottom = guiTop + height - OFFSET;
        int height = 23;
        return y < top || (y + height) > bottom;
    }
}
