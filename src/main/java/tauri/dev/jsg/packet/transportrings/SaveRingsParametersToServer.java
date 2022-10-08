package tauri.dev.jsg.packet.transportrings;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.PositionedPacket;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.transportrings.ParamsSetResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class SaveRingsParametersToServer extends PositionedPacket {
	public SaveRingsParametersToServer() {}

	String name;
	int distance;
	
	public SaveRingsParametersToServer(BlockPos pos, String name, int distance) {
		super(pos);

		this.name = name;
		this.distance = distance;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);

		buf.writeInt(name.length());
		buf.writeCharSequence(name, StandardCharsets.UTF_8);
		buf.writeInt(distance);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);

		int len = buf.readInt();
		name = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
		distance = buf.readInt();
	}
	
	
	public static class SaveRingsParametersServerHandler implements IMessageHandler<SaveRingsParametersToServer, IMessage> {

		@Override
		public StateUpdatePacketToClient onMessage(SaveRingsParametersToServer message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			WorldServer world = player.getServerWorld();
			
			world.addScheduledTask(() -> {
				TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(message.pos);
				if (ringsTile != null && ringsTile.setRingsParams(message.name, message.distance) == ParamsSetResult.DUPLICATE_ADDRESS)
					player.sendStatusMessage(new TextComponentTranslation("tile.jsg.transportrings_block.duplicate_address"), true);
			
				JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(message.pos, StateTypeEnum.GUI_STATE, ringsTile.getState(StateTypeEnum.GUI_STATE)), player);
			});
			
			return null;
		}
		
	}
}
