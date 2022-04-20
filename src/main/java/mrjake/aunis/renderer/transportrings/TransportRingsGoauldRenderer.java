package mrjake.aunis.renderer.transportrings;

import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.state.transportrings.TransportRingsRendererState;
import mrjake.aunis.util.AunisAxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TransportRingsGoauldRenderer extends TransportRingsAbstractRenderer {
    @Override
    public void renderRings(TransportRingsRendererState state, float partialTicks, int distance) {
        for (Ring ring : state.rings)
            ring.render(partialTicks, ElementEnum.RING_BLACK, distance); // Rings appear from underground, no frost/moss here.
    }
}
