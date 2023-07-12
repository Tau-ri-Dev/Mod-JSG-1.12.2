package tauri.dev.jsg.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.JSGGuiHandler;

public class GuiOpenToClient extends PositionedPacket {
    public GuiOpenToClient() {
    }

    protected int id;
    protected BlockPos pos;

    public GuiOpenToClient(BlockPos pos, int guiId) {
        super(pos);
        this.id = guiId;
        this.pos = pos;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(id);
        buf.writeLong(pos.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        id = buf.readInt();
        pos = BlockPos.fromLong(buf.readLong());
    }

    public static class GuiOpenToClientHandler implements IMessageHandler<GuiOpenToClient, IMessage> {

        @Override
        public IMessage onMessage(GuiOpenToClient message, MessageContext ctx) {
            EntityPlayer player = JSG.proxy.getPlayerClientSide();
            JSG.proxy.addScheduledTaskClientSide(() -> Minecraft.getMinecraft().displayGuiScreen((GuiScreen) JSGGuiHandler.INSTANCE.getClientGuiElement(message.id, player, player.world, message.pos.getX(), message.pos.getY(), message.pos.getZ())));

            return null;
        }

    }
}
