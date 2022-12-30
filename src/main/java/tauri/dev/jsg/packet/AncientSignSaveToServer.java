package tauri.dev.jsg.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.props.AncientSignTile;

import java.nio.charset.StandardCharsets;

public class AncientSignSaveToServer extends PositionedPacket {
    public AncientSignSaveToServer() {
    }

    String[] lines = AncientSignTile.getNewLines();
    final int size = AncientSignTile.LINES;

    public AncientSignSaveToServer(BlockPos pos, String[] lines) {
        super(pos);
        this.lines = lines;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        for(int i = 0; i < size; i++){
            buf.writeInt(lines[i].length());
            buf.writeCharSequence(lines[i], StandardCharsets.UTF_8);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        for(int i = 0; i < size; i++){
            int len = buf.readInt();
            lines[i] = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
        }
    }


    public static class AncientSignSaveToServerHandler implements IMessageHandler<AncientSignSaveToServer, IMessage> {

        @Override
        public StateUpdatePacketToClient onMessage(AncientSignSaveToServer message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();

            world.addScheduledTask(() -> {
                AncientSignTile te;
                if (world.getTileEntity(message.pos) instanceof AncientSignTile) {
                    te = (AncientSignTile) world.getTileEntity(message.pos);
                    if (te != null) {
                        te.ancientText = message.lines;
                        te.markDirty();
                        te.sendState(StateTypeEnum.RENDERER_UPDATE, te.getState(StateTypeEnum.RENDERER_UPDATE));
                    }
                }
            });

            return null;
        }

    }
}
