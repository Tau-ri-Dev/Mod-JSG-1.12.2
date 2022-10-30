package tauri.dev.jsg.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.energy.ZPMHubTile;

public class ZPMHubAnimationToServer extends PositionedPacket {
    public ZPMHubAnimationToServer() {
    }

    public ZPMHubAnimationToServer(BlockPos pos) {
        super(pos);
    }


    public static class ZPMHubAnimationToServerHandler implements IMessageHandler<ZPMHubAnimationToServer, IMessage> {

        @Override
        public StateUpdatePacketToClient onMessage(ZPMHubAnimationToServer message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();

            world.addScheduledTask(() -> {
                ZPMHubTile te;
                if (world.getTileEntity(message.pos) instanceof ZPMHubTile) {
                    te = (ZPMHubTile) world.getTileEntity(message.pos);
                    if (te != null) {
                        te.startAnimation();
                        JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(message.pos, StateTypeEnum.RENDERER_UPDATE, te.getState(StateTypeEnum.RENDERER_UPDATE)), player);
                    }
                }
            });

            return null;
        }

    }
}
