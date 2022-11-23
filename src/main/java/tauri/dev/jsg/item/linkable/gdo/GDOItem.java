package tauri.dev.jsg.item.linkable.gdo;

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
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.util.LinkingHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
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
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;

// It would be better to make gdo and uni dialer share code... -- matousss
public class GDOItem extends Item implements CustomModelItemInterface {
    public static final String ITEM_NAME = "gdo";
    private CustomModel customModel;

    public GDOItem() {
        setRegistryName(new ResourceLocation(JSG.MOD_ID, ITEM_NAME));
        setUnlocalizedName(JSG.MOD_ID + "." + ITEM_NAME);

        setCreativeTab(JSGCreativeTabsHandler.JSG_TOOLS_CREATIVE_TAB);
    }

    // TODO replace with capabilities. If item will have NBT like "display:Name" it will not init custom NBT! -- slava110
    // MrJake: Capabilities are meh in 1.12. Hope they've fixed them in 1.16.
    // matousss: lmao
    // MineDragonCZ was here
    // so was Fredyman_95
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

    public static boolean isLinked(ItemStack itemStack) {
        if (itemStack.getItem() == JSGItems.GDO) {
            if (itemStack.hasTagCompound()) {
                return itemStack.getTagCompound().hasKey("linkedGate");
            }
        }
        return false;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);
            initNBT(stack);
            items.add(stack);
        }
    }

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
        return new LinkAbleCapabilityProvider();
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

                int reachSquared = tauri.dev.jsg.config.JSGConfig.stargateConfig.universeDialerReach * tauri.dev.jsg.config.JSGConfig.stargateConfig.universeDialerReach;
                GDOMode mode = GDOMode.valueOf(compound.getByte("mode"));

                if (mode.linkable) {
                    if (compound.hasKey(mode.tagPosName)) {
                        BlockPos tilePos = BlockPos.fromLong(compound.getLong(mode.tagPosName));

                        if (world.getTileEntity(tilePos) == null || !(world.getTileEntity(tilePos) instanceof StargateAbstractBaseTile) || tilePos.distanceSq(pos) > reachSquared) {
                            compound.removeTag(mode.tagPosName);
                        }
                    } else {
                        boolean found = false;
                        BlockPos targetPos;
                        ArrayList<BlockPos> blacklist = new ArrayList<>();
                        int loop = 0;
                        do {

                            targetPos = getNearest(world, pos, blacklist);
                            if (targetPos == null)
                                break;

                            if (world.getTileEntity(targetPos) instanceof StargateAbstractBaseTile) {
                                switch (mode) {
                                    case CODE_SENDER:
                                    case OC:
                                        StargateAbstractBaseTile gateTile = (StargateAbstractBaseTile) world.getTileEntity(targetPos);

                                        if (gateTile == null || !gateTile.isMerged()) {
                                            blacklist.add(targetPos);
                                            continue;
                                        }

                                        compound.setLong(mode.tagPosName, targetPos.toLong());
                                        found = true;
                                        break;

                                    default:
                                        break;
                                }
                            }
                            loop++;
                        } while (!found && loop < 100);
                    }
                }
            }

            // Server side
            ItemEndpointInterface endpointStack = stack.getCapability(ItemEndpointCapability.ENDPOINT_CAPABILITY, null);
            endpointStack.checkAndUpdateEndpoint(world.getTotalWorldTime());
        }
    }

    public BlockPos getNearest(World world, BlockPos pos, ArrayList<BlockPos> blacklist) {
        return LinkingHelper.findClosestPos(world, pos, new BlockPos(tauri.dev.jsg.config.JSGConfig.stargateConfig.universeDialerReach, 10, JSGConfig.stargateConfig.universeDialerReach), JSGBlocks.STARGATE_BASE_BLOCKS, blacklist);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
        stack.getCapability(ItemEndpointCapability.ENDPOINT_CAPABILITY, null).removeEndpoint();

        return super.onDroppedByPlayer(stack, player);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (!world.isRemote) {
            checkNBT(player.getHeldItem(hand));
            NBTTagCompound compound = player.getHeldItem(hand).getTagCompound();
            GDOMode mode = GDOMode.valueOf(compound.getByte("mode"));
            int selected = compound.getByte("selected");

            if (mode.linkable && !compound.hasKey(mode.tagPosName))
                return super.onItemRightClick(world, player, hand);

            if (mode == GDOMode.OC) {
                NBTTagList tagList = compound.getTagList(mode.tagListName, Constants.NBT.TAG_COMPOUND);
                NBTTagCompound selectedCompound = tagList.getCompoundTagAt(selected);
                ItemOCMessage message = new ItemOCMessage(selectedCompound);
                JSG.logger.debug("Sending OC message: " + message.toString());
                JSG.ocWrapper.sendWirelessPacketPlayer(player, player.getHeldItem(hand), message.address, message.port, message.getData());
            }
        }

        return super.onItemRightClick(world, player, hand);
    }
}
