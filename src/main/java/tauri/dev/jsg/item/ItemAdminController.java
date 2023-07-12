package tauri.dev.jsg.item;

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
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.gui.admincontroller.GuiAdminController;
import tauri.dev.jsg.packet.GuiOpenToClient;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.stargate.network.StargateNetwork;
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

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (!world.isRemote) {
            TileEntity te = RayTraceHelper.rayTraceTileEntity(player);

            if (te instanceof StargateClassicMemberTile) {
                te = ((StargateClassicMemberTile) te).getBaseTile(world);
            }

            if (te instanceof StargateClassicBaseTile && player instanceof EntityPlayerMP) {
                ItemStack stack = player.getHeldItem(hand);
                NBTTagCompound compound = stack.getTagCompound();
                if(compound == null) compound = new NBTTagCompound();
                compound.setTag("sgNetwork", ((StargateClassicBaseTile) te).getNetwork().serializeNBT());
                compound.setLong("linkedGatePos", te.getPos().toLong());
                stack.setTagCompound(compound);

                JSGPacketHandler.INSTANCE.sendTo(new GuiOpenToClient(te.getPos(), GuiIdEnum.GUI_ADMIN_CONTROLLER.id), (EntityPlayerMP) player);
            }
        } else {
            ItemStack stack = player.getHeldItem(hand);
            NBTTagCompound compound = stack.getTagCompound();
            if (compound != null) {
                NBTTagCompound sgNetworkCompound = compound.getCompoundTag("sgNetwork");
                GuiAdminController.lastStargateNetwork = new StargateNetwork();
                GuiAdminController.lastStargateNetwork.deserializeNBT(sgNetworkCompound);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
