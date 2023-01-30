package tauri.dev.jsg.item.energy;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tauri.dev.jsg.capability.CapabilityEnergyZPM;
import tauri.dev.jsg.capability.ZPMItemCapabilityProvider;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.power.zpm.ZPMItemEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class ZPMItemBlockCreative extends ZPMItemBlock {
    public ZPMItemBlockCreative(Block block) {
        super(block, true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);
            ZPMItemEnergyStorage energyStorage = (ZPMItemEnergyStorage) stack.getCapability(CapabilityEnergyZPM.ENERGY, null);
            if (energyStorage != null) {
                energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
            }
            items.add(stack);
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ZPMItemCapabilityProvider(stack, nbt, (long) JSGConfig.powerConfig.zpmCapacity, true);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
    }
}
