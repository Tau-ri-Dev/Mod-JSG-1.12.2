package tauri.dev.jsg.packet.gui.entry;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerItem;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerMode;
import tauri.dev.jsg.item.notebook.NotebookItem;
import tauri.dev.jsg.item.notebook.PageNotebookItem;
import tauri.dev.jsg.stargate.EnumDialingType;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.stargate.network.*;
import tauri.dev.jsg.stargate.teleportation.TeleportHelper;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;
import tauri.dev.jsg.util.BlockHelpers;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EntryActionToServer implements IMessage {
    private EnumHand hand;
    private EntryDataTypeEnum dataType;
    private EntryActionEnum action;
    private int index;
    private String name;
    private int maxSymbols;
    private StargateAddressDynamic addressToDial;
    private BlockPos linkedGate;
    private StargatePos targetGatePos;
    private EnumDialingType dialType = EnumDialingType.NORMAL;

    public EntryActionToServer() {
    }

    public EntryActionToServer(EnumHand hand, StargateAddressDynamic addressToDial, int maxSymbols, BlockPos linkedGate, EnumDialingType dialType) {
        this.hand = hand;
        this.dataType = EntryDataTypeEnum.ADMIN_CONTROLLER;
        this.action = EntryActionEnum.DIAL;
        this.index = -1;
        this.name = "";
        this.addressToDial = addressToDial;
        this.maxSymbols = maxSymbols;
        this.linkedGate = linkedGate;
        this.targetGatePos = null;
        this.dialType = dialType;
    }

    public EntryActionToServer(EnumHand hand, String name, StargatePos targetGate) {
        this.hand = hand;
        this.dataType = EntryDataTypeEnum.ADMIN_CONTROLLER;
        this.action = EntryActionEnum.RENAME;
        this.index = targetGate.symbolType.id;
        this.name = name;
        this.targetGatePos = targetGate;
        this.addressToDial = null;
        this.linkedGate = null;
    }

    public EntryActionToServer(EntryActionEnum action, StargatePos targetGate, boolean notGenerated) {
        this.hand = EnumHand.MAIN_HAND;
        this.dataType = EntryDataTypeEnum.ADMIN_CONTROLLER;
        this.action = action;
        this.index = (notGenerated ? 1 : 0);
        this.name = "";
        this.targetGatePos = targetGate;
        this.addressToDial = null;
        this.linkedGate = null;
    }

    public EntryActionToServer(EntryActionEnum action, BlockPos linkedGate) {
        this.hand = EnumHand.MAIN_HAND;
        this.dataType = EntryDataTypeEnum.ADMIN_CONTROLLER;
        this.action = action;
        this.index = -1;
        this.name = "";
        this.linkedGate = linkedGate;
    }


    public EntryActionToServer(EnumHand hand, EntryDataTypeEnum dataType, EntryActionEnum action, int index, String name) {
        this.hand = hand;
        this.dataType = dataType;
        this.action = action;
        this.index = index;
        this.name = name;
        this.addressToDial = null;
        this.targetGatePos = null;
        this.linkedGate = null;
    }

    private static void tagSwitchPlaces(NBTTagList list, int a, int b) {
        NBTBase tagA = list.get(a);
        list.set(a, list.get(b));
        list.set(b, tagA);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hand.ordinal());
        buf.writeInt(dataType.ordinal());
        buf.writeInt(action.ordinal());
        buf.writeInt(index);

        buf.writeInt(name.length());
        buf.writeCharSequence(name, StandardCharsets.UTF_8);
        buf.writeInt(maxSymbols);
        if (addressToDial != null) {
            buf.writeBoolean(true);
            addressToDial.toBytes(buf);
        } else buf.writeBoolean(false);
        if (linkedGate != null) {
            buf.writeBoolean(true);
            buf.writeLong(linkedGate.toLong());
        } else buf.writeBoolean(false);
        if (targetGatePos != null) {
            buf.writeBoolean(true);
            targetGatePos.toBytes(buf);
        } else buf.writeBoolean(false);
        if(dialType == null) dialType = EnumDialingType.NORMAL;
        buf.writeInt(dialType.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hand = EnumHand.values()[buf.readInt()];
        dataType = EntryDataTypeEnum.values()[buf.readInt()];
        action = EntryActionEnum.values()[buf.readInt()];
        index = buf.readInt();

        int size = buf.readInt();
        name = buf.readCharSequence(size, StandardCharsets.UTF_8).toString();
        maxSymbols = buf.readInt();
        if (buf.readBoolean()) {
            addressToDial = new StargateAddressDynamic(buf);
        }
        if (buf.readBoolean()) {
            linkedGate = BlockPos.fromLong(buf.readLong());
        }
        if (buf.readBoolean()) {
            targetGatePos = new StargatePos(SymbolTypeEnum.valueOf(index), buf);
        }
        int i = buf.readInt();
        if(EnumDialingType.values().length <= i) i = 0;
        dialType = EnumDialingType.values()[i];
    }

    public static class EntryActionServerHandler implements IMessageHandler<EntryActionToServer, IMessage> {

        @Override
        public IMessage onMessage(EntryActionToServer message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();

            world.addScheduledTask(() -> {
                ItemStack stack = player.getHeldItem(message.hand);
                NBTTagCompound compound = stack.getTagCompound();

                if (message.dataType.page()) {
                    if(compound == null) return;
                    NBTTagList list = compound.getTagList("addressList", NBT.TAG_COMPOUND);

                    switch (message.action) {
                        case RENAME:
                            NotebookItem.setNameForIndex(list, message.index, message.name);
                            break;

                        case MOVE_UP:
                            tagSwitchPlaces(list, message.index, message.index - 1);
                            break;

                        case MOVE_DOWN:
                            tagSwitchPlaces(list, message.index, message.index + 1);
                            break;

                        case REMOVE:
                            NBTTagCompound selectedCompound = list.getCompoundTagAt(message.index);
                            list.removeTag(message.index);

                            if (list.tagCount() == 0)
                                player.setHeldItem(message.hand, ItemStack.EMPTY);
                            else
                                compound.setInteger("selected", Math.min(message.index, list.tagCount() - 1));

                            ItemStack pageStack = new ItemStack(JSGItems.PAGE_NOTEBOOK_ITEM, 1, 1);
                            pageStack.setTagCompound(selectedCompound);
                            player.addItemStackToInventory(pageStack);

                            break;
                    }
                } else if (message.dataType.universe()) {
                    if(compound == null) return;
                    NBTTagList list = compound.getTagList(UniverseDialerMode.MEMORY.tagListName, NBT.TAG_COMPOUND);
                    BlockPos linkedPos = BlockPos.fromLong(compound.getLong(UniverseDialerMode.MEMORY.tagPosName));
                    NBTTagCompound selectedCompound = list.getCompoundTagAt(message.index);

                    switch (message.action) {
                        case RENAME:
                            UniverseDialerItem.setMemoryNameForIndex(list, message.index, message.name);
                            break;

                        case MOVE_UP:
                            tagSwitchPlaces(list, message.index, message.index - 1);
                            break;

                        case MOVE_DOWN:
                            tagSwitchPlaces(list, message.index, message.index + 1);
                            break;

                        case REMOVE:
                            list.removeTag(message.index);

                            UniverseDialerMode mode = UniverseDialerMode.valueOf(compound.getByte("mode"));
                            if (mode == UniverseDialerMode.MEMORY)
                                compound.setByte("selected", (byte) Math.min(message.index, list.tagCount() - 1));

                            break;

                        case DIAL:
                            int maxSymbols = SymbolUniverseEnum.getMaxSymbolsDisplay(selectedCompound.getBoolean("hasUpgrade"));
                            StargateUniverseBaseTile gateTile = (StargateUniverseBaseTile) world.getTileEntity(linkedPos);
                            if (gateTile == null) break;

                            if (gateTile.dialAddress(new StargateAddress(selectedCompound), maxSymbols, false, EnumDialingType.NORMAL))
                                player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.dial_start"), true);

                            break;
                    }
                } else if (message.dataType.oc()) {
                    if(compound == null) return;
                    NBTTagList list = compound.getTagList(UniverseDialerMode.OC.tagListName, NBT.TAG_COMPOUND);

                    switch (message.action) {
                        case RENAME:
                            UniverseDialerItem.changeOCMessageAtIndex(list, message.index, (ocMessage) -> ocMessage.name = message.name);
                            break;

                        case MOVE_UP:
                            tagSwitchPlaces(list, message.index, message.index - 1);
                            break;

                        case MOVE_DOWN:
                            tagSwitchPlaces(list, message.index, message.index + 1);
                            break;

                        case REMOVE:
                            list.removeTag(message.index);

                            UniverseDialerMode mode = UniverseDialerMode.valueOf(compound.getByte("mode"));
                            if (mode == UniverseDialerMode.OC)
                                compound.setByte("selected", (byte) Math.min(message.index, list.tagCount() - 1));

                            break;
                    }
                } else if (message.dataType.admin()) {
                    switch (message.action) {
                        case RENAME:
                            StargateAbstractBaseTile gateTile = message.targetGatePos.getTileEntity();
                            if (gateTile == null) return;
                            gateTile.renameStargatePos(message.name);
                            break;
                        case DIAL:
                            StargateClassicBaseTile gateTile1 = (StargateClassicBaseTile) world.getTileEntity(message.linkedGate);
                            if (gateTile1 == null) return;
                            if (gateTile1.getStargateState().engaged()) {
                                gateTile1.attemptClose(StargateClosedReasonEnum.REQUESTED);
                                break;
                            }
                            gateTile1.dialAddress(message.addressToDial, message.maxSymbols - 1, true, message.dialType);
                            break;
                        case ABORT:
                            StargateClassicBaseTile gateTile2 = (StargateClassicBaseTile) world.getTileEntity(message.linkedGate);
                            if (gateTile2 == null) return;
                            if (gateTile2.getStargateState().dialing())
                                gateTile2.abortDialingSequence();
                            break;
                        case TOGGLE_IRIS:
                            StargateClassicBaseTile gateTile3 = (StargateClassicBaseTile) world.getTileEntity(message.linkedGate);
                            if (gateTile3 == null) return;
                            if (gateTile3.hasIris())
                                gateTile3.toggleIris();
                            break;
                        case GIVE_NOTEBOOK:
                            NBTTagList tagList = new NBTTagList();

                            for (SymbolTypeEnum s : SymbolTypeEnum.values()) {
                                StargateAddress address;
                                int originId;
                                if (message.index == 1) {
                                    // gate is not generated - there is no tileEntity
                                    StargateNetwork sgn = StargateNetwork.get(world);
                                    Map<SymbolTypeEnum, StargateAddress> map = sgn.getMapNotGenerated().get(message.targetGatePos);
                                    if(map == null){
                                        JSG.info("Lol123");
                                        continue;
                                    }
                                    address = StargateNetwork.get(world).getMapNotGenerated().get(message.targetGatePos).get(s);
                                    originId = StargateClassicBaseTile.getOriginId(null, message.targetGatePos.dimensionID, -1);
                                } else {
                                    address = message.targetGatePos.getTileEntity().getStargateAddress(s);
                                    originId = message.targetGatePos.getTileEntity().getOriginId();
                                }
                                NBTTagCompound pageCompound = PageNotebookItem.getCompoundFromAddress(address, true, false, false, PageNotebookItem.getRegistryPathFromWorld(world, message.targetGatePos.gatePos), originId);
                                tagList.appendTag(pageCompound);
                            }

                            ItemStack notebook = new ItemStack(JSGItems.NOTEBOOK_ITEM, 1);
                            NBTTagCompound compound1 = new NBTTagCompound();
                            compound1.setTag("addressList", tagList);
                            compound1.setInteger("selected", 0);
                            notebook.setTagCompound(compound1);
                            if (!message.targetGatePos.getName().equals(""))
                                notebook.setStackDisplayName(message.targetGatePos.getName());
                            else
                                notebook.setStackDisplayName(BlockHelpers.blockPosToBetterString(message.targetGatePos.gatePos));

                            player.addItemStackToInventory(notebook);
                            break;
                        case TELEPORT_TO_POS:
                            TeleportHelper.teleportEntityToStargate(player, message.targetGatePos, true);
                            break;
                        default:
                            break;
                    }
                }
            });

            return null;
        }

    }
}
