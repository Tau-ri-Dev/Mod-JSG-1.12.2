package tauri.dev.jsg.tileentity.transportrings;

import net.minecraft.entity.player.EntityPlayerMP;
import tauri.dev.jsg.advancements.JSGAdvancements;
import tauri.dev.jsg.block.transportrings.TRControllerAbstractBlock;

import static tauri.dev.jsg.block.JSGBlocks.TR_CONTROLLER_GOAULD_BLOCK;

public class TransportRingsAncientTile extends TransportRingsAbstractTile {
    @Override
    public int getDefaultCapacitors() {
        return 2;
    }

    @Override
    public void triggerTeleportAdvancement(EntityPlayerMP playerIn) {
        JSGAdvancements.TR_ANCIENT.trigger(playerIn);
    }

    @Override
    public TRControllerAbstractBlock getControllerBlock(){
        //todo(Mine): When ancient controller finished, switch this to it
        return TR_CONTROLLER_GOAULD_BLOCK;
    }
}
