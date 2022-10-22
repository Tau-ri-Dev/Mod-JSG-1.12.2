package tauri.dev.jsg.machine;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.item.JSGItems;

import java.util.ArrayList;

public class SGAssemblerRecipes {

    public static SGAssemblerRecipe SG_MW_BASE_BLOCK = new SGAssemblerRecipe() {
        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public boolean removeSubItem() {
            return false;
        }

        @Override
        public boolean removeDurabilitySubItem() {
            return true;
        }

        @Override
        public int getEnergyPerTick() {
            return 4096;
        }

        @Override
        public Item getSchematic() {
            return JSGItems.SCHEMATIC_MILKYWAY;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(JSGItems.FRAGMENT_MILKYWAY));
            array.add(new ItemStack(JSGItems.FRAGMENT_MILKYWAY));
            array.add(new ItemStack(JSGItems.FRAGMENT_MILKYWAY));

            array.add(new ItemStack(JSGItems.CRYSTAL_BLUE));
            array.add(new ItemStack(JSGItems.CIRCUIT_CONTROL_NAQUADAH));
            array.add(new ItemStack(JSGItems.CRYSTAL_ENDER));

            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(new ItemStack(JSGItems.CRYSTAL_RED));
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_HAMMER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK);
        }
    };
    public static SGAssemblerRecipe SG_MW_CHEVRON_BLOCK = new SGAssemblerRecipe() {
        @Override
        public int getWorkingTime() {
            return 100;
        }

        @Override
        public boolean removeSubItem() {
            return false;
        }

        @Override
        public boolean removeDurabilitySubItem() {
            return true;
        }

        @Override
        public int getEnergyPerTick() {
            return 2048;
        }

        @Override
        public Item getSchematic() {
            return JSGItems.SCHEMATIC_MILKYWAY;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(new ItemStack(Blocks.GLASS));
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));

            array.add(new ItemStack(JSGItems.FRAGMENT_MILKYWAY));
            array.add(new ItemStack(JSGItems.CRYSTAL_YELLOW));
            array.add(new ItemStack(JSGItems.FRAGMENT_MILKYWAY));

            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(new ItemStack(JSGItems.CRYSTAL_ENDER));
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_HAMMER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK, 1, JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK.CHEVRON_META);
        }
    };

    public static SGAssemblerRecipe[] RECIPES = {
            SG_MW_BASE_BLOCK,
            SG_MW_CHEVRON_BLOCK
    };

}
