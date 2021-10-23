package mrjake.aunis.item.gdo;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.capability.endpoint.ItemEndpointCapability;
import mrjake.aunis.capability.endpoint.ItemEndpointInterface;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.GuiSendCode;
import mrjake.aunis.item.renderer.CustomModel;
import mrjake.aunis.item.renderer.CustomModelItemInterface;
import mrjake.aunis.stargate.EnumStargateState;
import mrjake.aunis.stargate.network.StargateNetwork;
import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GDOItem extends Item implements CustomModelItemInterface {

    public static final String ITEM_NAME = "gdo";

    public GDOItem() {
        setRegistryName(new ResourceLocation(Aunis.ModID, ITEM_NAME));
        setUnlocalizedName(Aunis.ModID + "." + ITEM_NAME);

        setCreativeTab(Aunis.aunisCreativeTab);
        //setMaxStackSize(1);
    }

    // TODO replace with capabilities. If item will have NBT like "display:Name" it will not init custom NBT! -- slava110
    // MrJake: Capabilities are meh in 1.12. Hope they've fixed them in 1.16.
    // matousss: lmao
    // MineDragonCZ was here
    private static void checkNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            initNBT(stack);
        }
    }

    private static void initNBT(ItemStack stack) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setByte("mode", GDOMode.CODE_SENDER.id);
        compound.setByte("selected", (byte) 0);
        compound.setTag("saved", new NBTTagList());

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
        return new GDOTEISR();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new GDOCapabilityProvider();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote) {
            checkNBT(stack);
            NBTTagCompound compound = stack.getTagCompound();

            if (world.getTotalWorldTime() % 20 == 0 && isSelected) {
                BlockPos pos = entity.getPosition();

                int reachSquared = AunisConfig.stargateConfig.universeDialerReach * AunisConfig.stargateConfig.universeDialerReach * 2;
                GDOMode mode = GDOMode.valueOf(compound.getByte("mode"));

                if (mode.linkable) {
                    if (compound.hasKey(mode.tagPosName)) {
                        BlockPos tilePos = BlockPos.fromLong(compound.getLong(mode.tagPosName));

                        if (world.getTileEntity(tilePos) == null || !(world.getTileEntity(tilePos) instanceof StargateAbstractBaseTile) || tilePos.distanceSq(pos) > reachSquared) {
                            compound.removeTag(mode.tagPosName);
                        }
                    } else {
                        boolean found = false;

                        for (BlockPos targetPos : BlockPos.getAllInBoxMutable(pos.add(-10, -10, -10), pos.add(10, 10, 10))) {
                            if (world.getTileEntity(targetPos) instanceof StargateAbstractBaseTile) {
                                switch (mode) {
                                    case CODE_SENDER:
                                    case OC:
                                        StargateAbstractBaseTile gateTile = (StargateAbstractBaseTile) world.getTileEntity(targetPos);

                                        if (!gateTile.isMerged())
                                            continue;

                                        compound.setLong(mode.tagPosName, targetPos.toLong());
                                        found = true;
                                        break;

                                    default:
                                        break;
                                }
                            }

                            if (found)
                                break;
                        }
                    }
                }
            }

            // Server side
            ItemEndpointInterface endpointStack = stack.getCapability(ItemEndpointCapability.ENDPOINT_CAPABILITY, null);
            endpointStack.checkAndUpdateEndpoint(world.getTotalWorldTime());
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
        stack.getCapability(ItemEndpointCapability.ENDPOINT_CAPABILITY, null).removeEndpoint();

        return super.onDroppedByPlayer(stack, player);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            checkNBT(player.getHeldItem(hand));
            NBTTagCompound compound = player.getHeldItem(hand).getTagCompound();
            GDOMode mode = GDOMode.valueOf(compound.getByte("mode"));
            int selected = compound.getByte("selected");

            if (mode.linkable && !compound.hasKey(mode.tagPosName))
                return super.onItemRightClick(world, player, hand);

            BlockPos linkedPos = BlockPos.fromLong(compound.getLong(mode.tagPosName));

            switch (mode) {
                case CODE_SENDER:
//                    int irisCode = compound.getInteger("irisCode");
//                    StargateAbstractBaseTile gateTile = (StargateAbstractBaseTile) world.getTileEntity(linkedPos);
//                    if (gateTile.getStargateState() == EnumStargateState.ENGAGED_INITIATING) {
//                        TileEntity te = StargateNetwork.get(world).getStargate(gateTile.getDialedAddress()).getTileEntity();
//                        if (!(te instanceof StargateClassicBaseTile)) break;
//                        ((StargateClassicBaseTile) te).receiveIrisCode(player, irisCode);
//                    }

                    /** moved to {@link mrjake.aunis.event.InputHandlerClient}*/
                    break;
                default:
                    break;
            }
        }

        return super.onItemRightClick(world, player, hand);
    }
}
