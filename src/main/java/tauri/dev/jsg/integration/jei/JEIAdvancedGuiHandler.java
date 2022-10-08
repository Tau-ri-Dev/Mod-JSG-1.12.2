package tauri.dev.jsg.integration.jei;

import mezz.jei.api.gui.IAdvancedGuiHandler;
import tauri.dev.jsg.gui.element.tabs.TabbedContainerInterface;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.awt.*;
import java.util.List;

public class JEIAdvancedGuiHandler implements IAdvancedGuiHandler<GuiContainer> {

  @Override
  public Class<GuiContainer> getGuiContainerClass() {
    return GuiContainer.class;
  }

  @Override
  public List<Rectangle> getGuiExtraAreas(GuiContainer guiContainer) {
    if (guiContainer instanceof TabbedContainerInterface) {
      return ((TabbedContainerInterface) guiContainer).getGuiExtraAreas();
    }

    return null;
  }
}
