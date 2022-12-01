package tauri.dev.jsg.item.machine;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tauri.dev.jsg.capability.ItemCapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MachineItemBlock extends ItemBlock {

    private final int maxEnergy;

    public MachineItemBlock(Block block, String name, int maxEnergy) {
        super(block);
        this.maxEnergy = maxEnergy;
        setRegistryName(name);
        setHasSubtypes(true);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ItemCapabilityProvider(stack, nbt, maxEnergy);
    }
}
