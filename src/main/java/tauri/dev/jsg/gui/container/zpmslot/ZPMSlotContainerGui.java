package tauri.dev.jsg.gui.container.zpmslot;

import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.container.zpmhub.ZPMHubContainer;
import tauri.dev.jsg.gui.container.zpmhub.ZPMHubContainerGui;

public class ZPMSlotContainerGui extends ZPMHubContainerGui {
    public ZPMSlotContainerGui(ZPMHubContainer container) {
        super(container);
    }

    @Override
    public ResourceLocation getBackground(){
        return new ResourceLocation(JSG.MOD_ID, "textures/gui/container_zpmslot.png");
    }

    @Override
    public String getGuiUnlocalizedName(){
        return "tile.jsg.zpm_slot_block.name";
    }
}
