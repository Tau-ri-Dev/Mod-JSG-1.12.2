package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.renderer.transportrings.TransportRingsAbstractRenderer;
import mrjake.aunis.renderer.transportrings.TransportRingsOriRenderer;

public class TransportRingsOriTile extends TransportRingsAbstractTile {
    @Override
    public TransportRingsAbstractRenderer getNewRenderer(){
        return new TransportRingsOriRenderer(world, pos, LOCAL_TELEPORT_BOX);
    }

    @Override
    public int getSupportedCapacitors() {
        return 3;
    }
}
