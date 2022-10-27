package tauri.dev.jsg.item.machine;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tauri.dev.jsg.block.machine.AssemblerBlock;
import tauri.dev.jsg.capability.ItemCapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssemblerItemBlock extends ItemBlock {

    public AssemblerItemBlock(Block block) {
        super(block);

        setRegistryName(AssemblerBlock.BLOCK_NAME);
        setHasSubtypes(true);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ItemCapabilityProvider(stack, nbt, AssemblerBlock.MAX_ENERGY);
    }
}
