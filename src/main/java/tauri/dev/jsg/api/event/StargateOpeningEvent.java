package tauri.dev.jsg.api.event;

import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Event that posted when stargate is opening
 * This event is cancelable. You can cancel it and stargate will not open
 */
@Cancelable
public final class StargateOpeningEvent extends StargateConnectedAbstractEvent {

    public StargateOpeningEvent(StargateAbstractBaseTile tile, StargateAbstractBaseTile targetTile, boolean initiating) {
        super(tile, targetTile, initiating);
    }


}
