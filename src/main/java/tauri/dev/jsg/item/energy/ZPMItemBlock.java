package tauri.dev.jsg.item.energy;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tauri.dev.jsg.block.energy.ZPMBlock;
import tauri.dev.jsg.block.energy.ZPMBlockCreative;
import tauri.dev.jsg.capability.CapabilityEnergyZPM;
import tauri.dev.jsg.capability.ZPMItemCapabilityProvider;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.power.zpm.IEnergyStorageZPM;
import tauri.dev.jsg.power.zpm.ZPMItemEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ZPMItemBlock extends ItemBlock {

    public ZPMItemBlock(Block block, boolean creative) {
        super(block);

        setRegistryName(creative ? ZPMBlockCreative.BLOCK_NAME : ZPMBlock.BLOCK_NAME);
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            ItemStack stack = new ItemStack(this);
            ZPMItemEnergyStorage energyStorage = (ZPMItemEnergyStorage) stack.getCapability(CapabilityEnergyZPM.ENERGY, null);
            energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
            items.add(stack);
        }
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        IEnergyStorageZPM energyStorage = stack.getCapability(CapabilityEnergyZPM.ENERGY, null);

        String energy = String.format("%,d", energyStorage.getEnergyStored());
        String capacity = String.format("%,d", energyStorage.getMaxEnergyStored());

        tooltip.add(energy + " / " + capacity + " RF");

        String energyPercent = String.format("%.2f", energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored() * 100) + " %";
        tooltip.add(energyPercent);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ZPMItemCapabilityProvider(stack, nbt, (long) JSGConfig.powerConfig.zpmCapacity, false);
    }

    @Override
    public boolean showDurabilityBar(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        IEnergyStorageZPM energyStorage = stack.getCapability(CapabilityEnergyZPM.ENERGY, null);

        return 1 - (energyStorage.getEnergyStored() / (double) energyStorage.getMaxEnergyStored());
    }
}
