package tauri.dev.jsg.tileentity.transportrings;

import tauri.dev.jsg.block.transportrings.TRControllerAbstractBlock;

import static tauri.dev.jsg.block.JSGBlocks.TR_CONTROLLER_GOAULD_BLOCK;

public class TransportRingsGoauldTile extends TransportRingsAbstractTile {
    @Override
    public int getSupportedCapacitors() {
        return 2;
    }

    @Override
    public TRControllerAbstractBlock getControllerBlock(){
        return TR_CONTROLLER_GOAULD_BLOCK;
    }
}
