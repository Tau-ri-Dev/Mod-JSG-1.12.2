package tauri.dev.jsg.gui.admincontroller;

import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

import java.util.Map;

public class StargateEntry {
    public StargatePos pos;
    public StargateAddress address;

    public Map<SymbolTypeEnum, StargateAddress> addresses;

    public boolean notGenerated = false;
    public String defaultName = "";
}
