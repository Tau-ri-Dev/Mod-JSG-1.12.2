package tauri.dev.jsg.machine.orewashing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.config.craftings.CraftingConfig;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.machine.AbstractMachineRecipe;

public abstract class OreWashingRecipe extends AbstractMachineRecipe {
    public static final String ID = "Ore Washing Machine";

    public abstract FluidStack getSubFluidStack();

    public abstract ItemStack getResult();

    public abstract ItemStack getItemNeeded();

    public boolean isOk(int energyStored, FluidStack fluidStored, ItemStack itemIn) {
        if(isDisabled()) return false;

        if (energyStored < getEnergyPerTick()) return false;
        if (!(fluidStored.isFluidEqual(getSubFluidStack()))) return false;
        if (fluidStored.amount < getSubFluidStack().amount) return false;
        if (!itemIn.isItemEqualIgnoreDurability(getItemNeeded())) return false;
        return itemIn.getCount() >= getItemNeeded().getCount();
    }

    public boolean isDisabled(){
        return CraftingConfig.isDisabled(ID, getResult().getItem().getRegistryName());
    }
}
