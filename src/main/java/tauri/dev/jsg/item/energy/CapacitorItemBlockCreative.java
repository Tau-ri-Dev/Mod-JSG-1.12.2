package tauri.dev.jsg.item.energy;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.capability.ItemCapabilityProvider;
import tauri.dev.jsg.config.JSGConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CapacitorItemBlockCreative extends CapacitorItemBlock {
    public CapacitorItemBlockCreative(Block block) {
        super(block, true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {}

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ItemCapabilityProvider(stack, nbt, JSGConfig.Stargate.power.stargateEnergyStorage / 4, true);
    }

    @Override
    public boolean showDurabilityBar(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 0;
    }
}
