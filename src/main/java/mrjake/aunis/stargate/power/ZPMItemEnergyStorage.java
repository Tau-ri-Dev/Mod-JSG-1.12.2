package mrjake.aunis.stargate.power;

import mrjake.aunis.config.AunisConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

public class ZPMItemEnergyStorage extends StargateItemEnergyStorage {
    public ZPMItemEnergyStorage(ItemStack stack) {
        super(stack);
    }

    @Override
    public int getMaxEnergyStored() {
        return AunisConfig.powerConfig.zpmEnergyStorage;
    }
}
