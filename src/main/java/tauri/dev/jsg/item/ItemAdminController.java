package tauri.dev.jsg.item;

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
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.packet.AdminControllerGuiOpenToClient;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicMemberTile;
import tauri.dev.jsg.util.RayTraceHelper;

import javax.annotation.Nonnull;

public class ItemAdminController extends Item {
    public static final String ITEM_NAME = "admin_controller";

    public ItemAdminController() {
        setRegistryName(JSG.MOD_ID + ":" + ITEM_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + ITEM_NAME);

        setCreativeTab(JSGCreativeTabsHandler.JSG_TOOLS_CREATIVE_TAB);
    }

    @Override
    public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void onUpdate(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote) {
            if(world.getTotalWorldTime() % 5 != 0) return;
            if(!(entity instanceof EntityPlayerMP)) return;

            TileEntity te = RayTraceHelper.rayTraceTileEntity((EntityPlayerMP) entity, 20);

            if (te instanceof StargateClassicMemberTile) {
                te = ((StargateClassicMemberTile) te).getBaseTile(world);
            }

            if (!(te instanceof StargateClassicBaseTile)) return;
            StargateClassicBaseTile gateTile = (StargateClassicBaseTile) te;

            NBTTagCompound compound = stack.getTagCompound();
            if(compound == null) compound = new NBTTagCompound();

            compound.setTag("gateNBT", gateTile.writeToNBT(new NBTTagCompound()));

            stack.setTagCompound(compound);
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (!world.isRemote) {
            TileEntity te = RayTraceHelper.rayTraceTileEntity(player, 20);

            if (te instanceof StargateClassicMemberTile) {
                te = ((StargateClassicMemberTile) te).getBaseTile(world);
            }

            if (te instanceof StargateClassicBaseTile && player instanceof EntityPlayerMP) {
                JSGPacketHandler.INSTANCE.sendTo(new AdminControllerGuiOpenToClient(te.getPos(), ((StargateClassicBaseTile) te).getNetwork()), (EntityPlayerMP) player);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
