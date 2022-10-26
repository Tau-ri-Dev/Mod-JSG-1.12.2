package tauri.dev.jsg.packet.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.packet.PositionedPacket;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.stargate.network.SymbolPegasusEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDPegasusTile;
import tauri.dev.jsg.tileentity.stargate.StargatePegasusBaseTile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;

public class DHDPegasusButtonClickedToServer extends PositionedPacket {
	public DHDPegasusButtonClickedToServer() {}

	public SymbolPegasusEnum symbol;
	public boolean force;

	public DHDPegasusButtonClickedToServer(BlockPos pos, SymbolPegasusEnum symbol) {
		super(pos);
		this.symbol = symbol;
	}

	public DHDPegasusButtonClickedToServer(BlockPos pos, SymbolPegasusEnum symbol, boolean force) {
		super(pos);
		this.symbol = symbol;
		this.force = force;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);

		buf.writeInt(symbol.id);
		buf.writeBoolean(force);
	}

	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);

		symbol = SymbolPegasusEnum.valueOf(buf.readInt());
		force = buf.readBoolean();
	}
	
	
	public static class DHDPegasusButtonClickedServerHandler implements IMessageHandler<DHDPegasusButtonClickedToServer, IMessage> {

		@Override
		public IMessage onMessage(DHDPegasusButtonClickedToServer message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			WorldServer world = player.getServerWorld();
			if(world.getTileEntity(message.pos) instanceof DHDPegasusTile) {

				world.addScheduledTask(() -> {
					DHDPegasusTile dhdTile = (DHDPegasusTile) world.getTileEntity(message.pos);

					if (dhdTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0).isEmpty()) {
						player.sendStatusMessage(new TextComponentTranslation("tile.jsg.dhd_block.no_crystal_warn"), true);
						return;
					}

					if (!dhdTile.isLinked()) {
						player.sendStatusMessage(new TextComponentTranslation("tile.jsg.dhd_block.not_linked_warn"), true);
						return;
					}

					StargatePegasusBaseTile gateTile = (StargatePegasusBaseTile) dhdTile.getLinkedGate(world);
					EnumStargateState gateState = gateTile.getStargateState();

					if (gateState.engaged() && message.symbol.brb()) {
						// Gate is open, BRB was press, possible closure attempt

						if (gateState.initiating())
							gateTile.attemptClose(StargateClosedReasonEnum.REQUESTED);
						else
							player.sendStatusMessage(new TextComponentTranslation("tile.jsg.dhd_block.incoming_wormhole_warn"), true);
					}
					else if(message.force && message.symbol.brb()){
						gateTile.abortDialingSequence();
					}
					else if ((gateState.idle() || gateState.dialing()) && !gateState.dialingComputer()) {
						// Gate is idle, some glyph was pressed
						gateTile.addSymbolToAddressDHD(message.symbol, player);
					}


				});
			}
			
			return null;
		}
	}
}
