package tauri.dev.jsg.worldgen.structures.stargate;

import tauri.dev.jsg.stargate.network.StargateAddress;

public class GeneratedStargate {
    public StargateAddress address;
    public String path;
    public boolean hasUpgrade;

    public GeneratedStargate(StargateAddress address, String path, boolean upgrade) {
        this.address = address;
        this.path = path;
        this.hasUpgrade = upgrade;
    }
}
