package mrjake.aunis.packet.stargate;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.config.ingame.AunisTileEntityConfig;
import mrjake.aunis.config.ingame.ITileConfig;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.PositionedPacket;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.state.StateTypeEnum;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SaveConfigToServer extends PositionedPacket {
    public SaveConfigToServer() {
    }

    AunisTileEntityConfig config = new AunisTileEntityConfig();

    public SaveConfigToServer(BlockPos pos, AunisTileEntityConfig config) {
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
                        AunisPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(message.pos, StateTypeEnum.GUI_STATE, te.getState(StateTypeEnum.GUI_STATE)), player);
                    }
                }
            });

            return null;
        }

    }
}
