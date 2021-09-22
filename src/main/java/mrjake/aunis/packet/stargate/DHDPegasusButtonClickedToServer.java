package mrjake.aunis.packet.stargate;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.packet.PositionedPacket;
import mrjake.aunis.stargate.EnumStargateState;
import mrjake.aunis.stargate.StargateClosedReasonEnum;
import mrjake.aunis.stargate.StargateOpenResult;
import mrjake.aunis.stargate.network.SymbolPegasusEnum;
import mrjake.aunis.tileentity.DHDPegasusTile;
import mrjake.aunis.tileentity.stargate.StargatePegasusBaseTile;
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

	public DHDPegasusButtonClickedToServer(BlockPos pos, SymbolPegasusEnum symbol) {
		super(pos);
		this.symbol = symbol;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);

		buf.writeInt(symbol.id);
	}

	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);

		symbol = SymbolPegasusEnum.valueOf(buf.readInt());
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
						player.sendStatusMessage(new TextComponentTranslation("tile.aunis.dhd_block.no_crystal_warn"), true);
						return;
					}

					if (!dhdTile.isLinked()) {
						player.sendStatusMessage(new TextComponentTranslation("tile.aunis.dhd_block.not_linked_warn"), true);
						return;
					}

					StargatePegasusBaseTile gateTile = (StargatePegasusBaseTile) dhdTile.getLinkedGate(world);
					EnumStargateState gateState = gateTile.getStargateState();

					if (gateState.engaged() && message.symbol.brb()) {
						// Gate is open, BRB was press, possible closure attempt

						if (gateState.initiating())
							gateTile.attemptClose(StargateClosedReasonEnum.REQUESTED);
						else
							player.sendStatusMessage(new TextComponentTranslation("tile.aunis.dhd_block.incoming_wormhole_warn"), true);
					} else if (gateState.idle()) {
						// Gate is idle, some glyph was pressed

						if (message.symbol.brb()) {
							// BRB pressed on idling gate, attempt to open

							StargateOpenResult openResult = gateTile.attemptOpenAndFail();

							if (openResult == StargateOpenResult.NOT_ENOUGH_POWER) {
								player.sendStatusMessage(new TextComponentTranslation("tile.aunis.stargatebase_block.not_enough_power"), true);
							}
						} else if (gateTile.canAddSymbol(message.symbol)) {
							// Not BRB, some other glyph pressed on idling gate, we can add this symbol now
							//gateTile.addSymbolToAddressManual(message.symbol, null);
							gateTile.addSymbolToAddressDHD(message.symbol);
						}
					}


				});
			}
			
			return null;
		}
	}
}
