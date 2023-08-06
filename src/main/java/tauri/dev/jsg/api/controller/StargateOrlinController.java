package tauri.dev.jsg.api.controller;

import tauri.dev.jsg.tileentity.stargate.StargateOrlinBaseTile;

import javax.annotation.Nonnull;

/**
 * Controller contains safe methods to operate with stargate from your project.
 * It's highly recommended to use only these controllers to operate gates.
 * <p>
 * If something is missing, DM me on discord or on our discord server <a href="https://discord.justsgmod.eu">discord.justsgmod.eu</a>
 */
@SuppressWarnings("unused")
public class StargateOrlinController extends StargateAbstractController {
    protected StargateOrlinController(StargateOrlinBaseTile tile) {
        super(tile);
    }

    /**
     * Returns the gate base tile.
     *
     * @return stargate base tile
     */
    @Override
    public StargateOrlinBaseTile getStargate() {
        return (StargateOrlinBaseTile) super.getStargate();
    }


    // -------------------- STATIC --------------------

    /**
     * Get controller for the gate
     *
     * @param gateTile - target gate
     * @return controller
     */
    public static StargateOrlinController getController(@Nonnull StargateOrlinBaseTile gateTile) {
        return new StargateOrlinController(gateTile);
    }
}
