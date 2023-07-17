package tauri.dev.jsg.gui.admincontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.base.JSGButton;
import tauri.dev.jsg.gui.base.JSGTextField;
import tauri.dev.jsg.gui.element.GuiHelper;
import tauri.dev.jsg.gui.element.ModeButton;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.gui.entry.EntryActionEnum;
import tauri.dev.jsg.packet.gui.entry.EntryActionToServer;
import tauri.dev.jsg.stargate.EnumIrisState;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.util.BlockHelpers;

import java.util.*;

public class AddressesSection {

    public static final int OFFSET = 15;
    // ----------------------------------------------------------
    protected static final int SCROLL_AMOUNT = 5;
    public ArrayList<StargateEntry> entries = new ArrayList<>();
    public int guiTop;
    public int height;
    public int guiLeft;
    public int width;
    public int scrolled = 0;
    public GuiAdminController guiBase;
    public int thisGateEntryIndex = -1;
    public ArrayList<ModeButton> dialButtons = new ArrayList<>();
    public ArrayList<JSGButton> optionButtons = new ArrayList<>();
    public ArrayList<GuiTextField> entriesTextFields = new ArrayList<>();

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

    public void init(boolean reset) {
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
            ModeButton btn = new ModeButton(index, guiLeft + 125, 0, 20, new ResourceLocation(JSG.MOD_ID, "textures/gui/controller_mode.png"), 80, 40, 4);
            btn.setActionCallback(() -> mainButtonPerformAction(finalIndex));
            if (e.pos.gatePos.equals(Objects.requireNonNull(guiBase.gateTile).getPos()) && e.pos.dimensionID == guiBase.gateTile.world().provider.getDimension()) {
                thisGateEntryIndex = index;
                btn.setEnabled(0, false);
                btn.setEnabled(1, false);
            }

            if(e.notGenerated)
                btn.setEnabled(3, false);

            entriesTextFields.add(field);
            dialButtons.add(btn);
        }

        // Options buttons
        optionButtons.clear();

        // Abort button
        String text = "Abort dialing";
        int width = (10 + guiBase.mc.fontRenderer.getStringWidth(text));
        int y = this.guiTop;
        int x = guiBase.guiRight - OFFSET - 40 - width;
        optionButtons.add(new JSGButton(100, x, y, width, 20, text).setActionCallback(() -> {
            if (guiBase.imaginaryGateTile != null && guiBase.imaginaryGateTile.getStargateState().dialing()) {
                if (guiBase.imaginaryGateTile.abortDialingSequence()) {
                    guiBase.notifer.setText("Dialing aborted.", Notifier.EnumAlertType.INFO, 5);
                    sendPacket(EntryActionEnum.ABORT);
                } else
                    guiBase.notifer.setText("Gate is busy!", Notifier.EnumAlertType.WARNING, 5);
            } else if (guiBase.imaginaryGateTile != null) {
                guiBase.notifer.setText("Gate is not dialing", Notifier.EnumAlertType.WARNING, 5);
            } else {
                guiBase.notifer.setText("Gate is NULL!", Notifier.EnumAlertType.ERROR, 5);
            }
        }));

        // Toggle IRIS
        text = "Toggle iris";
        width = (10 + guiBase.mc.fontRenderer.getStringWidth(text));
        x -= (width + 3);
        optionButtons.add(new JSGButton(101, x, y, width, 20, text).setActionCallback(() -> {
            if (guiBase.imaginaryGateTile != null && guiBase.imaginaryGateTile.hasIris()) {
                if (guiBase.imaginaryGateTile.getIrisState() == EnumIrisState.OPENED || guiBase.imaginaryGateTile.getIrisState() == EnumIrisState.CLOSED) {
                    guiBase.notifer.setText("Toggling iris.", Notifier.EnumAlertType.INFO, 5);
                    sendPacket(EntryActionEnum.TOGGLE_IRIS);
                } else
                    guiBase.notifer.setText("Gate's iris is busy!", Notifier.EnumAlertType.WARNING, 5);
            } else if (guiBase.imaginaryGateTile != null) {
                guiBase.notifer.setText("Gate has no iris!", Notifier.EnumAlertType.WARNING, 5);
            } else {
                guiBase.notifer.setText("Gate is NULL!", Notifier.EnumAlertType.ERROR, 5);
            }
        }));
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

    public void sendPacket(EntryActionEnum action) {
        try {
            JSGPacketHandler.INSTANCE.sendToServer(new EntryActionToServer(action, guiBase.pos));
        } catch (Exception e) {
            JSG.error("Error", e);
        }
    }

    public void sendPacketWithTarget(EntryActionEnum action, StargatePos targetPos) {
        try {
            JSGPacketHandler.INSTANCE.sendToServer(new EntryActionToServer(action, targetPos));
        } catch (Exception e) {
            JSG.error("Error", e);
        }
    }

    public void mainButtonPerformAction(int index) {
        ModeButton btn = dialButtons.get(index);
        if(!btn.isEnabledCurrent()) return;
        StargateEntry entry = entries.get(index);
        StargatePos pos = entry.pos;
        switch(btn.getCurrentState()){
            default:break;
            case 0:
            case 1:
                dialGate(index, btn.getCurrentState() == 1);
                break;
            case 2:
                sendPacketWithTarget(EntryActionEnum.GIVE_NOTEBOOK, pos);
                break;
            case 3:
                sendPacketWithTarget(EntryActionEnum.TELEPORT_TO_POS, pos);
                break;
        }
    }

    public void dialGate(int index, boolean fastDial){
        try {
            EnumHand hand = guiBase.getHand();
            StargateEntry entry = entries.get(index);
            StargatePos pos = entry.pos;
            if (guiBase.gateTile == null || guiBase.imaginaryGateTile == null) {
                guiBase.notifer.setText("Linked gate is NULL!", Notifier.EnumAlertType.ERROR, 5);
                return;
            }
            if (!guiBase.imaginaryGateTile.getStargateState().idle() && !guiBase.imaginaryGateTile.getStargateState().engaged()) {
                guiBase.notifer.setText("Stargate is busy!", Notifier.EnumAlertType.WARNING, 5);
                return;
            }

            if (!guiBase.imaginaryGateTile.getStargateState().engaged())
                guiBase.notifer.setText("Dialing gate " + (pos.getName().equals("") ? DimensionManager.getProviderType(pos.dimensionID).getName() : pos.getName()), Notifier.EnumAlertType.INFO, 5);
            else
                guiBase.notifer.setText("Closing gate...", Notifier.EnumAlertType.INFO, 5);

            int symbolsCount = Objects.requireNonNull(guiBase.gateTile).getMinimalSymbolsToDial(pos.getGateSymbolType(), pos);

            JSGPacketHandler.INSTANCE.sendToServer(new EntryActionToServer(hand, new StargateAddressDynamic(entry.address), symbolsCount, guiBase.gateTile.getPos(), fastDial));
        } catch (Exception e) {
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
        for (ModeButton f : dialButtons) {
            f.y = (scrolled + (f.id * 23)) + OFFSET + guiTop;
        }
    }

    public void renderEntries() {
        updateY();
        for (GuiTextField f : entriesTextFields) {
            if (canNotRenderEntry(f.y)) continue;
            f.drawTextBox();
        }

        boolean shouldBeEnabled = (guiBase.imaginaryGateTile != null && (guiBase.imaginaryGateTile.getStargateState().idle() || guiBase.imaginaryGateTile.getStargateState().engaged()));

        for (ModeButton b : dialButtons) {
            if (canNotRenderEntry(b.y)) continue;
            boolean enabled = shouldBeEnabled && b.id != thisGateEntryIndex;
            b.setEnabled(0, enabled);
            b.setEnabled(1, enabled);
            if(entries.get(b.id).notGenerated)
                b.setEnabled(3, false);
            b.drawButton(guiBase.mouseX, guiBase.mouseY);
        }

        for (JSGButton b : optionButtons) {
            b.drawButton(Minecraft.getMinecraft(), guiBase.mouseX, guiBase.mouseY, guiBase.partialTicks);
        }
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
        for (ModeButton f : dialButtons) {
            if (!canNotRenderEntry(f.y) && f.isEnabledCurrent() && GuiHelper.isPointInRegion(f.x, f.y, f.width, f.height, guiBase.mouseX, guiBase.mouseY)) {
                List<String> lines = new ArrayList<>();
                switch (f.getCurrentState()) {
                    default:
                        break;
                    case 0:
                        lines = Collections.singletonList("Dial this address (slow)");
                        break;
                    case 1:
                        lines = Collections.singletonList("Dial this address (fast)");
                        break;
                    case 2:
                        lines = Collections.singletonList("Get gate's addresses");
                        break;
                    case 3:
                        lines = Collections.singletonList("Teleport to gate's location");
                        break;
                }
                Util.drawHoveringText(lines, guiBase.mouseX, guiBase.mouseY, guiBase.width, guiBase.height, -1, guiBase.mc.fontRenderer);
            }
        }
    }

    public void scroll(int k) {
        if (k == 0) return;
        if (k < 0) k = -1;
        if (k > 0) k = 1;
        if (canContinueScrolling(k)) {
            scrolled += (SCROLL_AMOUNT * k);
        }
    }

    public boolean canContinueScrolling(int k) {
        int top = guiTop;
        int bottom = guiTop + height;
        if (entriesTextFields.size() < 1 && dialButtons.size() < 1) return false;

        boolean isTop = ((entriesTextFields.size() > 0 && entriesTextFields.get(0).getId() < dialButtons.get(0).id) ? entriesTextFields.get(0).y > top : dialButtons.get(0).y > top);
        boolean isBottom = ((entriesTextFields.size() > 0 && entriesTextFields.get(entriesTextFields.size() - 1).getId() >= dialButtons.get(dialButtons.size() - 1).id) ? entriesTextFields.get(entriesTextFields.size() - 1).y < bottom : dialButtons.get(dialButtons.size() - 1).y < bottom);

        return (!isTop && k == 1) || (!isBottom && k == -1);
    }

    public boolean canNotRenderEntry(int y) {
        int top = guiTop;
        int bottom = guiTop + height;
        int height = 23;
        return y < top || (y + height) > bottom;
    }
}
