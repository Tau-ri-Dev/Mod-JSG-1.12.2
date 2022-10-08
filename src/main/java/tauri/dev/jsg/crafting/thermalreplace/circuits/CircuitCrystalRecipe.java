package tauri.dev.jsg.crafting.thermalreplace.circuits;

import tauri.dev.jsg.crafting.thermalreplace.ThermalAbstractRecipe;
import tauri.dev.jsg.item.JSGItems;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class CircuitCrystalRecipe extends ThermalAbstractRecipe {

    // CONFIGURATION
    public static final int MINIMAL_SLOTS = 9;
    public static final int MAXIMAL_SLOTS = 9;
    public static final String NAME = "thermal_circuit_crystal";
    public static final ItemStack OUTPUT_ITEM = new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL);

    // items / patterns
    public HashMap<ItemStack, int[]> PATTERN_LIST = new HashMap<ItemStack, int[]>() {
        {
            put(new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), new int[]{
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
    public CircuitCrystalRecipe() {
        super(NAME, MINIMAL_SLOTS, MAXIMAL_SLOTS, OUTPUT_ITEM);
        setPatterns(PATTERN_LIST);
    }
}
