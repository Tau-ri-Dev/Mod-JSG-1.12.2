package mrjake.aunis.util.main.loader;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.item.AunisItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public class AunisOreDictionary {
    public static void registerOreDictionary(){
        regODOre("oreNaquadahRaw", AunisBlocks.ORE_NAQUADAH_BLOCK);
        regODOre("oreNaquadahRaw", AunisBlocks.ORE_NAQUADAH_BLOCK_STONE);
        regODOre("gemNaquadahRaw", AunisItems.NAQUADAH_SHARD);
        regODOre("ingotNaquadahRaw", AunisItems.NAQUADAH_ALLOY_RAW);
        regODOre("ingotNaquadahRefined", AunisItems.NAQUADAH_ALLOY);
        regODOre("blockNaquadahRefined", AunisBlocks.NAQUADAH_BLOCK);
        regODOre("blockNaquadahRaw", AunisBlocks.NAQUADAH_BLOCK_RAW);

        regODOre("oreTrinium", AunisBlocks.ORE_TRINIUM_BLOCK);
        regODOre("oreTitanium", AunisBlocks.ORE_TITANIUM_BLOCK);
        regODOre("ingotTrinium", AunisItems.TRINIUM_INGOT);
        regODOre("ingotTitanium", AunisItems.TITANIUM_INGOT);
        regODOre("blockTrinium", AunisBlocks.TRINIUM_BLOCK);
        regODOre("blockTitanium", AunisBlocks.TITANIUM_BLOCK);
    }

    public static void regODOre(String name, Item ore){
        OreDictionary.registerOre(name, ore);
    }
    public static void regODOre(String name, Block ore){
        OreDictionary.registerOre(name, ore);
    }

}
