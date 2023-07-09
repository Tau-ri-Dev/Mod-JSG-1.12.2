package tauri.dev.jsg.worldgen.util;

import tauri.dev.jsg.stargate.network.StargateAddress;

public class GeneratedStargate {
    public final StargateAddress address;
    public final String biomePath;
    public final boolean hasUpgrade;
    public final int originId;

    public GeneratedStargate(StargateAddress address, String biomePath, boolean upgrade, int originId) {
        this.address = address;
        this.biomePath = biomePath;
        this.hasUpgrade = upgrade;
        this.originId = originId;
    }
}
