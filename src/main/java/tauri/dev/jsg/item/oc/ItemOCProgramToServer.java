package tauri.dev.jsg.item.oc;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerMode;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class ItemOCProgramToServer implements IMessage {
	public ItemOCProgramToServer() {}
	
	private EnumHand hand;
	private ItemOCMessage message;
		
	public ItemOCProgramToServer(EnumHand hand, ItemOCMessage message) {
		this.hand = hand;
		this.message = message;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(hand == EnumHand.MAIN_HAND ? 0 : 1);
		
		buf.writeShort(message.port);
		writeString(buf, message.name);
		writeString(buf, message.address);
		writeString(buf, message.dataStr);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		hand = buf.readInt() == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		
		short port = buf.readShort();
		String name = readString(buf);
		String address = readString(buf);
		String data = readString(buf);
		message = new ItemOCMessage(name, address, port, data);
	}
	
	private static void writeString(ByteBuf buf, String string) {
		buf.writeInt(string.length());
		buf.writeCharSequence(string, StandardCharsets.UTF_8);
	}
	
	private static String readString(ByteBuf buf) {
		int len = buf.readInt();
		return buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
	}
	
	
	public static class ItemOCProgramServerHandler implements IMessageHandler<ItemOCProgramToServer, IMessage> {
		
		public IMessage onMessage(ItemOCProgramToServer message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			WorldServer world = player.getServerWorld();
			world.addScheduledTask(() -> {
				ItemStack stack = player.getHeldItem(message.hand);
				if ((stack.getItem() == JSGItems.UNIVERSE_DIALER || stack.getItem() == JSGItems.GDO) && stack.hasTagCompound()) {
					NBTTagCompound compound = stack.getTagCompound();
					NBTTagList ocList = compound.getTagList(UniverseDialerMode.OC.tagListName, NBT.TAG_COMPOUND);
					ocList.appendTag(message.message.serializeNBT());
					compound.setTag(UniverseDialerMode.OC.tagListName, ocList);

					player.connection.sendPacket(new SPacketWindowItems(player.inventory.getSlotFor(stack), NonNullList.from(stack)));
				}

			});
			
			return null;
		}
	}
}
