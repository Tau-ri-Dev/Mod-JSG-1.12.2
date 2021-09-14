package mrjake.aunis.item.gdo;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.item.AunisItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GDOActionPacketToServer implements IMessage {
	public GDOActionPacketToServer() {}
	
	private GDOActionEnum action;
	private EnumHand hand;
	private boolean next;
	
	public GDOActionPacketToServer(GDOActionEnum action, EnumHand hand, boolean next) {
		this.action = action;
		this.hand = hand;
		this.next = next;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(action.ordinal());
		buf.writeInt(hand == EnumHand.MAIN_HAND ? 0 : 1);
		buf.writeBoolean(next);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		action = GDOActionEnum.values()[buf.readInt()];
		hand = buf.readInt() == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		next = buf.readBoolean();
	}
	
	
	public static class GDOActionPacketServerHandler implements IMessageHandler<GDOActionPacketToServer, IMessage> {

		@Override
		public IMessage onMessage(GDOActionPacketToServer message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			WorldServer world = player.getServerWorld();

			world.addScheduledTask(() -> {
				ItemStack stack = player.getHeldItem(message.hand);
				
				if (stack.getItem() == AunisItems.UNIVERSE_DIALER && stack.hasTagCompound()) {
					NBTTagCompound compound = stack.getTagCompound();
					GDOMode mode = GDOMode.valueOf(compound.getByte("mode"));
					byte selected = compound.getByte("selected");

					switch (message.action) {
					
						case ENTER_CODE:
							break;
					}
				}
			});
			
			return null;
		}
		
	}
}
