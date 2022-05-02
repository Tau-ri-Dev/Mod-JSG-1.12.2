package mrjake.aunis.item.dialer;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.capability.endpoint.ItemEndpointCapability;
import mrjake.aunis.capability.endpoint.ItemEndpointInterface;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.item.oc.ItemOCMessage;
import mrjake.aunis.item.renderer.CustomModel;
import mrjake.aunis.item.renderer.CustomModelItemInterface;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.stargate.EnumStargateState;
import mrjake.aunis.stargate.StargateClosedReasonEnum;
import mrjake.aunis.stargate.network.*;
import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import mrjake.aunis.tileentity.stargate.StargateUniverseBaseTile;
import mrjake.aunis.transportrings.SymbolTypeTransportRingsEnum;
import mrjake.aunis.transportrings.TransportRings;
import mrjake.aunis.util.EnumKeyInterface;
import mrjake.aunis.util.EnumKeyMap;
import mrjake.aunis.util.LinkingHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

import static mrjake.aunis.Aunis.*;
import static mrjake.aunis.item.dialer.UniverseDialerMode.RINGS;

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
        setRegistryName(new ResourceLocation(Aunis.ModID, ITEM_NAME));
        setUnlocalizedName(Aunis.ModID + "." + ITEM_NAME);
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(aunisToolsCreativeTab);
        // setMaxStackSize(1);
    }

    // TODO replace with capabilities. If item will have NBT like "display:Name" it will not init custom NBT! -- slava110
    // MrJake: Capabilities are meh in 1.12. Hope they've fixed them in 1.16.
    private static void checkNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            initNBT(stack);
        }
    }

    private static void initNBT(ItemStack stack) {
        NBTTagCompound compound = new NBTTagCompound();
        switch (UniverseDialerVariants.valueOf(stack.getItemDamage())) {
            case NORMAL:
                compound.setByte("mode", UniverseDialerMode.NEARBY.id);
                compound.setByte("selected", (byte) 0);
                compound.setTag("saved", new NBTTagList());
                break;
            case BROKEN:

                break;
        }

        stack.setTagCompound(compound);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
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
        return new UniverseDialerCapabilityProvider();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack.getMetadata() == UniverseDialerVariants.BROKEN.meta) {
            return "item.aunis.universe_dialer.broken";
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            switch (UniverseDialerVariants.valueOf(stack.getItemDamage())) {
                case NORMAL:
                    NBTTagList list = stack.getTagCompound().getTagList("saved", NBT.TAG_COMPOUND);
                    tooltip.add(TextFormatting.GRAY + Aunis.proxy.localize("item.aunis.universe_dialer.saved_gates", list.tagCount()));

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

    public BlockPos getNearest(World world, BlockPos pos, ArrayList<BlockPos> blacklist, UniverseDialerMode mode){
        return LinkingHelper.findClosestPos(world, pos, new BlockPos(AunisConfig.stargateConfig.universeDialerReach, 40, AunisConfig.stargateConfig.universeDialerReach), mode.matchBlocks, blacklist);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote) {
            checkNBT(stack);
            if (stack.getItemDamage() == UniverseDialerVariants.BROKEN.meta) {
                return;
            }
            NBTTagCompound compound = stack.getTagCompound();

            if (world.getTotalWorldTime() % 20 == 0 && isSelected && compound != null) {
                BlockPos pos = entity.getPosition();

                int reachSquared = AunisConfig.stargateConfig.universeDialerReach * AunisConfig.stargateConfig.universeDialerReach * 2;
                UniverseDialerMode mode = UniverseDialerMode.valueOf(compound.getByte("mode"));
                compound.setBoolean("serverSideEnabledFastDial", false);

                if (mode.linkable) {
                    if (compound.hasKey(mode.tagPosName)) {
                        BlockPos tilePos = BlockPos.fromLong(compound.getLong(mode.tagPosName));

                        if (!AunisBlocks.isInBlocksArray(world.getBlockState(tilePos).getBlock(), mode.matchBlocks) || tilePos.distanceSq(pos) > reachSquared) {
                            compound.removeTag(mode.tagPosName);
                        }
                    }

                    boolean found = false;

                    long targetPosOld = 0;
                    if (compound.hasKey(mode.tagPosName)) {
                        targetPosOld = compound.getLong(mode.tagPosName);
                    }

                    BlockPos targetPos;
                    ArrayList<BlockPos> blacklist = new ArrayList<>();
                    int loop = 0;
                    do{
                        targetPos = getNearest(world, pos, blacklist, mode);
                        if(targetPos == null)
                            break;

                        switch (mode) {
                            case MEMORY:
                            case NEARBY:
                                StargateAbstractBaseTile gateTile = (StargateAbstractBaseTile) world.getTileEntity(targetPos);

                                if (gateTile == null || !gateTile.isMerged()){
                                    blacklist.add(targetPos);
                                    continue;
                                }

                                NBTTagList nearbyList = new NBTTagList();
                                int squaredGate = AunisConfig.stargateConfig.universeGateNearbyReach * AunisConfig.stargateConfig.universeGateNearbyReach;


                                compound.setBoolean("requireOnlySeven", true);
                                try {

                                    addrToBytes(gateTile.getDialedAddress(), compound, "dialedAddress");
                                    addrToBytes(((StargateUniverseBaseTile) gateTile).getAddressToDial(), compound, "toDialAddress");
                                    compound.setInteger("gateStatus", gateTile.getStargateState().id);

                                    for (Map.Entry<StargateAddress, StargatePos> entry : StargateNetwork.get(world).getMap().get(SymbolTypeEnum.UNIVERSE).entrySet()) {

                                        StargatePos stargatePos = entry.getValue();

                                        if (stargatePos.dimensionID != world.provider.getDimension())
                                            continue;

                                        if (stargatePos.gatePos.distanceSq(targetPos) > squaredGate)
                                            continue;

                                        if (stargatePos.gatePos.equals(targetPos))
                                            continue;

                                        StargateAbstractBaseTile targetGateTile = stargatePos.getTileEntity();

                                        if (!(targetGateTile instanceof StargateClassicBaseTile))
                                            continue;

                                        if (!targetGateTile.isMerged())
                                            continue;

                                        // get only universe gates in nearby
                                        if (!(targetGateTile instanceof StargateUniverseBaseTile) && mode.equals(UniverseDialerMode.NEARBY))
                                            continue;


                                        compound.setBoolean("serverSideEnabledFastDial", ((StargateClassicBaseTile) targetGateTile).getConfig().getOption(StargateClassicBaseTile.ConfigOptions.ALLOW_FAST_DIAL.id).getBooleanValue());

                                        NBTTagCompound entryCompound = entry.getKey().serializeNBT();

                                        entryCompound.setBoolean("requireOnlySeven", true);

                                        nearbyList.appendTag(entryCompound);
                                    }

                                    compound.setTag(UniverseDialerMode.NEARBY.tagListName, nearbyList);
                                    compound.setLong(mode.tagPosName, targetPos.toLong());
                                    found = true;
                                } catch (ConcurrentModificationException e) {
                                    Aunis.logger.error("Error while iterating nearby stargates occurred", e);

                                    if (entity instanceof EntityPlayer) {
                                        ((EntityPlayer) entity).sendStatusMessage(new TextComponentTranslation("item.aunis.universe_dialer.dialer_broke"), true);
                                    }
                                    broke(stack);

                                }
                                break;

                            case RINGS:
                                TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(targetPos);
                                if(ringsTile == null){
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

                            default:
                                break;
                        }

                        if (found) {
                            if (targetPosOld != 0 && !(BlockPos.fromLong(targetPosOld).equals(targetPos)))
                                AunisSoundHelper.playSoundEventClientSide(entity.getEntityWorld(), entity.getPosition(), SoundEventEnum.UNIVERSE_DIALER_CONNECTED);
                        }
                        loop++;
                    }while(!found && loop < 100);
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
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
        if (stack.getItemDamage() != UniverseDialerVariants.BROKEN.meta)
            stack.getCapability(ItemEndpointCapability.ENDPOINT_CAPABILITY, null).removeEndpoint();

        return super.onDroppedByPlayer(stack, player);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && player.getHeldItem(hand).getItemDamage() == UniverseDialerVariants.NORMAL.meta) {
            checkNBT(player.getHeldItem(hand));
            NBTTagCompound compound = player.getHeldItem(hand).getTagCompound();
            UniverseDialerMode mode = UniverseDialerMode.valueOf(compound.getByte("mode"));
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

                    switch (gateTile.getStargateState()) {
                        case IDLE:
                            int maxSymbols = SymbolUniverseEnum.getMaxSymbolsDisplay(selectedCompound.getBoolean("hasUpgrade"));
                            gateTile.dialAddress(new StargateAddress(selectedCompound), maxSymbols);
                            player.sendStatusMessage(new TextComponentTranslation("item.aunis.universe_dialer.dial_start"), true);
                            break;

                        case ENGAGED_INITIATING:
                            gateTile.attemptClose(StargateClosedReasonEnum.REQUESTED);
                            break;

                        case ENGAGED:
                            player.sendStatusMessage(new TextComponentTranslation("tile.aunis.dhd_block.incoming_wormhole_warn"), true);
                            break;

                        default:
                            if (gateTile.getStargateState() == EnumStargateState.DIALING) {
                                if(gateTile.abortDialingSequence()) {
                                    player.sendStatusMessage(new TextComponentTranslation("item.aunis.universe_dialer.aborting"), true);
                                    break;
                                }
                            }
                            player.sendStatusMessage(new TextComponentTranslation("item.aunis.universe_dialer.gate_busy"), true);
                            break;
                    }

                    break;

                case RINGS:
                    TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(linkedPos);
                    if(ringsTile == null) break;
                    ringsTile.attemptTransportTo(new TransportRings(selectedCompound).getAddress(SymbolTypeTransportRingsEnum.GOAULD), 5).sendMessageIfFailed(player);

                    break;

                case OC:
                    ItemOCMessage message = new ItemOCMessage(selectedCompound);
                    Aunis.logger.debug("Sending OC message: " + message.toString());
                    Aunis.ocWrapper.sendWirelessPacketPlayer(player, player.getHeldItem(hand), message.address, message.port, message.getData());
                    break;
            }
        }

        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void setCustomModelLocation() {
        ModelResourceLocation modelLocation = new ModelResourceLocation(this.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, modelLocation);

        CustomModelItemInterface.super.setCustomModelLocation();
        ModelBakery.registerItemVariants(AunisItems.UNIVERSE_DIALER, modelLocation);
        ModelLoader.setCustomMeshDefinition(AunisItems.UNIVERSE_DIALER, stack -> modelLocation);
    }

    public static void broke(ItemStack stack) {
        if (stack.getItem() == AunisItems.UNIVERSE_DIALER) {
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

    public static interface ChangeMessage {
        public void change(ItemOCMessage message);
    }
}
