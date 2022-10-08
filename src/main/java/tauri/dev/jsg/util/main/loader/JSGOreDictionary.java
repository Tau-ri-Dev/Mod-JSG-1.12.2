package tauri.dev.jsg.util.main.loader;

import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.item.JSGItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public class JSGOreDictionary {
    public static void registerOreDictionary(){
        regODOre("oreNaquadahRaw", JSGBlocks.ORE_NAQUADAH_BLOCK);
        regODOre("oreNaquadahRaw", JSGBlocks.ORE_NAQUADAH_BLOCK_STONE);
        regODOre("gemNaquadahRaw", JSGItems.NAQUADAH_SHARD);
        regODOre("ingotNaquadahRaw", JSGItems.NAQUADAH_ALLOY_RAW);
        regODOre("ingotNaquadahRefined", JSGItems.NAQUADAH_ALLOY);
        regODOre("blockNaquadahRefined", JSGBlocks.NAQUADAH_BLOCK);
        regODOre("blockNaquadahRaw", JSGBlocks.NAQUADAH_BLOCK_RAW);

        regODOre("oreTrinium", JSGBlocks.ORE_TRINIUM_BLOCK);
        regODOre("oreTitanium", JSGBlocks.ORE_TITANIUM_BLOCK);
        regODOre("ingotTrinium", JSGItems.TRINIUM_INGOT);
        regODOre("ingotTitanium", JSGItems.TITANIUM_INGOT);
        regODOre("blockTrinium", JSGBlocks.TRINIUM_BLOCK);
        regODOre("blockTitanium", JSGBlocks.TITANIUM_BLOCK);
    }

    public static void regODOre(String name, Item ore){
        OreDictionary.registerOre(name, ore);
    }
    public static void regODOre(String name, Block ore){
        OreDictionary.registerOre(name, ore);
    }

}
