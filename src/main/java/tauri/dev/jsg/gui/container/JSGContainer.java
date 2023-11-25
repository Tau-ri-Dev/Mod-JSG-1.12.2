package tauri.dev.jsg.gui.container;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.block.JSGBlocks;

import javax.annotation.Nonnull;

public abstract class JSGContainer extends Container {
    public abstract World getWorld();

    public abstract BlockPos getPos();

    public abstract Block[] getAllowedBlocks();

    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return (JSGBlocks.isInBlocksArray(getWorld().getBlockState(getPos()).getBlock(), getAllowedBlocks()) && playerIn.getDistanceSq((double) getPos().getX() + 0.5D, (double) getPos().getY() + 0.5D, (double) getPos().getZ() + 0.5D) <= 128.0D);
    }
}
