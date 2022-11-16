package tauri.dev.jsg.tileentity.energy;

import tauri.dev.jsg.util.JSGAdvancementsUtil;

import static tauri.dev.jsg.util.JSGAdvancementsUtil.tryTriggerRangedAdvancement;

public class ZPMSlotTile extends ZPMHubTile {

    @Override
    public void triggerAdvancement(){
        if(itemStackHandler.getStackInSlot(0).isEmpty()) return;
        tryTriggerRangedAdvancement(this, JSGAdvancementsUtil.EnumAdvancementType.ZPM_SLOT);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public int getAnimationLength(){
        return (int) Math.round(super.getAnimationLength()*0.75);
    }
}
