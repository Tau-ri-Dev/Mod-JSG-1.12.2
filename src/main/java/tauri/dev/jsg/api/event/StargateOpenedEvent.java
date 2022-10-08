package tauri.dev.jsg.api.event;

import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

/**
 * Event that posted when stargate is fully opened
 */
public final class StargateOpenedEvent extends StargateConnectedAbstractEvent {

    public StargateOpenedEvent(StargateAbstractBaseTile tile, StargateAbstractBaseTile targetTile, boolean initiating) {
        super(tile, targetTile, initiating);
    }


}
