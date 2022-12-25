package tauri.dev.jsg.gui.container.dhd;

public class DHDMilkyWayContainerGui extends DHDAbstractContainerGui {

    public DHDMilkyWayContainerGui(DHDAbstractContainer container) {
        super(container);
    }

    @Override
    public void drawCrystal() {
        drawTexturedModalRect(guiLeft + 77, guiTop + 21, 176, 0, 24, 32);
    }

    @Override
    public void drawAncientTitle() {
        drawTexturedModalRect(guiLeft + 136, guiTop + 4, 177, 87, 211 - 176, 94 - 86);
    }
}
