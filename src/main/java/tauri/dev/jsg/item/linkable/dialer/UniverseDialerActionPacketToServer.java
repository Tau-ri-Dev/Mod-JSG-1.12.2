package tauri.dev.jsg.item.linkable.dialer;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UniverseDialerActionPacketToServer implements IMessage {
	public UniverseDialerActionPacketToServer() {}
	
	private UniverseDialerActionEnum action;
	private EnumHand hand;
	private boolean next;
	
	public UniverseDialerActionPacketToServer(UniverseDialerActionEnum action, EnumHand hand, boolean next) {
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
		action = UniverseDialerActionEnum.values()[buf.readInt()];
		hand = buf.readInt() == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		next = buf.readBoolean();
	}
	
	
	public static class UniverseDialerActionPacketServerHandler implements IMessageHandler<UniverseDialerActionPacketToServer, IMessage> {

		@Override
		public IMessage onMessage(UniverseDialerActionPacketToServer message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			WorldServer world = player.getServerWorld();

			world.addScheduledTask(() -> {
				ItemStack stack = player.getHeldItem(message.hand);

				boolean playModeChangeSound = false;
				boolean playDialFailSound = false;

				if (stack.getItem() == JSGItems.UNIVERSE_DIALER && stack.hasTagCompound()) {
					NBTTagCompound compound = stack.getTagCompound();
					UniverseDialerMode mode = UniverseDialerMode.valueOf(compound.getByte("mode"));
					byte selected = compound.getByte("selected");

					switch (message.action) {
					
						case MODE_CHANGE:
							if (message.next) // message.offset < 0
								mode = mode.next();
							else
								mode = mode.prev();
							
							compound.setByte("mode", mode.id);
							compound.setByte("selected", (byte) 0);
							
							break;
							
							
						case ADDRESS_CHANGE:
							int addressCount = compound.getTagList(mode.tagListName, NBT.TAG_COMPOUND).tagCount();
							
							if (message.next && selected < addressCount-1) { // message.offset < 0
								compound.setByte("selected", (byte) (selected + 1));
								playModeChangeSound = true;
							}
							
							if (!message.next && selected > 0) {
								compound.setByte("selected", (byte) (selected - 1));
								playModeChangeSound = true;
							}
							
							break;
							
							
						case ABORT:
							if (compound.hasKey("linkedGate")) {
								BlockPos pos = BlockPos.fromLong(compound.getLong("linkedGate"));
								StargateUniverseBaseTile gateTile = (StargateUniverseBaseTile) world.getTileEntity(pos);
								if(gateTile == null) break;
								if (gateTile.getStargateState() == EnumStargateState.DIALING) {
									if(gateTile.abortDialingSequence()) {
										player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.aborting"), true);
										playDialFailSound = true;
									}
								}
								
								else {
									player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.not_dialing"), true);
								}
							}
							
							else {
								player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.not_linked"), true);
							}
							
							break;

						case SET_FAST_DIAL:
							if (compound.hasKey("linkedGate")) {
								BlockPos pos = BlockPos.fromLong(compound.getLong("linkedGate"));
								StargateUniverseBaseTile gateTile = (StargateUniverseBaseTile) world.getTileEntity(pos);
								if(gateTile == null) break;
								if (gateTile.getStargateState().idle()) {
									gateTile.setFastDial(!gateTile.getFastDialState());
									playModeChangeSound = true;
									if(gateTile.getFastDialState())
										player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.fast_dail_true"), true);
									else
										player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.fast_dail_false"), true);
								}

								else {
									player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.gate_busy"), true);
								}
							}

							else {
								player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.not_linked"), true);
							}

							break;
					}
				}

				if(playDialFailSound)
					JSGSoundHelper.playSoundToPlayer(player, SoundEventEnum.UNIVERSE_DIALER_START_DIAL, player.getPosition());
				if(playModeChangeSound)
					JSGSoundHelper.playSoundToPlayer(player, SoundEventEnum.UNIVERSE_DIALER_MODE_CHANGE, player.getPosition());
			});
			
			return null;
		}
		
	}
}
