package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.renderer.transportrings.TransportRingsAbstractRenderer;
import mrjake.aunis.renderer.transportrings.TransportRingsAncientRenderer;

public class TransportRingsAncientTile extends TransportRingsAbstractTile {
    @Override
    public TransportRingsAbstractRenderer getNewRenderer(){
        return new TransportRingsAncientRenderer(world, pos, LOCAL_TELEPORT_BOX);
    }
}
