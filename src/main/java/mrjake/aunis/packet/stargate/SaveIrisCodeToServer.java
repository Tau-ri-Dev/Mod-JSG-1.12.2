package mrjake.aunis.packet.stargate;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.PositionedPacket;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.tileentity.TransportRingsTile;
import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import mrjake.aunis.transportrings.ParamsSetResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class SaveIrisCodeToServer extends PositionedPacket {
	public SaveIrisCodeToServer() {}

	int code;

	public SaveIrisCodeToServer(BlockPos pos, int code) {
		super(pos);
		
		this.code = code;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		
		buf.writeInt(code);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);

		code = buf.readInt();
	}
	
	
	public static class SaveIrisCodeToServerHandler implements IMessageHandler<SaveIrisCodeToServer, IMessage> {

		@Override
		public StateUpdatePacketToClient onMessage(SaveIrisCodeToServer message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			WorldServer world = player.getServerWorld();
			
			world.addScheduledTask(() -> {
				StargateClassicBaseTile te;
				if(world.getTileEntity(message.pos) instanceof StargateClassicBaseTile){
					te = (StargateClassicBaseTile) world.getTileEntity(message.pos);
					te.setIrisCode(message.code);
					AunisPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(message.pos, StateTypeEnum.GUI_STATE, te.getState(StateTypeEnum.GUI_STATE)), player);
				}
			});
			
			return null;
		}
		
	}
}
