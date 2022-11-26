package tauri.dev.jsg.item.machine;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tauri.dev.jsg.block.machine.PCBFabricatorBlock;
import tauri.dev.jsg.capability.ItemCapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PCBFabricatorItemBlock extends ItemBlock {

    public PCBFabricatorItemBlock(Block block) {
        super(block);

        setRegistryName(PCBFabricatorBlock.BLOCK_NAME);
        setHasSubtypes(true);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ItemCapabilityProvider(stack, nbt, PCBFabricatorBlock.MAX_ENERGY);
    }
}
