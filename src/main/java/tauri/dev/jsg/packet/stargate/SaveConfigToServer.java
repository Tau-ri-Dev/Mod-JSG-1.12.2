package tauri.dev.jsg.packet.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.config.ingame.ITileConfig;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.PositionedPacket;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.state.StateTypeEnum;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SaveConfigToServer extends PositionedPacket {
    public SaveConfigToServer() {
    }

    JSGTileEntityConfig config = new JSGTileEntityConfig();

    public SaveConfigToServer(BlockPos pos, JSGTileEntityConfig config) {
        super(pos);
        this.config = config;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        config.toBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        config.fromBytes(buf);
    }


    public static class SaveConfigToServerHandler implements IMessageHandler<SaveConfigToServer, IMessage> {

        @Override
        public StateUpdatePacketToClient onMessage(SaveConfigToServer message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();

            world.addScheduledTask(() -> {
                ITileConfig te;
                if (world.getTileEntity(message.pos) instanceof ITileConfig) {
                    te = (ITileConfig) world.getTileEntity(message.pos);
                    if(te != null) {
                        te.setConfig(message.config);
                        JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(message.pos, StateTypeEnum.GUI_STATE, te.getState(StateTypeEnum.GUI_STATE)), player);
                    }
                }
            });

            return null;
        }

    }
}
