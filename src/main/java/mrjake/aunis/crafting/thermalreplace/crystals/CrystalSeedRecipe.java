package mrjake.aunis.crafting.thermalreplace.crystals;

import mrjake.aunis.crafting.thermalreplace.ThermalAbstractRecipe;
import mrjake.aunis.item.AunisItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class CrystalSeedRecipe extends ThermalAbstractRecipe {

    // CONFIGURATION
    public static final int MINIMAL_SLOTS = 4;
    public static final int MAXIMAL_SLOTS = 9;
    public static final String NAME = "thermal_crystal_seed";
    public static final ItemStack OUTPUT_ITEM = new ItemStack(AunisItems.CRYSTAL_SEED);

    // items / patterns
    public HashMap<ItemStack, int[]> PATTERN_LIST = new HashMap<ItemStack, int[]>() {
        {
            put(new ItemStack(Items.QUARTZ), new int[]{
                    1, 1, 0,
                    1, 0, 0,
                    0, 0, 0
            });
        }
    };

    // REGISTER / CONSTRUCTOR
    public CrystalSeedRecipe() {
        super(NAME, MINIMAL_SLOTS, MAXIMAL_SLOTS, OUTPUT_ITEM);
        setPatterns(PATTERN_LIST);
    }
}
