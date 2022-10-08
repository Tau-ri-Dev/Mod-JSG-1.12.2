package tauri.dev.jsg.api.event;

import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

/**
 * Event that posted when stargate is fully closed
 */
public final class StargateClosedEvent extends StargateAbstractEvent {
    public StargateClosedEvent(StargateAbstractBaseTile tile) {
        super(tile);
    }

}
