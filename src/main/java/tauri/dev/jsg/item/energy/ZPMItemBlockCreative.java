package tauri.dev.jsg.item.energy;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import tauri.dev.jsg.stargate.power.StargateItemEnergyStorage;

import java.util.List;

public class ZPMItemBlockCreative extends ZPMItemBlock {
    public ZPMItemBlockCreative(Block block) {
        super(block, true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);
            StargateItemEnergyStorage energyStorage = (StargateItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage != null) {
                energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
            }
            items.add(stack);
        }
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
    }
}
