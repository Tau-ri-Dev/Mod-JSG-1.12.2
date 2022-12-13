package tauri.dev.jsg.worldgen.util;

import tauri.dev.jsg.stargate.network.StargateAddress;

public class GeneratedStargate {
    public final StargateAddress address;
    public final String path;
    public final boolean hasUpgrade;
    public final int originId;

    public GeneratedStargate(StargateAddress address, String path, boolean upgrade, int originId) {
        this.address = address;
        this.path = path;
        this.hasUpgrade = upgrade;
        this.originId = originId;
    }
}
