package tauri.dev.jsg.packet.stargate;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.PositionedPacket;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.stargate.EnumIrisMode;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import java.nio.charset.StandardCharsets;

public class SaveIrisCodeToServer extends PositionedPacket {
    public SaveIrisCodeToServer() {
    }

    String code;
    EnumIrisMode mode;

    public SaveIrisCodeToServer(BlockPos pos, String code, EnumIrisMode mode) {
        super(pos);

        this.code = code;
        this.mode = mode;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        buf.writeInt(code.length());
        buf.writeCharSequence(code, StandardCharsets.UTF_8);
        buf.writeByte(mode.id);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        int codeSize = buf.readInt();
        code = buf.readCharSequence(codeSize, StandardCharsets.UTF_8).toString();
        mode = EnumIrisMode.getValue(buf.readByte());
    }


    public static class SaveIrisCodeToServerHandler implements IMessageHandler<SaveIrisCodeToServer, IMessage> {

        @Override
        public StateUpdatePacketToClient onMessage(SaveIrisCodeToServer message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();

            world.addScheduledTask(() -> {
                StargateClassicBaseTile te;
                if (world.getTileEntity(message.pos) instanceof StargateClassicBaseTile) {
                    te = (StargateClassicBaseTile) world.getTileEntity(message.pos);
                    te.setIrisCode(message.code);
                    te.setIrisMode(message.mode);
                    JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(message.pos, StateTypeEnum.GUI_STATE, te.getState(StateTypeEnum.GUI_STATE)), player);
                }
            });

            return null;
        }

    }
}
