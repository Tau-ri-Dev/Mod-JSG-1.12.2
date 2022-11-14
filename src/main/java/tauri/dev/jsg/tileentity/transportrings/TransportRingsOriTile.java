package tauri.dev.jsg.tileentity.transportrings;

import tauri.dev.jsg.block.transportrings.TRControllerAbstractBlock;

import static tauri.dev.jsg.block.JSGBlocks.TR_CONTROLLER_GOAULD_BLOCK;

public class TransportRingsOriTile extends TransportRingsAbstractTile {

    @Override
    public int getSupportedCapacitors() {
        return 3;
    }

    @Override
    public TRControllerAbstractBlock getControllerBlock() {
        //todo(Mine): When ori controller finished, switch this to it
        return TR_CONTROLLER_GOAULD_BLOCK;
    }
}
