package tauri.dev.jsg.config.ingame;

import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;

/**
 * Specifies that TileEntity is configuratable trough GUI
 */
public interface ITileConfig {
    JSGTileEntityConfig getConfig();
    void initConfig();
    void setConfig(JSGTileEntityConfig config);
    void setConfigAndUpdate(JSGTileEntityConfig config);
    State getState(StateTypeEnum stateType);
}
