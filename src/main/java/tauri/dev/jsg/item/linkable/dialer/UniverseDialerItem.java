package tauri.dev.jsg.item.linkable.dialer;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.capability.endpoint.ItemEndpointCapability;
import tauri.dev.jsg.capability.endpoint.ItemEndpointInterface;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.linkable.LinkAbleCapabilityProvider;
import tauri.dev.jsg.item.oc.ItemOCMessage;
import tauri.dev.jsg.item.renderer.CustomModel;
import tauri.dev.jsg.item.renderer.CustomModelItemInterface;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.stargate.EnumIrisMode;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.stargate.NearbyGate;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.stargate.network.SymbolUniverseEnum;
import tauri.dev.jsg.tileentity.props.DestinyCountDownTile;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.jsg.transportrings.TransportRings;
import tauri.dev.jsg.util.EnumKeyInterface;
import tauri.dev.jsg.util.EnumKeyMap;
import tauri.dev.jsg.util.LinkingHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

import static tauri.dev.jsg.item.linkable.dialer.UniverseDialerMode.NEARBY;

public class UniverseDialerItem extends Item implements CustomModelItemInterface {

    public static final String ITEM_NAME = "universe_dialer";

    public enum UniverseDialerVariants implements EnumKeyInterface<Integer> {
        NORMAL(0, ITEM_NAME), BROKEN(1, ITEM_NAME + "_broken");

        public final int meta;
        public final String name;

        UniverseDialerVariants(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        @Override
        public Integer getKey() {
            return meta;
        }

        private static final EnumKeyMap<Integer, UniverseDialerVariants> KEY_MAP = new EnumKeyMap<>(values());

        public static UniverseDialerVariants valueOf(int id) {
            return KEY_MAP.valueOf(id);
        }
    }

    public UniverseDialerItem() {
        setRegistryName(new ResourceLocation(JSG.MOD_ID, ITEM_NAME));
        setUnlocalizedName(JSG.MOD_ID + "." + ITEM_NAME);
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(JSGCreativeTabsHandler.JSG_TOOLS_CREATIVE_TAB);
        // setMaxStackSize(1);
    }

    // TODO replace with capabilities. If item will have NBT like "display:Name" it will not init custom NBT! -- slava110
    // MrJake: Capabilities are meh in 1.12. Hope they've fixed them in 1.16.
    private static void checkNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            initNBT(stack);
        }
    }

    public static void initNBT(ItemStack stack) {
        NBTTagCompound compound = new NBTTagCompound();
        switch (UniverseDialerVariants.valueOf(stack.getItemDamage())) {
            case NORMAL:
                compound.setByte("mode", NEARBY.id);
                compound.setByte("selected", (byte) 0);
                compound.setTag("saved", new NBTTagList());
                break;
            case BROKEN:

                break;
        }

        stack.setTagCompound(compound);
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);
            initNBT(stack);
            items.add(stack);
        }
    }

    private CustomModel customModel;

    @Override
    public void setCustomModel(CustomModel customModel) {
        this.customModel = customModel;
    }

    public TransformType getLastTransform() {
        return customModel.lastTransform;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntityItemStackRenderer createTEISR() {
        return new UniverseDialerTEISR();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        if (stack.getItemDamage() == UniverseDialerVariants.BROKEN.meta) return null;
        return new LinkAbleCapabilityProvider();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack.getMetadata() == UniverseDialerVariants.BROKEN.meta) {
            return "item.jsg.universe_dialer.broken";
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            switch (UniverseDialerVariants.valueOf(stack.getItemDamage())) {
                case NORMAL:
                    NBTTagList list = Objects.requireNonNull(stack.getTagCompound()).getTagList("saved", NBT.TAG_COMPOUND);
                    tooltip.add(TextFormatting.GRAY + JSG.proxy.localize("item.jsg.universe_dialer.saved_gates", list.tagCount()));

                    for (int i = 0; i < list.tagCount(); i++) {
                        NBTTagCompound compound = list.getCompoundTagAt(i);

                        if (compound.hasKey("name")) {
                            tooltip.add(TextFormatting.AQUA + compound.getString("name"));
                        }
                    }
                    break;
                case BROKEN:
                    break;
            }
        }
    }

    public BlockPos getNearest(World world, BlockPos pos, ArrayList<BlockPos> blacklist, UniverseDialerMode mode) {
        return LinkingHelper.findClosestPos(world, pos, new BlockPos(JSGConfig.DialHomeDevice.mechanics.universeDialerReach, 40, tauri.dev.jsg.config.JSGConfig.DialHomeDevice.mechanics.universeDialerReach), mode.matchBlocks, blacklist);
    }

    @Override
    public void onUpdate(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote) {
            checkNBT(stack);
            if (stack.getItemDamage() == UniverseDialerVariants.BROKEN.meta) {
                return;
            }
            NBTTagCompound compound = stack.getTagCompound();
            if(compound != null && compound.hasKey("timerCountTo")){
                long time = compound.getLong("timerCountTo");
                long actualTicks = (time - entity.getEntityWorld().getTotalWorldTime());
                if(entity instanceof EntityPlayerMP) {
                    EntityPlayerMP p = (EntityPlayerMP) entity;
                    if (actualTicks == 0)
                        JSGSoundHelper.playSoundToPlayer(p, SoundEventEnum.DESTINY_COUNTDOWN_STOP, p.getPosition());
                    if (actualTicks == (20*60))
                        JSGSoundHelper.playSoundToPlayer(p, SoundEventEnum.DESTINY_COUNTDOWN_ONE_MINUTE, p.getPosition());
                }
            }
            boolean wasLinked = false;
            if (world.getTotalWorldTime() % 20 == 0 && isSelected && compound != null) {
                BlockPos pos = entity.getPosition();

                int reachSquared = tauri.dev.jsg.config.JSGConfig.DialHomeDevice.mechanics.universeDialerReach * tauri.dev.jsg.config.JSGConfig.DialHomeDevice.mechanics.universeDialerReach * 2;
                UniverseDialerMode mode = UniverseDialerMode.valueOf(compound.getByte("mode"));
                compound.setBoolean("serverSideEnabledFastDial", false);

                if (mode.linkable) {
                    if (compound.hasKey(mode.tagPosName)) {
                        wasLinked = true;
                        BlockPos tilePos = BlockPos.fromLong(compound.getLong(mode.tagPosName));

                        if (!JSGBlocks.isInBlocksArray(world.getBlockState(tilePos).getBlock(), mode.matchBlocks) || tilePos.distanceSq(pos) > reachSquared) {
                            compound.removeTag(mode.tagPosName);
                        }
                    }

                    boolean found = false;

                    BlockPos targetPos;
                    ArrayList<BlockPos> blacklist = new ArrayList<>();
                    int loop = 0;
                    do {
                        targetPos = getNearest(world, pos, blacklist, mode);
                        if (targetPos == null)
                            break;

                        switch (mode) {
                            case MEMORY:
                            case NEARBY:
                                StargateAbstractBaseTile gateTile = (StargateAbstractBaseTile) world.getTileEntity(targetPos);

                                if (gateTile == null || !gateTile.isMerged() || !(gateTile instanceof StargateUniverseBaseTile)) {
                                    blacklist.add(targetPos);
                                    continue;
                                }

                                StargateUniverseBaseTile uniTile = (StargateUniverseBaseTile) gateTile;
                                NBTTagList nearbyList = new NBTTagList();
                                try {

                                    addrToBytes(gateTile.getDialedAddress(), compound, "dialedAddress");
                                    addrToBytes(((StargateUniverseBaseTile) gateTile).getAddressToDial(), compound, "toDialAddress");
                                    compound.setInteger("gateStatus", gateTile.getStargateState().id);
                                    compound.setBoolean("serverSideEnabledFastDial", ((StargateClassicBaseTile) gateTile).getConfig().getOption(StargateClassicBaseTile.ConfigOptions.ENABLE_FAST_DIAL.id).getBooleanValue());

                                    ArrayList<NearbyGate> foundList = uniTile.getNearbyGates(SymbolTypeEnum.UNIVERSE, false, false);

                                    if(foundList != null) {
                                        for (NearbyGate gate : foundList) {
                                            NBTTagCompound entryCompound = gate.address.serializeNBT();
                                            entryCompound.setBoolean("hasUpgrade", gate.symbolsNeeded > 7);

                                            nearbyList.appendTag(entryCompound);

                                        }
                                    }
                                    compound.setTag(NEARBY.tagListName, nearbyList);
                                    compound.setLong(mode.tagPosName, targetPos.toLong());
                                    found = true;
                                } catch (ConcurrentModificationException e) {
                                    JSG.error("Error while iterating nearby stargates occurred", e);

                                    if (entity instanceof EntityPlayer) {
                                        ((EntityPlayer) entity).sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.dialer_broke"), true);
                                    }
                                    broke(stack);

                                }
                                break;

                            case RINGS:
                                TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(targetPos);
                                if (ringsTile == null) {
                                    blacklist.add(targetPos);
                                    continue;
                                }
                                NBTTagList ringsList = new NBTTagList();

                                for (TransportRings rings : ringsTile.ringsMap.values()) {
                                    ringsList.appendTag(rings.serializeNBT());
                                }

                                compound.setTag(mode.tagListName, ringsList);
                                compound.setLong(mode.tagPosName, targetPos.toLong());
                                found = true;
                                break;

                            case GATE_INFO:
                                TileEntity tile = world.getTileEntity(targetPos);
                                if (tile instanceof StargateClassicBaseTile) {
                                    StargateClassicBaseTile t = (StargateClassicBaseTile) tile;

                                    compound.setBoolean("serverSideEnabledFastDial", t.getConfig().getOption(StargateClassicBaseTile.ConfigOptions.ENABLE_FAST_DIAL.id).getBooleanValue());
                                    compound.setInteger("gateStatus", t.getStargateState().id);
                                    compound.setString("gateOpenTime", t.getOpenedSeconds() > 0 ? t.getOpenedSecondsToDisplayAsMinutes() : "CLOSED");
                                    compound.setString("gateIrisState", t.hasIris() ? t.getIrisState().toString() : "MISSING");
                                    compound.setString("gateLastSymbol", (t.getDialedAddress().size() > 0) ? t.getDialedAddress().get(t.getDialedAddress().size() - 1).toString() + " (" + t.getDialedAddress().size() + ")" : "-- (0)");

                                    if (t.getStargateState().notInitiating())
                                        compound.setString("gateLastSymbol", "INCOMING");

                                    compound.setLong(mode.tagPosName, targetPos.toLong());
                                    found = true;
                                }
                                break;

                            case COUNTDOWN:
                                TileEntity timerTile = world.getTileEntity(targetPos);
                                if (timerTile instanceof DestinyCountDownTile) {
                                    DestinyCountDownTile timerTileCasted = (DestinyCountDownTile) timerTile;
                                    compound.setLong("timerCountTo", timerTileCasted.countdownTo);
                                    compound.setLong(mode.tagPosName, targetPos.toLong());
                                    found = true;
                                }
                                break;

                            default:
                                break;
                        }

                        if (found && !wasLinked && entity instanceof EntityPlayerMP) {
                            JSGSoundHelper.playSoundToPlayer(((EntityPlayerMP) entity), SoundEventEnum.UNIVERSE_DIALER_CONNECTED, entity.getPosition());
                        }
                        loop++;
                    } while (!found && loop < 100);
                }
            }

            // Server side
            ItemEndpointInterface endpointStack = stack.getCapability(ItemEndpointCapability.ENDPOINT_CAPABILITY, null);
            if (endpointStack != null)
                endpointStack.checkAndUpdateEndpoint(world.getTotalWorldTime());
        }
    }

    private static void addrToBytes(StargateAddress address, NBTTagCompound compound, String baseName) {
        if (compound == null || address == null || baseName == null) return;
        compound.setByte(baseName + "_addressLength", (byte) address.getSize());
        compound.setByte(baseName + "_symbolType", (byte) address.getSymbolType().id);
        for (int i = 0; i < address.getSize(); i++) {
            compound.setByte(baseName + "_" + i, (byte) address.get(i).getId());
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, @Nonnull EntityPlayer player) {
        if (stack.getItemDamage() != UniverseDialerVariants.BROKEN.meta)
            Objects.requireNonNull(stack.getCapability(ItemEndpointCapability.ENDPOINT_CAPABILITY, null)).removeEndpoint();

        return super.onDroppedByPlayer(stack, player);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (!world.isRemote && player.getHeldItem(hand).getItemDamage() == UniverseDialerVariants.NORMAL.meta) {
            boolean shift = player.isSneaking();
            checkNBT(player.getHeldItem(hand));
            NBTTagCompound compound = player.getHeldItem(hand).getTagCompound();
            UniverseDialerMode mode = UniverseDialerMode.valueOf(Objects.requireNonNull(compound).getByte("mode"));
            int selected = compound.getByte("selected");

            if (mode.linkable && !compound.hasKey(mode.tagPosName))
                return super.onItemRightClick(world, player, hand);

            BlockPos linkedPos = BlockPos.fromLong(compound.getLong(mode.tagPosName));
            NBTTagList tagList = compound.getTagList(mode.tagListName, NBT.TAG_COMPOUND);

            if (selected >= tagList.tagCount())
                return super.onItemRightClick(world, player, hand);

            NBTTagCompound selectedCompound = tagList.getCompoundTagAt(selected);

            switch (mode) {
                case MEMORY:
                case NEARBY:
                    StargateUniverseBaseTile gateTile = (StargateUniverseBaseTile) world.getTileEntity(linkedPos);
                    if (gateTile == null) break;
                    if(shift && gateTile.hasIris() && gateTile.getIrisMode() == EnumIrisMode.DIALER){
                        gateTile.toggleIris();
                        break;
                    }
                    switch (gateTile.getStargateState()) {
                        case IDLE:
                            int maxSymbols = SymbolUniverseEnum.getMaxSymbolsDisplay(selectedCompound.getBoolean("hasUpgrade"));
                            StargateAddress address = new StargateAddress(selectedCompound);
                            /*StargatePos targetStargate = gateTile.getNetwork().getStargate(address);
                            if(targetStargate != null){
                                boolean sameDim = (targetStargate.dimensionID == gateTile.world().provider.getDimension());
                                boolean sameType = (targetStargate.getTileEntity().getSymbolType() == gateTile.getSymbolType());
                                int symbolsNeeded = (sameType ? (sameDim ? 6 : 7) : 8);
                                maxSymbols = Math.min(symbolsNeeded, maxSymbols);
                            }*/
                            gateTile.dialAddress(address, maxSymbols);
                            player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.dial_start"), true);
                            if(player instanceof EntityPlayerMP)
                                JSGSoundHelper.playSoundToPlayer(((EntityPlayerMP) player), SoundEventEnum.UNIVERSE_DIALER_START_DIAL, player.getPosition());
                            break;

                        case ENGAGED_INITIATING:
                            gateTile.attemptClose(StargateClosedReasonEnum.REQUESTED);
                            if(player instanceof EntityPlayerMP)
                                JSGSoundHelper.playSoundToPlayer(((EntityPlayerMP) player), SoundEventEnum.UNIVERSE_DIALER_START_DIAL, player.getPosition());
                            break;

                        case ENGAGED:
                            player.sendStatusMessage(new TextComponentTranslation("tile.jsg.dhd_block.incoming_wormhole_warn"), true);
                            //TODO(Mine): add error sound
                            //if(player instanceof EntityPlayerMP)
                            //    JSGSoundHelper.playSoundToPlayer(((EntityPlayerMP) player), SoundEventEnum.UNIVERSE_DIALER_START_DIAL, player.getPosition());
                            break;

                        default:
                            if (gateTile.getStargateState() == EnumStargateState.DIALING) {
                                if (gateTile.abortDialingSequence()) {
                                    player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.aborting"), true);
                                    if(player instanceof EntityPlayerMP)
                                        JSGSoundHelper.playSoundToPlayer(((EntityPlayerMP) player), SoundEventEnum.UNIVERSE_DIALER_START_DIAL, player.getPosition());
                                    break;
                                }
                            }
                            player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.gate_busy"), true);
                            //TODO(Mine): add error sound
                            //if(player instanceof EntityPlayerMP)
                            //    JSGSoundHelper.playSoundToPlayer(((EntityPlayerMP) player), SoundEventEnum.UNIVERSE_DIALER_START_DIAL, player.getPosition());
                            break;
                    }

                    break;

                case GATE_INFO:
                    StargateUniverseBaseTile tile = (StargateUniverseBaseTile) world.getTileEntity(linkedPos);
                    if (tile == null) break;
                    if(shift && tile.hasIris() && tile.getIrisMode() == EnumIrisMode.DIALER){
                        tile.toggleIris();
                        break;
                    }
                    switch (tile.getStargateState()) {
                        case IDLE:
                            break;
                        case ENGAGED_INITIATING:
                            tile.attemptClose(StargateClosedReasonEnum.REQUESTED);
                            break;

                        case ENGAGED:
                            player.sendStatusMessage(new TextComponentTranslation("tile.jsg.dhd_block.incoming_wormhole_warn"), true);
                            break;

                        default:
                            if (tile.getStargateState() == EnumStargateState.DIALING) {
                                if (tile.abortDialingSequence()) {
                                    player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.aborting"), true);
                                    break;
                                }
                            }
                            player.sendStatusMessage(new TextComponentTranslation("item.jsg.universe_dialer.gate_busy"), true);
                            break;
                    }

                    break;

                case RINGS:
                    TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(linkedPos);
                    if (ringsTile == null) break;
                    ringsTile.attemptTransportTo(new TransportRings(selectedCompound).getAddress(SymbolTypeTransportRingsEnum.GOAULD), 5).sendMessageIfFailed(player);

                    break;

                case OC:
                    ItemOCMessage message = new ItemOCMessage(selectedCompound);
                    JSG.debug("Sending OC message: " + message);
                    JSG.ocWrapper.sendWirelessPacketPlayer("unv-dialer", player, player.getHeldItem(hand), message.address, message.port, message.getData());
                    break;
            }
        }

        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void setCustomModelLocation() {
        ModelResourceLocation modelLocation = new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, modelLocation);

        CustomModelItemInterface.super.setCustomModelLocation();
        ModelBakery.registerItemVariants(JSGItems.UNIVERSE_DIALER, modelLocation);
        ModelLoader.setCustomMeshDefinition(JSGItems.UNIVERSE_DIALER, stack -> modelLocation);
    }

    public static void broke(ItemStack stack) {
        if (stack.getItem() == JSGItems.UNIVERSE_DIALER) {
            stack.setItemDamage(UniverseDialerVariants.BROKEN.meta);
            if (stack.hasTagCompound()) stack.setTagCompound(null);
        }
    }

    // ------------------------------------------------------------------------------------------------------------
    // NBT handles

    public static void setMemoryNameForIndex(NBTTagList list, int index, String name) {
        list.getCompoundTagAt(index).setString("name", name);
    }

    public static void changeOCMessageAtIndex(NBTTagList list, int index, ChangeMessage changeMessage) {
        ItemOCMessage message = new ItemOCMessage(list.getCompoundTagAt(index));
        changeMessage.change(message);
        list.set(index, message.serializeNBT());
    }

    public interface ChangeMessage {
        void change(ItemOCMessage message);
    }
}
