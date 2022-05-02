package mrjake.aunis.config.ingame;

import mrjake.aunis.state.State;
import mrjake.aunis.state.StateTypeEnum;

/**
 * Specifies that TileEntity is configuratable trough GUI
 */
public interface ITileConfig {
    AunisTileEntityConfig getConfig();
    void initConfig();
    void setConfig(AunisTileEntityConfig config);
    State getState(StateTypeEnum stateType);
}
