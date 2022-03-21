package mrjake.aunis.crafting.thermalreplace.blocks;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.crafting.thermalreplace.ThermalAbstractRecipe;
import mrjake.aunis.item.AunisItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class BlockOrlinBase extends ThermalAbstractRecipe {

    // CONFIGURATION
    public static final int MINIMAL_SLOTS = 9;
    public static final int MAXIMAL_SLOTS = 9;
    public static final String NAME = "thermal_block_orlin_base";
    public static final ItemStack OUTPUT_ITEM = new ItemStack(AunisBlocks.STARGATE_ORLIN_BASE_BLOCK);

    // items / patterns
    public HashMap<ItemStack, int[]> PATTERN_LIST = new HashMap<ItemStack, int[]>() {
        {
            put(new ItemStack(Items.ENDER_PEARL), new int[]{
                    0, 0, 0,
                    0, 1, 0,
                    0, 0, 0
            });
            put(new ItemStack(Blocks.OBSIDIAN), new int[]{
                    1, 0, 1,
                    0, 0, 0,
                    1, 0, 1
            });
            put(new ItemStack(Items.IRON_INGOT), new int[]{
                    0, 1, 0,
                    1, 0, 1,
                    0, 1, 0
            });
        }
    };

    // REGISTER / CONSTRUCTOR
    public BlockOrlinBase() {
        super(NAME, MINIMAL_SLOTS, MAXIMAL_SLOTS, OUTPUT_ITEM);
        setPatterns(PATTERN_LIST);
    }
}
