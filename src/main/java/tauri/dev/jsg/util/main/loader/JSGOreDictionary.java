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
        regODOre("impureNaquadahRaw", JSGItems.NAQUADAH_ORE_IMPURE);
        regODOre("purifiedNaquadahRaw", JSGItems.NAQUADAH_ORE_PURIFIED);
        regODOre("gemNaquadahRaw", JSGItems.NAQUADAH_ORE_RAW);
        regODOre("ingotNaquadahRaw", JSGItems.NAQUADAH_ALLOY_RAW);
        regODOre("blockNaquadahRaw", JSGBlocks.NAQUADAH_BLOCK_RAW);
        regODOre("plateNaquadahRaq", JSGItems.PLATE_NAQUADAH_RAW);
        regODOre("nuggetNaquadahRaw", JSGItems.NAQUADAH_RAW_NUGGET);
        regODOre("dustNaquadahRaw", JSGItems.NAQUADAH_RAW_DUST);

        regODOre("ingotNaquadahRefined", JSGItems.NAQUADAH_ALLOY);
        regODOre("blockNaquadahRefined", JSGBlocks.NAQUADAH_BLOCK);
        regODOre("gearNaquadahRefined", JSGItems.GEAR_NAQUADAH);
        regODOre("plateNaquadahRefined", JSGItems.PLATE_NAQUADAH);
        regODOre("nuggetNaquadahRefined", JSGItems.NAQUADAH_NUGGET);
        regODOre("dustNaquadahRefined", JSGItems.NAQUADAH_DUST);

        regODOre("oreTrinium", JSGBlocks.ORE_TRINIUM_BLOCK);
        regODOre("impureTrinium", JSGItems.TRINIUM_ORE_IMPURE);
        regODOre("purifiedTrinium", JSGItems.TRINIUM_ORE_PURIFIED);
        regODOre("gemTrinium", JSGItems.TRINIUM_ORE_RAW);
        regODOre("ingotTrinium", JSGItems.TRINIUM_INGOT);
        regODOre("blockTrinium", JSGBlocks.TRINIUM_BLOCK);
        regODOre("gearTrinium", JSGItems.GEAR_TRINIUM);
        regODOre("plateTrinium", JSGItems.PLATE_TRINIUM);
        regODOre("nuggetTrinium", JSGItems.TRINIUM_NUGGET);
        regODOre("dustTrinium", JSGItems.TRINIUM_DUST);

        regODOre("oreTitanium", JSGBlocks.ORE_TITANIUM_BLOCK);
        regODOre("impureTitanium", JSGItems.TITANIUM_ORE_IMPURE);
        regODOre("purifiedTitanium", JSGItems.TITANIUM_ORE_PURIFIED);
        regODOre("gemTitanium", JSGItems.TITANIUM_ORE_RAW);
        regODOre("ingotTitanium", JSGItems.TITANIUM_INGOT);
        regODOre("blockTitanium", JSGBlocks.TITANIUM_BLOCK);
        regODOre("gearTitanium", JSGItems.GEAR_TITANIUM);
        regODOre("plateTitanium", JSGItems.PLATE_TITANIUM);
        regODOre("nuggetTitanium", JSGItems.TITANIUM_NUGGET);
        regODOre("dustTitanium", JSGItems.TITANIUM_DUST);
    }

    public static void regODOre(String name, Item ore){
        OreDictionary.registerOre(name, ore);
    }
    public static void regODOre(String name, Block ore){
        OreDictionary.registerOre(name, ore);
    }

}
