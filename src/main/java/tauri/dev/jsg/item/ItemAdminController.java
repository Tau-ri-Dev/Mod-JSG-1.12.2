package tauri.dev.jsg.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.packet.AdminControllerGuiOpenToClient;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicMemberTile;
import tauri.dev.jsg.util.LinkingHelper;
import tauri.dev.jsg.util.RayTraceHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemAdminController extends Item {
    public static final String ITEM_NAME = "admin_controller";

    public ItemAdminController() {
        setRegistryName(JSG.MOD_ID + ":" + ITEM_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + ITEM_NAME);

        setCreativeTab(JSGCreativeTabsHandler.JSG_TOOLS_CREATIVE_TAB);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(JSG.getInProgress());
    }

    @Override
    public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void onUpdate(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote) {
            if (world.getTotalWorldTime() % 5 != 0) return;
            if (!(entity instanceof EntityPlayerMP)) return;

            // Get compound - if null, gate is probably not even linked
            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null) return;

            // Check if gate is linked
            if (!compound.hasKey("linkedGatePos")) return;

            // get and check if linked Tile is not null and is classic base tile
            TileEntity te = world.getTileEntity(BlockPos.fromLong(compound.getLong("linkedGatePos")));
            if (!(te instanceof StargateClassicBaseTile)) return;

            // cast and set NBT of gate tile to NBT of controller
            StargateClassicBaseTile gateTile = (StargateClassicBaseTile) te;
            compound.setTag("gateNBT", gateTile.writeToNBT(new NBTTagCompound()));
            compound.setTag("sgNetwork", gateTile.getNetwork().serializeNBT());
            stack.setTagCompound(compound);
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (!world.isRemote) {
            TileEntity te = RayTraceHelper.rayTraceTileEntity(player, 20);

            if (te instanceof StargateClassicMemberTile) {
                // If member, get base block
                te = ((StargateClassicMemberTile) te).getBaseTile(world);
            }

            if (!(te instanceof StargateClassicBaseTile)) {
                te = LinkingHelper.findClosestTile(world, player.getPosition(), JSGBlocks.STARGATE_BASE_BLOCKS, StargateClassicBaseTile.class, 20, 20);
            }

            if (te instanceof StargateClassicBaseTile && player instanceof EntityPlayerMP) {

                // Set linked gate for updating
                NBTTagCompound compound = player.getHeldItem(hand).getTagCompound();
                if (compound == null) compound = new NBTTagCompound();
                compound.setLong("linkedGatePos", te.getPos().toLong());
                player.getHeldItem(hand).setTagCompound(compound);

                // Open GUI for the player
                JSGPacketHandler.INSTANCE.sendTo(new AdminControllerGuiOpenToClient(te.getPos(), ((StargateClassicBaseTile) te).getNetwork()), (EntityPlayerMP) player);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
