package tauri.dev.jsg.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.GameData;
import tauri.dev.jsg.item.JSGItems;

@Mod.EventBusSubscriber
public class JSGRecipeHandler {
    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {

        // register furnace
        FurnaceRecipes.instance().addSmelting(JSGItems.TITANIUM_DUST, new ItemStack(JSGItems.TITANIUM_INGOT), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.PLATE_TITANIUM, new ItemStack(JSGItems.TITANIUM_INGOT), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.GEAR_TITANIUM, new ItemStack(JSGItems.TITANIUM_INGOT, 4), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.TITANIUM_ORE_IMPURE, new ItemStack(JSGItems.TITANIUM_NUGGET, 4), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.TITANIUM_ORE_PURIFIED, new ItemStack(JSGItems.TITANIUM_INGOT), 3f);
        FurnaceRecipes.instance().addSmelting(JSGItems.TITANIUM_ORE_RAW, new ItemStack(JSGItems.TITANIUM_INGOT, 2), 4f);

        FurnaceRecipes.instance().addSmelting(JSGItems.TRINIUM_DUST, new ItemStack(JSGItems.TRINIUM_INGOT), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.PLATE_TRINIUM, new ItemStack(JSGItems.TRINIUM_INGOT), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.GEAR_TRINIUM, new ItemStack(JSGItems.TRINIUM_INGOT, 4), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.TRINIUM_ORE_IMPURE, new ItemStack(JSGItems.TRINIUM_NUGGET, 4), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.TRINIUM_ORE_PURIFIED, new ItemStack(JSGItems.TRINIUM_INGOT), 3f);
        FurnaceRecipes.instance().addSmelting(JSGItems.TRINIUM_ORE_RAW, new ItemStack(JSGItems.TRINIUM_INGOT, 2), 4f);

        FurnaceRecipes.instance().addSmelting(JSGItems.NAQUADAH_DUST, new ItemStack(JSGItems.NAQUADAH_ALLOY), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.PLATE_NAQUADAH, new ItemStack(JSGItems.NAQUADAH_ALLOY), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.GEAR_NAQUADAH, new ItemStack(JSGItems.NAQUADAH_ALLOY, 4), 2f);

        FurnaceRecipes.instance().addSmelting(JSGItems.NAQUADAH_RAW_DUST, new ItemStack(JSGItems.NAQUADAH_ALLOY_RAW), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.PLATE_NAQUADAH_RAW, new ItemStack(JSGItems.NAQUADAH_ALLOY_RAW), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.GEAR_NAQUADAH_RAW, new ItemStack(JSGItems.NAQUADAH_ALLOY_RAW, 4), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.NAQUADAH_ORE_IMPURE, new ItemStack(JSGItems.NAQUADAH_RAW_NUGGET, 4), 2f);
        FurnaceRecipes.instance().addSmelting(JSGItems.NAQUADAH_ORE_PURIFIED, new ItemStack(JSGItems.NAQUADAH_ALLOY_RAW), 3f);
        FurnaceRecipes.instance().addSmelting(JSGItems.NAQUADAH_ORE_RAW, new ItemStack(JSGItems.NAQUADAH_ALLOY_RAW, 2), 4f);

        // normal recipes
        event.getRegistry().register(new NotebookRecipe());
        event.getRegistry().register(new NotebookPageCloneRecipe());
        event.getRegistry().register(new UniverseDialerCloneRecipe());
        event.getRegistry().register(new UniverseDialerRepairRecipe());
    }
}
