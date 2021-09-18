package mrjake.aunis.integration.jei;

import mezz.jei.api.gui.IAdvancedGuiHandler;
import mrjake.aunis.gui.element.TabbedContainerInterface;
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
