package tauri.dev.jsg.api.controller;

import net.minecraft.util.math.BlockPos;
import tauri.dev.jsg.power.general.EnergyRequiredToOperate;
import tauri.dev.jsg.power.general.SmallEnergyStorage;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.stargate.StargateOpenResult;
import tauri.dev.jsg.stargate.network.*;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Controller contains safe methods to operate with stargate from your project.
 * It's highly recommended to use only these controllers to operate gates.
 * <p>
 * If something is missing, DM me on discord or on our discord server <a href="https://discord.justsgmod.eu">discord.justsgmod.eu</a>
 */
@SuppressWarnings("unused")
public class StargateAbstractController {
    protected final StargateAbstractBaseTile gateTile;

    protected StargateAbstractController(StargateAbstractBaseTile tile) {
        gateTile = tile;
    }

    /**
     * @return stargate base tile
     */
    public StargateAbstractBaseTile getStargate() {
        return gateTile;
    }

    /**
     * @return center pos of the gate
     */
    public BlockPos getGateCenter() {
        return getStargate().getGateCenterPos();
    }

    /**
     * @param entitiesCount - entities count
     * @param chevronsCount - chevrons to engage (7 - 9)
     * @param delay         - delay before opening in ticks
     */
    public void generateIncomingWormhole(int entitiesCount, int chevronsCount, int delay) {
        getStargate().generateIncoming(entitiesCount, chevronsCount, delay);
    }

    /**
     * @param targetGatePos - target gate pos
     * @return Energy required to operate gate
     */
    public EnergyRequiredToOperate getEnergyRequired(StargatePos targetGatePos) {
        return getStargate().getEnergyRequiredToDialForApi(targetGatePos);
    }

    /**
     * @return if gate is currently unstable
     */
    public boolean isGateUnstable() {
        return getStargate().shouldBeUnstable;
    }

    /**
     * @return current stargate's state
     */
    public EnumStargateState getStargateState() {
        return getStargate().getStargateState();
    }

    /**
     * @return seconds to close (by energy)
     */
    public float getSecondsToClose() {
        return getStargate().getEnergySecondsToClose();
    }

    /**
     * @return set of supported overlays
     */
    public EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
        return getStargate().getSupportedOverlays();
    }

    /**
     * @return gate's open seconds
     */
    public long getOpenedSeconds() {
        return getStargate().getOpenedSeconds();
    }

    /**
     * @return gate's open time as MINUTES:SECONDS
     */
    public String getOpenedMinutesAndSeconds() {
        return getStargate().getOpenedSecondsToDisplayAsMinutes();
    }

    /**
     * @return true if gate is merged
     */
    public boolean isMerged() {
        return getStargate().isMerged();
    }

    /**
     * Unmerge gate - after reloading the world, gate will ignore this option
     */
    public void unMergeGate() {
        getStargate().updateMergeState(false, getStargate().getFacing(), getStargate().getFacingVertical());
    }

    /**
     * Merge gate - after reloading the world, gate will ignore this option
     */
    public void mergeGate() {
        getStargate().updateMergeState(true, getStargate().getFacing(), getStargate().getFacingVertical());
    }

    /**
     * @return energy that has been tranfered last tick
     */
    public int getEnergyTransferedLastTick() {
        return getStargate().getEnergyTransferedLastTick();
    }

    /**
     * @return energy storage of the gate
     */
    public SmallEnergyStorage getEnergyStorage() {
        return getStargate().getEnergyStorageForApi();
    }

    /**
     * @return gate's dialed address
     */
    public StargateAddressDynamic getDialedAddress() {
        return getStargate().getDialedAddress();
    }

    /**
     * @return gate's symbol type/gate type
     */
    public SymbolTypeEnum getSymbolType() {
        return getStargate().getSymbolType();
    }

    /**
     * @param symbolType - symbol type of address to get
     * @return gate address
     */
    public StargateAddress getStargateAddress(SymbolTypeEnum symbolType) {
        return getStargate().getStargateAddress(symbolType);
    }

    /**
     * @param address - address to set
     */
    public void setStargateAddress(StargateAddress address) {
        getStargate().setGateAddress(address.getSymbolType(), address);
    }

    /**
     * @param newName - name to set
     */
    public void renameAddressInNetwork(String newName) {
        getStargate().renameStargatePos(newName);
    }

    /**
     * @return chevron count that gate is capable to dial
     */
    public int getMaxChevrons() {
        return getStargate().getMaxChevronsForApi();
    }

    /**
     * @param symbol - target symbol
     * @return true if gate can add that symbol
     */
    public boolean canAddSymbol(SymbolInterface symbol) {
        if (symbol.getSymbolType() != getStargate().getSymbolType()) return false;
        return getStargate().canAddSymbol(symbol);
    }

    /**
     * @return OK if gate was opened successfully
     */
    public StargateOpenResult openGate() {
        return getStargate().attemptOpenAndFail();
    }

    /**
     * @param reason - reason to close the gate
     */
    public void closeGate(StargateClosedReasonEnum reason) {
        getStargate().attemptClose(reason);
    }

    /**
     * @param address - address to check
     * @return true if gate can dial that address
     */
    public boolean canDialAddress(StargateAddressDynamic address) {
        return getStargate().canDialAddress(address);
    }

    /**
     * @param address - address to check
     * @return OK if address is valid and gate has enough energy to dial that address
     */
    public StargateOpenResult checkAddressAndEnergy(StargateAddressDynamic address) {
        return getStargate().checkAddressAndEnergy(address);
    }

    /**
     * @return if gate is dialing using nox system
     */
    public boolean isNoxDialing() {
        return getStargate().isNoxDialing;
    }


    // -------------------- STATIC --------------------

    /**
     * Get controller for the gate
     *
     * @param gateTile - target gate
     * @return controller
     */
    public static StargateAbstractController getController(@Nonnull StargateAbstractBaseTile gateTile) {
        return new StargateAbstractController(gateTile);
    }

}
