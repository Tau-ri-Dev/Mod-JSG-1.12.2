package tauri.dev.jsg.api.event;

import tauri.dev.jsg.stargate.StargateOpenResult;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

/**
 * Event that posted when stargate dial failed
 */
public final class StargateDialFailEvent extends StargateAbstractEvent {
    private final StargateOpenResult reason;

    public StargateDialFailEvent(StargateAbstractBaseTile tile, StargateOpenResult result) {
        super(tile);
        this.reason = result;
    }

    public StargateOpenResult getReason(){
        return reason;
    }
}
