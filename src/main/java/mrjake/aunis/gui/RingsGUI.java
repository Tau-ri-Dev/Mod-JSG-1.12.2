package mrjake.aunis.gui;

import mrjake.aunis.Aunis;
import mrjake.aunis.gui.base.AunisGuiBase;
import mrjake.aunis.gui.base.AunisGuiButton;
import mrjake.aunis.gui.element.GuiHelper;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.transportrings.SaveRingsParametersToServer;
import mrjake.aunis.state.transportrings.TransportRingsGuiState;
import mrjake.aunis.transportrings.SymbolTransportRingsEnum;
import mrjake.aunis.transportrings.TransportRings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RingsGUI extends AunisGuiBase {

    private final List<GuiTextField> textFields = new ArrayList<>();
    public TransportRingsGuiState state;
    private BlockPos pos;
    private GuiTextField nameTextField;
    private GuiTextField distanceTextField;
    private AunisGuiButton saveButton;

    public RingsGUI(BlockPos pos, TransportRingsGuiState state) {
        super(196, 160, 8, FRAME_COLOR, BG_COLOR, TEXT_COLOR, 4);

        this.pos = pos;
        this.state = state;
    }

    @Override
    public void initGui() {
        textFields.clear();
        super.initGui();
        setBackGroundSize(Math.max(width / 3, 210), Math.max(height - 100, 160));
        int y = 55;

        nameTextField = createTextField(50, y, imageWidth / 2 + 10, 16, state.getName());
        textFields.add(nameTextField);
        y += 15;

        distanceTextField = createTextField(50, y, imageWidth / 2 + 10, 3, state.getDistance() + "");
        textFields.add(distanceTextField);

        saveButton = new AunisGuiButton(id++, imageWidth / 2 - Math.max(imageWidth / 4 * 3, 70) / 2, imageHeight - 25 - padding, Math.max(imageWidth / 4 * 3, 70), 20, Aunis.proxy.localize("tile.aunis.transportrings_block.rings_save"));
        buttonList.add(saveButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mouseX -= getTopLeftAbsolute(false);
        mouseY -= getTopLeftAbsolute(true);

        GlStateManager.pushMatrix();
        translateToCenter();
        drawBackground();

        if (!state.isInGrid())
            drawVerticallCenteredString(Aunis.proxy.localize("tile.aunis.transportrings_block.rings_not_in_grid"), 0, 0, 0xB36262);

        drawString(Aunis.proxy.localize("tile.aunis.transportrings_block.rings_address") + ": ", 0, 20, 0x00AA00);

        drawString(Aunis.proxy.localize("tile.aunis.transportrings_block.rings_name") + ": ", 0, 55, 0x00AAAA);
        drawString(Aunis.proxy.localize("tile.aunis.transportrings_block.rings_distance") + ": ", 0, 70, 0xF9801D);

        int shadow = 2;
        float color = 1.0f;
        int space = 34;
        for (int i = 0; i < state.getAddress().size(); i++) {
            SymbolTransportRingsEnum symbol = state.getAddress().get(i);
            if (symbol.origin()) continue;
            Minecraft.getMinecraft().getTextureManager().bindTexture(symbol.getIconResource());
            GuiHelper.drawTexturedRectWithShadow(60 + 20 / 2 + space * i, 25, shadow, shadow, 20, 20, color);
            drawCenteredString(fontRenderer, symbol.getEnglishName(), 60 + 20 + space * i, 50, 0xFFFFFF);
        }

        for (GuiTextField tf : textFields)
            drawTextBox(tf);

        int y = 95;
        int maxY = imageHeight - 25*2 - padding;
        drawString(Aunis.proxy.localize("tile.aunis.transportrings_block.rings_address_list") + ": ", 0, y - 10, 0xFC6767);
        for (TransportRings rings : state.getRings()) {
            if(y > maxY) break; // prevents from rendering out of gui / under save button
            drawString(rings.getName(), 5, y, 0x00AAAA);
            drawString("" + rings.getAddress(), 50, y, 0x00AA00);

            y += 12;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();


    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == saveButton) {
            EntityPlayer player = Minecraft.getMinecraft().player;

            try {
                String name = nameTextField.getText();
                try {
                    int distance = Integer.parseInt(distanceTextField.getText());

                    if (distance >= -40 && distance <= 40) {
                        AunisPacketHandler.INSTANCE.sendToServer(new SaveRingsParametersToServer(pos, name, distance));
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

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        for (GuiTextField tf : textFields)
            tf.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        for (GuiTextField tf : textFields)
            tf.updateCursorCounter();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        mouseX -= getTopLeftAbsolute(false);
        mouseY -= getTopLeftAbsolute(true);

        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (GuiTextField tf : textFields)
            tf.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
