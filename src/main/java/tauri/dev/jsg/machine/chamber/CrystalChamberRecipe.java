package tauri.dev.jsg.machine.chamber;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.config.craftings.CraftingConfig;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.machine.AbstractMachineRecipe;

public abstract class CrystalChamberRecipe extends AbstractMachineRecipe {
    public static final String ID = "Crystal Chamber";

    public abstract FluidStack getSubFluidStack();

    public abstract ItemStack getResult();

    public int getNeededSeeds() {
        return 1;
    }

    public boolean isOk(int energyStored, FluidStack fluidStored, ItemStack seeds) {
        if(isDisabled()) return false;

        if (energyStored < (getEnergyPerTick() * (getWorkingTime() / 2))) return false;
        if (!(fluidStored.isFluidEqual(getSubFluidStack()))) return false;
        if (fluidStored.amount < getSubFluidStack().amount) return false;
        if (seeds.getItem() != JSGItems.CRYSTAL_SEED) return false;
        return seeds.getCount() >= getNeededSeeds();
    }

    public boolean isDisabled(){
        return CraftingConfig.isDisabled(ID, getResult().getItem().getRegistryName());
    }
}
