package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.renderer.transportrings.TransportRingsAbstractRenderer;
import mrjake.aunis.renderer.transportrings.TransportRingsGoauldRenderer;

public class TransportRingsGoauldTile extends TransportRingsAbstractTile {
    @Override
    public TransportRingsAbstractRenderer getNewRenderer() {
        return new TransportRingsGoauldRenderer(world, pos, LOCAL_TELEPORT_BOX);
    }

    @Override
    public int getSupportedCapacitors() {
        return 3;
    }
}
