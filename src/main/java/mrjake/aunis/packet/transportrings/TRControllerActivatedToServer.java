package mrjake.aunis.packet.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.packet.PositionedPacket;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.tileentity.transportrings.TRControllerAbstractTile;
import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.transportrings.SymbolGoauldEnum;
import mrjake.aunis.transportrings.TransportResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TRControllerActivatedToServer extends PositionedPacket {
	public TRControllerActivatedToServer() {}
	
	public int symbol;
	
	public TRControllerActivatedToServer(BlockPos pos, int symbol) {
		super(pos);
		
		this.symbol = symbol;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		
		buf.writeInt(symbol);
	}

	
	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		
		symbol = buf.readInt();
	}

	
	public static class TRControllerActivatedServerHandler implements IMessageHandler<TRControllerActivatedToServer, IMessage> {

		@Override
		public IMessage onMessage(TRControllerActivatedToServer message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			WorldServer world = player.getServerWorld();
			
			world.addScheduledTask(() -> {
				TRControllerAbstractTile controllerTile = (TRControllerAbstractTile) world.getTileEntity(message.pos);
				if(controllerTile != null) {
					TransportRingsAbstractTile ringsTile = controllerTile.getLinkedRingsTile(world);

					if (ringsTile != null) {
						AunisSoundHelper.playSoundEvent(world, message.pos, SoundEventEnum.RINGS_CONTROLLER_BUTTON);
						TransportResult result = ringsTile.addSymbolToAddress(SymbolGoauldEnum.valueOf(message.symbol-1));
						result.sendMessageIfFailed(player);
					} else
						player.sendStatusMessage(new TextComponentTranslation("tile.aunis.transportrings_controller_block.not_linked"), true);
				}
			});
			
			return null;
		}
		
	}
}
