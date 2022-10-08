package tauri.dev.jsg.api.event;

import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Event that posted on chevron lock
 * This event is cancelable, you can cancel it and chevron will not lock
 */
@Cancelable
public final class StargateChevronEngagedEvent extends StargateAbstractEvent {
    private final SymbolInterface symbol;
    private final boolean lastSymbol;

    public StargateChevronEngagedEvent(StargateAbstractBaseTile tile, SymbolInterface symbol, boolean lastSymbol) {
        super(tile);
        this.symbol = symbol;
        this.lastSymbol = lastSymbol;
    }

    public SymbolInterface getSymbol() {
        return symbol;
    }

    public boolean isLastSymbol() {
        return lastSymbol;
    }
}
