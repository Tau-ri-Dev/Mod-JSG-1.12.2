package tauri.dev.jsg.tileentity.transportrings;

import net.minecraft.entity.player.EntityPlayerMP;
import tauri.dev.jsg.advancements.JSGAdvancements;
import tauri.dev.jsg.block.transportrings.TRControllerAbstractBlock;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;

import static tauri.dev.jsg.block.JSGBlocks.TR_CONTROLLER_GOAULD_BLOCK;

public class TransportRingsGoauldTile extends TransportRingsAbstractTile {
    @Override
    public int getDefaultCapacitors() {
        return 2;
    }

    @Override
    public void triggerTeleportAdvancement(EntityPlayerMP playerIn) {
        JSGAdvancements.TR_GOAULD.trigger(playerIn);
    }

    @Override
    protected SymbolTypeTransportRingsEnum getSymbolTypeByThis() {
        return SymbolTypeTransportRingsEnum.GOAULD;
    }

    @Override
    public TRControllerAbstractBlock getControllerBlock(){
        return TR_CONTROLLER_GOAULD_BLOCK;
    }
}
