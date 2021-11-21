package mrjake.aunis.item.gdo;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.Aunis;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.stargate.codesender.PlayerCodeSender;
import mrjake.aunis.stargate.network.StargateNetwork;
import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import mrjake.aunis.tileentity.stargate.StargateUniverseBaseTile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GDOActionPacketToServer implements IMessage {
	public GDOActionPacketToServer() {}
	
	private GDOActionEnum action;
	private EnumHand hand;
	private boolean next;
	private int code;
	
	public GDOActionPacketToServer(GDOActionEnum action, EnumHand hand, int code, boolean next) {
		this.action = action;
		this.hand = hand;
		this.code = code;
		this.next = next;
	}

	public GDOActionPacketToServer(GDOActionEnum action, EnumHand hand, boolean next) {
		this.action = action;
		this.hand = hand;
		this.code = -1;
		this.next = next;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(action.ordinal());
		buf.writeInt(hand == EnumHand.MAIN_HAND ? 0 : 1);
		buf.writeInt(code);
		buf.writeBoolean(next);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		action = GDOActionEnum.values()[buf.readInt()];
		hand = buf.readInt() == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		code = buf.readInt();
		next = buf.readBoolean();
	}

	public static class GDOActionPacketServerHandler implements IMessageHandler<GDOActionPacketToServer, IMessage> {

		@Override
		public IMessage onMessage(GDOActionPacketToServer message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			WorldServer world = player.getServerWorld();

			world.addScheduledTask(() -> {
				ItemStack stack = player.getHeldItem(message.hand);
				if (stack.getItem() == AunisItems.GDO && stack.hasTagCompound()) {
					NBTTagCompound compound = stack.getTagCompound();
					GDOMode mode = GDOMode.valueOf(compound.getByte("mode"));
					switch (message.action) {
						case SEND_CODE:
							if (compound.hasKey("linkedGate")) {
								try {
									BlockPos pos = BlockPos.fromLong(compound.getLong("linkedGate"));
									StargateClassicBaseTile gateTile = (StargateClassicBaseTile) world.getTileEntity(pos);
									if (gateTile == null || gateTile.getDialedAddress() == null) return;
									// todo both direction code sending
									StargateAbstractBaseTile targetGate = null;
									if (gateTile.getStargateState().initiating() || gateTile.getStargateState().engaged()) {
										targetGate = StargateNetwork.get(world).getStargate(gateTile.getDialedAddress()).getTileEntity();
										if (targetGate != null && targetGate instanceof StargateClassicBaseTile) {
											((StargateClassicBaseTile) targetGate).receiveIrisCode(new PlayerCodeSender(player), message.code);
										}
									}
								}
								catch (NullPointerException e) {
									Aunis.logger.error("Exception in GDO Action packet", e);
								}
							}
							break;
						case MODE_CHANGE:
							if (message.next)
								mode = mode.next();
							else
								mode = mode.prev();

							compound.setByte("mode", mode.id);
							break;

						case ADDRESS_CHANGE:
							byte selected = compound.getByte("selected");
							int addressCount = compound.getTagList(mode.tagListName, Constants.NBT.TAG_COMPOUND).tagCount();

							if (message.next && selected < addressCount-1)
								compound.setByte("selected", (byte) (selected+1));

							if (!message.next && selected > 0)
								compound.setByte("selected", (byte) (selected-1));
							break;
					}
				}
			});
			
			return null;
		}
		
	}
}
