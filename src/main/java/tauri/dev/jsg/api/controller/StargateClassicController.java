package tauri.dev.jsg.api.controller;

import tauri.dev.jsg.config.ingame.JSGConfigOption;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.stargate.EnumDialingType;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;

/**
 * Controller contains safe methods to operate with stargate from your project.
 * It's highly recommended to use only these controllers to operate gates.
 * <p>
 * If something is missing, DM me on discord or on our discord server <a href="https://discord.justsgmod.eu">discord.justsgmod.eu</a>
 */
@SuppressWarnings("unused")
public class StargateClassicController extends StargateAbstractController {
    protected StargateClassicController(StargateClassicBaseTile tile) {
        super(tile);
    }

    /**
     * Returns the gate base tile.
     *
     * @return stargate base tile
     */
    @Override
    public StargateClassicBaseTile getStargate() {
        return (StargateClassicBaseTile) super.getStargate();
    }

    /**
     * Dial specified address
     *
     * @param address     - address to dial
     * @param noEnergy    - should dial without energy?
     * @param dialingType - type of dialing
     */
    public void dialAddress(StargateAddressDynamic address, boolean noEnergy, EnumDialingType dialingType) {
        getStargate().dialAddress(address, address.getSize(), noEnergy, dialingType);
    }

    /**
     * @return iris temperature in kelvins
     */
    public double getMaxIrisHeat() {
        return getStargate().getMaxIrisHeat();
    }

    /**
     * @return gate temperature in kelvins
     */
    public double getMaxGateHeat() {
        return getStargate().getMaxGateHeat();
    }

    /**
     * @param intensity - intensity of heating up
     */
    public void heatUpGate(double intensity) {
        getStargate().tryHeatUp(intensity);
    }

    /**
     * @param intensity - intensity of heating up
     */
    public void heatUpIris(double intensity) {
        getStargate().tryHeatUp(false, intensity);
    }

    /**
     * @return temperature around gate in kelvins
     */
    public double getTemperatureAroundGate() {
        return getStargate().getTemperatureAroundGate();
    }

    /**
     * @return size of the gate
     */
    public StargateSizeEnum getGateSize() {
        return getStargate().getStargateSizeForApi();
    }

    /**
     * @return if the gate is buried
     */
    public boolean isGateBuried() {
        return getStargate().isGateBuried();
    }

    /**
     * @return true if aborting was successful
     */
    public boolean attemptAbortDialing() {
        return getStargate().abortDialingSequence();
    }

    /**
     * @return if gate is dialing without using energy
     */
    public boolean isDialingWithoutEnergy() {
        return getStargate().isDialingWithoutEnergy();
    }

    /**
     * Adds symbol to address. Requires gate to be IDLE! This method requires to have own scheduled tasks to add symbols one by one only when gate is idle!<br/>
     * <b>If you don't want to use own scheduled tasks, you can use {@link #dialAddress(StargateAddressDynamic, boolean, EnumDialingType)} method to dial whole address.</b>
     *
     * @param useDHD - should use DHD to dial
     * @param symbol - symbol to dial
     */
    public boolean addSymbolToAddress(boolean useDHD, SymbolInterface symbol) {
        if (!getStargate().getStargateState().idle()) return false;
        if (useDHD && getStargate().getSymbolType() != SymbolTypeEnum.UNIVERSE) {
            getStargate().addSymbolToAddressDHD(symbol);
        } else {
            getStargate().addSymbolToAddressManual(symbol, null);
        }
        return true;
    }

    /**
     * Gets config of the gate. To apply changes correctly, use {@link #saveGateConfig(JSGTileEntityConfig)}
     *
     * @return gate's config
     */
    public JSGTileEntityConfig getGateConfig() {
        return getStargate().getConfig();
    }

    /**
     * Gets option from gate config (not need to be saved after change)
     *
     * @return gate's config option
     */
    public JSGConfigOption getGateConfigOption(String name) {
        return getStargate().getConfig().getOption(StargateClassicBaseTile.ConfigOptions.valueOf(name.toUpperCase()).id);
    }

    /**
     * Saves gate's config and update its visual parts on clients
     *
     * @param config - config to save
     */
    public void saveGateConfig(JSGTileEntityConfig config) {
        getStargate().setConfigAndUpdate(config);
    }

    /**
     * @return amount of capacitors supported by gate
     */
    public int getSupportedCapacitors() {
        return getStargate().getSupportedCapacitors();
    }

    /**
     * Opens iris of the gate
     *
     * @return true if success
     */
    public boolean openIris() {
        if (getStargate().isIrisOpened()) return false;
        getStargate().toggleIris();
        return true;
    }

    /**
     * Opens iris of the target gate
     * <p>
     * WARNING!
     * When executed, loads world in what is target gate
     *
     * @return true if success
     */
    public boolean openTargetIris() {
        StargateAbstractBaseTile t = getTargetGateTile();
        if (t == null) return false;
        if (!(t instanceof StargateClassicBaseTile)) return false;
        StargateClassicBaseTile c = (StargateClassicBaseTile) t;
        if (c.isIrisOpened()) return false;
        c.toggleIris();
        return true;
    }

    /**
     * Closes iris of the gate
     *
     * @return true if success
     */
    public boolean closeIris() {
        if (getStargate().isIrisClosed()) return false;
        getStargate().toggleIris();
        return true;
    }

    /**
     * Closes iris of the target gate
     * <p>
     * WARNING!
     * When executed, loads world in what is target gate
     *
     * @return true if success
     */
    public boolean closeTargetIris() {
        StargateAbstractBaseTile t = getTargetGateTile();
        if (t == null) return false;
        if (!(t instanceof StargateClassicBaseTile)) return false;
        StargateClassicBaseTile c = (StargateClassicBaseTile) t;
        if (c.isIrisClosed()) return false;
        c.toggleIris();
        return true;
    }


    // -------------------- STATIC --------------------

    /**
     * Get controller for the gate
     *
     * @param gateTile - target gate
     * @return controller
     */
    public static StargateClassicController getController(@Nonnull StargateClassicBaseTile gateTile) {
        return new StargateClassicController(gateTile);
    }
}
