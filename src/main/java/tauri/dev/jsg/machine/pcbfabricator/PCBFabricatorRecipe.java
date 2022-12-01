package tauri.dev.jsg.machine.pcbfabricator;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.config.craftings.CraftingConfig;
import tauri.dev.jsg.machine.AbstractMachineRecipe;

import java.util.ArrayList;
import java.util.Objects;

public abstract class PCBFabricatorRecipe extends AbstractMachineRecipe {
    public static final String ID = "PCB Fabricator";

    public abstract FluidStack getSubFluidStack();

    public abstract ArrayList<ItemStack> getPattern();

    public abstract ItemStack getResult();

    public abstract float[] getBeamColors();

    public boolean isOk(int energyStored, FluidStack fluidStored, ArrayList<ItemStack> stacks) {
        if(isDisabled()) return false;

        if (energyStored < getEnergyPerTick()) return false;
        if (!(fluidStored.isFluidEqual(getSubFluidStack()))) return false;
        if (fluidStored.amount < getSubFluidStack().amount) return false;
        int i = 0;
        for (ItemStack s : getPattern()) {
            if (s != null) {
                if (stacks.get(i).isEmpty()) return false;
                if (stacks.get(i).getItem() != s.getItem()) return false;
                if (stacks.get(i).getCount() < s.getCount()) return false;
            } else {
                if (!(stacks.get(i).isEmpty())) return false;
            }
            i++;
        }
        return true;
    }

    public boolean isDisabled(){
        return CraftingConfig.isDisabled(ID, getResult().getItem().getRegistryName());
    }
}
