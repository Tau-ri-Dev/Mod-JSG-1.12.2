package tauri.dev.jsg.stargate;

import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

public class NearbyGate {
    public StargateAddress address;
    public int symbolsNeeded;
    public SymbolTypeEnum gateType;

    public NearbyGate(StargateAddress address, int symbolsNeeded, SymbolTypeEnum gateType) {
        this.address = address;
        this.symbolsNeeded = symbolsNeeded;
        this.gateType = gateType;
    }
}
