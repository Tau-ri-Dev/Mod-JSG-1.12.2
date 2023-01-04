package tauri.dev.jsg.renderer.transportrings;

import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.state.transportrings.TransportRingsRendererState;

public class TransportRingsAncientRenderer extends TransportRingsAbstractRenderer {
    @Override
    public void renderRings(TransportRingsRendererState state, float partialTicks, int distance, float addToYMax) {
        for (Ring ring : state.rings)
            ring.render(partialTicks, ElementEnum.RING_ANCIENT, distance, addToYMax); // Rings appear from underground, no frost/moss here.
    }
}
