package tauri.dev.jsg.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public abstract class SidedTileEntity extends TileEntity implements ISidedInventory {
    @Override
    @ParametersAreNonnullByDefault
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return getSlotsForSideAsList(direction).contains(index) && isItemValidForSlot(index, itemStackIn);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return getSlotsForSideAsList(direction).contains(index);
    }

    public List<Integer> getSlotsForSideAsList(EnumFacing side){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < getSlotsForFace(side).length; i++){
            list.add(getSlotsForFace(side)[i]);
        }
        return list;
    }

    public abstract ItemStackHandler getItemStackHandler();

    @Override
    public int getSizeInventory() {
        return getItemStackHandler().getSlots();
    }

    public List<ItemStack> getStacks() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < getSizeInventory(); i++)
            list.add(getItemStackHandler().getStackInSlot(i));
        return list;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : getStacks()) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int index) {
        return getItemStackHandler().getStackInSlot(index);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(getStacks(), index, count);
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(getStacks(), index);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setInventorySlotContents(int index, ItemStack stack) {
        getItemStackHandler().setStackInSlot(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openInventory(EntityPlayer player) {

    }

    @Override
    @ParametersAreNonnullByDefault
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return getItemStackHandler().isItemValid(index, stack);
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        getStacks().forEach(stack -> stack.setCount(0));
    }

    @Override
    @Nonnull
    public String getName() {
        return world.getBlockState(pos).getBlock().getUnlocalizedName();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TextComponentString(world.getBlockState(pos).getBlock().getLocalizedName());
    }
}
