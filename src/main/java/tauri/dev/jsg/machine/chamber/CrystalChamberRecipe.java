package tauri.dev.jsg.machine.chamber;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.item.JSGItems;

public abstract class CrystalChamberRecipe {
    public abstract int getWorkingTime();

    public abstract FluidStack getSubFluidStack();

    public abstract ItemStack getResult();

    public abstract int getEnergyPerTick();

    public int getNeededSeeds() {
        return 1;
    }

    public boolean isOk(int energyStored, FluidStack fluidStored, ItemStack seeds) {
        if (energyStored < getEnergyPerTick()) return false;
        if (!(fluidStored.isFluidEqual(getSubFluidStack()))) return false;
        if (fluidStored.amount < getSubFluidStack().amount) return false;
        if (seeds.getItem() != JSGItems.CRYSTAL_SEED) return false;
        return seeds.getCount() >= getNeededSeeds();
    }
}
