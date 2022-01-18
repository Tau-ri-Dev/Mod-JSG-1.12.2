package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.renderer.transportrings.TransportRingsAncientRenderer;
import mrjake.aunis.state.StateTypeEnum;

public class TransportRingsAncientTile extends TransportRingsAbstractTile {
  // todo(Mine): tings types

    @Override
    public void onLoad(){
        if (world.isRemote) {
            renderer = new TransportRingsAncientRenderer(world, pos, LOCAL_TELEPORT_BOX);
            AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
        }
        if (!world.isRemote) {
            setBarrierBlocks(false, false);

            globalTeleportBox = LOCAL_TELEPORT_BOX.offset(pos);
        }
        super.onLoad();
    }
}
