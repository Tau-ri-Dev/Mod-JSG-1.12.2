package tauri.dev.jsg.api.controller;

import tauri.dev.jsg.stargate.EnumSpinDirection;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Controller contains safe methods to operate with stargate from your project.
 * It's highly recommended to use only these controllers to operate gates.
 * <p>
 * If something is missing, DM me on discord or on our discord server <a href="https://discord.justsgmod.eu">discord.justsgmod.eu</a>
 */
@SuppressWarnings("unused")
public class StargateMilkyWayController extends StargateClassicController {
    protected StargateMilkyWayController(StargateMilkyWayBaseTile tile) {
        super(tile);
    }

    /**
     * Returns the gate base tile.
     *
     * @return stargate base tile
     */
    @Override
    public StargateMilkyWayBaseTile getStargate() {
        return (StargateMilkyWayBaseTile) super.getStargate();
    }

    /**
     * Spin gate's ring x rounds (1 round = 360°)
     * @param rounds - amount of rounds
     * @param changeStateToDialing - set to true if gate should change its state to DIALING_COMPUTER
     * @return true if gate's ring begin spinning
     */
    public boolean spinGate(int rounds, boolean changeStateToDialing){
        if(!getStargate().getStargateState().idle()) return false;
        getStargate().spinRing(rounds, changeStateToDialing, false, -1);
        return true;
    }

    /**
     * Spin gate's ring for x ticks
     * @param spinDirection - set to null for CLOCKWISE spinning
     * @param changeStateToDialing - set to true if gate should change its state to DIALING_COMPUTER
     * @param timeToSpin - time in ticks for what ring should spin
     * @return true if gate's ring begin spinning
     */
    public boolean spinGate(@Nullable EnumSpinDirection spinDirection, boolean changeStateToDialing, int timeToSpin){
        if(!getStargate().getStargateState().idle()) return false;
        getStargate().spinRing(((spinDirection == null || spinDirection == EnumSpinDirection.CLOCKWISE) ? 1 : -1), changeStateToDialing, true, timeToSpin);
        return true;
    }


    // -------------------- STATIC --------------------

    /**
     * Get controller for the gate
     *
     * @param gateTile - target gate
     * @return controller
     */
    public static StargateMilkyWayController getController(@Nonnull StargateMilkyWayBaseTile gateTile) {
        return new StargateMilkyWayController(gateTile);
    }
}
