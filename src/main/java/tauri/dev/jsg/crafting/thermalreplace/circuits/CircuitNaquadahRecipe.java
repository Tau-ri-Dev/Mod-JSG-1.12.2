package tauri.dev.jsg.crafting.thermalreplace.circuits;

import tauri.dev.jsg.crafting.thermalreplace.ThermalAbstractRecipe;
import tauri.dev.jsg.item.JSGItems;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class CircuitNaquadahRecipe extends ThermalAbstractRecipe {

    // CONFIGURATION
    public static final int MINIMAL_SLOTS = 9;
    public static final int MAXIMAL_SLOTS = 9;
    public static final String NAME = "thermal_circuit_naquadah";
    public static final ItemStack OUTPUT_ITEM = new ItemStack(JSGItems.CIRCUIT_CONTROL_NAQUADAH);

    // items / patterns
    public HashMap<ItemStack, int[]> PATTERN_LIST = new HashMap<ItemStack, int[]>() {
        {
            put(new ItemStack(JSGItems.NAQUADAH_ALLOY), new int[]{
                    0, 1, 0,
                    1, 0, 1,
                    0, 1, 0
            });
            put(new ItemStack(JSGItems.CIRCUIT_CONTROL_BASE), new int[]{
                    0, 0, 0,
                    0, 1, 0,
                    0, 0, 0
            });
        }
    };

    // REGISTER / CONSTRUCTOR
    public CircuitNaquadahRecipe() {
        super(NAME, MINIMAL_SLOTS, MAXIMAL_SLOTS, OUTPUT_ITEM);
        setPatterns(PATTERN_LIST);
    }
}
