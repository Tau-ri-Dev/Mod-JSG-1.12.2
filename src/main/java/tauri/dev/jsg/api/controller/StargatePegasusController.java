package tauri.dev.jsg.api.controller;

import tauri.dev.jsg.tileentity.stargate.StargatePegasusBaseTile;

import javax.annotation.Nonnull;

/**
 * Controller contains safe methods to operate with stargate from your project.
 * It's highly recommended to use only these controllers to operate gates.
 * <p>
 * If something is missing, DM me on discord or on our discord server <a href="https://discord.justsgmod.eu">discord.justsgmod.eu</a>
 */
@SuppressWarnings("unused")
public class StargatePegasusController extends StargateClassicController {
    protected StargatePegasusController(StargatePegasusBaseTile tile) {
        super(tile);
    }

    /**
     * Returns the gate base tile.
     *
     * @return stargate base tile
     */
    @Override
    public StargatePegasusBaseTile getStargate() {
        return (StargatePegasusBaseTile) super.getStargate();
    }


    // -------------------- STATIC --------------------

    /**
     * Get controller for the gate
     *
     * @param gateTile - target gate
     * @return controller
     */
    public static StargatePegasusController getController(@Nonnull StargatePegasusBaseTile gateTile) {
        return new StargatePegasusController(gateTile);
    }
}
