package tauri.dev.jsg.item.machine;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.machine.AssemblerBlock;
import tauri.dev.jsg.capability.ItemCapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class StargateAssemblerItemBlock extends ItemBlock {

    public StargateAssemblerItemBlock(Block block) {
        super(block);

        setRegistryName(AssemblerBlock.BLOCK_NAME);
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ItemCapabilityProvider(stack, nbt, AssemblerBlock.MAX_ENERGY);
    }
}
