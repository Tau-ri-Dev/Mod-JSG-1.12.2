package mrjake.aunis.renderer.transportrings;

import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.util.AunisAxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TransportRingsOriRenderer extends TransportRingsAbstractRenderer {
    public TransportRingsOriRenderer(World world, BlockPos pos, AunisAxisAlignedBB localTeleportBox) {
        super(world, pos, localTeleportBox);
    }

    @Override
    public void renderRings(float partialTicks, int distance) {
        for (Ring ring : rings)
            ring.render(partialTicks, ElementEnum.RING_ORI, distance); // Rings appear from underground, no frost/moss here.
    }
}
