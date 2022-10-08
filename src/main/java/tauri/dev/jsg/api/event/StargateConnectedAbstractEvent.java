package tauri.dev.jsg.api.event;

import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

/**
 * Parent event for all stargate events with connected stargates
 */
public abstract class StargateConnectedAbstractEvent extends StargateAbstractEvent {
    private final StargateAbstractBaseTile targetTile;
    private final boolean initiating;

    public StargateConnectedAbstractEvent(StargateAbstractBaseTile tile, StargateAbstractBaseTile targetTile, boolean initiating) {
        super(tile);
        this.targetTile = targetTile;
        this.initiating = initiating;
    }

    /**
     * Get target stargate
     * @return target stargate tileentity
     */
    public StargateAbstractBaseTile getTargetTile() {
        return targetTile;
    }

    /**
     * Get address of target stargate
     * @return stargate address by stargate type
     */
    public StargateAddress getTargetAddress(){
        return targetTile.getStargateAddress(targetTile.getSymbolType());
    }

    /**
     * Get address of target stargate
     * @param type address type
     * @return stargate address
     */
    public StargateAddress getTargetAddress(SymbolTypeEnum type){
        return targetTile.getStargateAddress(type);
    }

    /**
     * Is {@link #getTile()} initiating stargate or not
     * @return true if yes, false if no
     */
    public boolean isInitiating() {
        return initiating;
    }
}
