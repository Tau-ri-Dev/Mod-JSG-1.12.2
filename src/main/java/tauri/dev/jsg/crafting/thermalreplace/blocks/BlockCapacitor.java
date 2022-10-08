package tauri.dev.jsg.crafting.thermalreplace.blocks;

import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.crafting.thermalreplace.ThermalAbstractRecipe;
import tauri.dev.jsg.item.JSGItems;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class BlockCapacitor extends ThermalAbstractRecipe {

    // CONFIGURATION
    public static final int MINIMAL_SLOTS = 9;
    public static final int MAXIMAL_SLOTS = 9;
    public static final String NAME = "thermal_block_capacitor";
    public static final ItemStack OUTPUT_ITEM = new ItemStack(JSGBlocks.CAPACITOR_BLOCK);

    // items / patterns
    public HashMap<ItemStack, int[]> PATTERN_LIST = new HashMap<ItemStack, int[]>() {
        {
            put(new ItemStack(JSGItems.CRYSTAL_RED), new int[]{
                    0, 1, 0,
                    1, 0, 1,
                    0, 1, 0
            });
            put(new ItemStack(JSGBlocks.CAPACITOR_BLOCK_EMPTY), new int[]{
                    0, 0, 0,
                    0, 1, 0,
                    0, 0, 0
            });
        }
    };

    // REGISTER / CONSTRUCTOR
    public BlockCapacitor() {
        super(NAME, MINIMAL_SLOTS, MAXIMAL_SLOTS, OUTPUT_ITEM);
        setPatterns(PATTERN_LIST);
    }
}
