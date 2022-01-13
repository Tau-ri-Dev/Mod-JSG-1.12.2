package mrjake.aunis.item;

import mrjake.aunis.block.zpm.ZPMBlock;
import mrjake.aunis.capability.CapacitorCapabilityProvider;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.stargate.power.StargateItemEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class ZPMItemBlock extends ItemBlock {

	public ZPMItemBlock(Block block) {
		super(block);
		
		setRegistryName(ZPMBlock.BLOCK_NAME);
		setMaxStackSize(1);
		setHasSubtypes(true);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			items.add(new ItemStack(this));
			
			ItemStack stack = new ItemStack(this);
			StargateItemEnergyStorage energyStorage = (StargateItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
			energyStorage.setEnergyStored(AunisConfig.powerConfig.zpmEnergyStorage);
			items.add(stack);
		}
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
		IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		
		String energy = String.format("%,d", energyStorage.getEnergyStored());
		String capacity = String.format("%,d", AunisConfig.powerConfig.zpmEnergyStorage);
		
		tooltip.add(energy + " / " + capacity + " RF");
		
		String energyPercent = String.format("%.2f", energyStorage.getEnergyStored()/(float)AunisConfig.powerConfig.zpmEnergyStorage * 100) + " %";
		tooltip.add(energyPercent);
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new CapacitorCapabilityProvider(stack, nbt);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);

		return 1 - (energyStorage.getEnergyStored() / (double)AunisConfig.powerConfig.zpmEnergyStorage);
	}
}
