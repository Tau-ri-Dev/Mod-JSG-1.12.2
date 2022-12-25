package tauri.dev.jsg.gui.container.dhd;

public class DHDPegasusContainerGui extends DHDAbstractContainerGui {

    public DHDPegasusContainerGui(DHDAbstractContainer container) {
        super(container);
    }

    @Override
    public void drawCrystal() {
        drawTexturedModalRect(guiLeft + 77, guiTop + 21, 201, 0, 24, 32);
    }

    @Override
    public void drawAncientTitle() {
        drawTexturedModalRect(guiLeft + 136, guiTop + 4, 177, 96, 211 - 176, 103 - 95);
    }
}
