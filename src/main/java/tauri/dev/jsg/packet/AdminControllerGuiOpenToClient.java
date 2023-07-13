package tauri.dev.jsg.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.admincontroller.GuiAdminController;
import tauri.dev.jsg.stargate.network.StargateNetwork;

public class AdminControllerGuiOpenToClient extends PositionedPacket {
    public AdminControllerGuiOpenToClient() {
    }

    protected BlockPos pos;
    protected StargateNetwork network = null;

    public AdminControllerGuiOpenToClient(BlockPos pos, StargateNetwork network) {
        super(pos);
        this.pos = pos;
        this.network = network;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeLong(pos.toLong());
        if (network != null) {
            buf.writeBoolean(true);
            network.toBytes(buf);
        } else buf.writeBoolean(false);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        pos = BlockPos.fromLong(buf.readLong());
        if (buf.readBoolean()) {
            network = new StargateNetwork();
            network.fromBytes(buf);
        }
    }

    public static class AdminControllerGuiOpenToClientHandler implements IMessageHandler<AdminControllerGuiOpenToClient, IMessage> {

        @Override
        public IMessage onMessage(AdminControllerGuiOpenToClient message, MessageContext ctx) {
            EntityPlayer player = JSG.proxy.getPlayerClientSide();
            JSG.proxy.addScheduledTaskClientSide(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiAdminController(player, player.getEntityWorld(), message.pos, message.network)));

            return null;
        }

    }
}
