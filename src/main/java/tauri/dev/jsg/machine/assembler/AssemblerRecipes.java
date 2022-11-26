package tauri.dev.jsg.machine.assembler;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.craftings.CraftingConfig;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.machine.chamber.CrystalChamberRecipe;

import java.util.ArrayList;

public class AssemblerRecipes {

    public static AssemblerRecipe SG_MW_BASE_BLOCK = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.stargate_milkyway_base_block.name";
        }

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
    public static AssemblerRecipe SG_MW_CHEVRON_BLOCK = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.stargate_milkyway_chevron_block.name";
        }

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
    public static AssemblerRecipe SG_MW_RING_BLOCK = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.stargate_milkyway_ring_block.name";
        }

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

            array.add(null);
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(null);

            array.add(new ItemStack(JSGItems.FRAGMENT_MILKYWAY));
            array.add(new ItemStack(JSGItems.CRYSTAL_RED));
            array.add(new ItemStack(JSGItems.FRAGMENT_MILKYWAY));

            array.add(null);
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(null);
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_HAMMER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK, 1, JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK.RING_META);
        }
    };
    public static AssemblerRecipe SG_MW_DHD = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.dhd_block.name";
        }

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

            array.add(new ItemStack(Blocks.STONE_BUTTON));
            array.add(new ItemStack(JSGItems.DHD_BRB));
            array.add(new ItemStack(Blocks.STONE_BUTTON));

            array.add(new ItemStack(JSGItems.CRYSTAL_RED));
            array.add(new ItemStack(JSGItems.CIRCUIT_CONTROL_NAQUADAH));
            array.add(new ItemStack(JSGItems.CRYSTAL_ENDER));

            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(new ItemStack(JSGItems.HOLDER_CRYSTAL));
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_SCREWDRIVER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.DHD_BLOCK, 1);
        }
    };

    public static AssemblerRecipe SG_PEG_BASE_BLOCK = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.stargate_pegasus_base_block.name";
        }

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
            return JSGItems.SCHEMATIC_PEGASUS;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(JSGItems.FRAGMENT_PEGASUS));
            array.add(new ItemStack(JSGItems.FRAGMENT_PEGASUS));
            array.add(new ItemStack(JSGItems.FRAGMENT_PEGASUS));

            array.add(new ItemStack(JSGItems.CRYSTAL_BLUE));
            array.add(new ItemStack(JSGItems.CIRCUIT_CONTROL_NAQUADAH));
            array.add(new ItemStack(JSGItems.CRYSTAL_ENDER));

            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(new ItemStack(JSGItems.CRYSTAL_YELLOW));
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_HAMMER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK);
        }
    };
    public static AssemblerRecipe SG_PEG_CHEVRON_BLOCK = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.stargate_pegasus_chevron_block.name";
        }

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
            return JSGItems.SCHEMATIC_PEGASUS;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(new ItemStack(Blocks.GLASS));
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));

            array.add(new ItemStack(JSGItems.FRAGMENT_PEGASUS));
            array.add(new ItemStack(JSGItems.CRYSTAL_YELLOW));
            array.add(new ItemStack(JSGItems.FRAGMENT_PEGASUS));

            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(new ItemStack(JSGItems.CRYSTAL_BLUE));
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_HAMMER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK, 1, JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK.CHEVRON_META);
        }
    };
    public static AssemblerRecipe SG_PEG_RING_BLOCK = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.stargate_pegasus_ring_block.name";
        }

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
            return JSGItems.SCHEMATIC_PEGASUS;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(null);
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(null);

            array.add(new ItemStack(JSGItems.FRAGMENT_PEGASUS));
            array.add(new ItemStack(JSGItems.CRYSTAL_BLUE));
            array.add(new ItemStack(JSGItems.FRAGMENT_PEGASUS));

            array.add(null);
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(null);
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_HAMMER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK, 1, JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK.RING_META);
        }
    };
    public static AssemblerRecipe SG_PEG_DHD = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.dhd_pegasus_block.name";
        }

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
            return JSGItems.SCHEMATIC_PEGASUS;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(Blocks.STONE_BUTTON));
            array.add(new ItemStack(JSGItems.DHD_BBB));
            array.add(new ItemStack(Blocks.STONE_BUTTON));

            array.add(new ItemStack(JSGItems.CRYSTAL_BLUE));
            array.add(new ItemStack(JSGItems.CIRCUIT_CONTROL_NAQUADAH));
            array.add(new ItemStack(JSGItems.CRYSTAL_ENDER));

            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(new ItemStack(JSGItems.HOLDER_CRYSTAL_PEGASUS));
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_SCREWDRIVER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.DHD_PEGASUS_BLOCK, 1);
        }
    };

    public static AssemblerRecipe SG_UNI_BASE_BLOCK = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.stargate_universe_base_block.name";
        }

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
            return JSGItems.SCHEMATIC_UNIVERSE;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(JSGItems.FRAGMENT_UNIVERSE));
            array.add(new ItemStack(JSGItems.FRAGMENT_UNIVERSE));
            array.add(new ItemStack(JSGItems.FRAGMENT_UNIVERSE));

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
            return new ItemStack(JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK);
        }
    };
    public static AssemblerRecipe SG_UNI_CHEVRON_BLOCK = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.stargate_universe_chevron_block.name";
        }

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
            return JSGItems.SCHEMATIC_UNIVERSE;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(new ItemStack(Blocks.GLASS));
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));

            array.add(new ItemStack(JSGItems.FRAGMENT_UNIVERSE));
            array.add(new ItemStack(JSGItems.CRYSTAL_WHITE));
            array.add(new ItemStack(JSGItems.FRAGMENT_UNIVERSE));

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
            return new ItemStack(JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK, 1, JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK.CHEVRON_META);
        }
    };
    public static AssemblerRecipe SG_UNI_RING_BLOCK = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.stargate_universe_ring_block.name";
        }

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
            return JSGItems.SCHEMATIC_UNIVERSE;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(null);
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(null);

            array.add(new ItemStack(JSGItems.FRAGMENT_UNIVERSE));
            array.add(new ItemStack(JSGItems.CRYSTAL_RED));
            array.add(new ItemStack(JSGItems.FRAGMENT_UNIVERSE));

            array.add(null);
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(null);
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_HAMMER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK, 1, JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK.RING_META);
        }
    };

    public static AssemblerRecipe DESTINY_COUNTDOWN = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.destiny_countdown_block.name";
        }

        @Override
        public int getWorkingTime() {
            return 100;
        }

        @Override
        public boolean removeSubItem() {
            return true;
        }

        @Override
        public int getEnergyPerTick() {
            return 2048;
        }

        @Override
        public Item getSchematic() {
            return JSGItems.SCHEMATIC_UNIVERSE;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(JSGItems.GEAR_NAQUADAH_RAW));
            array.add(new ItemStack(JSGItems.CRYSTAL_WHITE));
            array.add(new ItemStack(JSGItems.GEAR_NAQUADAH_RAW));

            array.add(new ItemStack(JSGItems.FRAGMENT_UNIVERSE));
            array.add(new ItemStack(Blocks.GLASS_PANE));
            array.add(new ItemStack(JSGItems.FRAGMENT_UNIVERSE));

            array.add(new ItemStack(JSGItems.GEAR_NAQUADAH_RAW));
            array.add(new ItemStack(JSGItems.CRYSTAL_WHITE));
            array.add(new ItemStack(JSGItems.GEAR_NAQUADAH_RAW));
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.DESTINY_COUNTDOWN_BLOCK);
        }
    };

    public static AssemblerRecipe TR_GOAULD = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.transportrings_goauld_block.name";
        }

        @Override
        public int getWorkingTime() {
            return 540;
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
            return 1048;
        }

        @Override
        public Item getSchematic() {
            return JSGItems.SCHEMATIC_TR_GOAULD;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(JSGItems.FRAGMENT_TR_GOAULD));
            array.add(new ItemStack(JSGItems.CRYSTAL_YELLOW));
            array.add(new ItemStack(JSGItems.FRAGMENT_TR_GOAULD));

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
            return new ItemStack(JSGBlocks.TRANSPORT_RINGS_GOAULD_BLOCK);
        }
    };
    public static AssemblerRecipe TR_ORI = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.transportrings_ori_block.name";
        }

        @Override
        public int getWorkingTime() {
            return 600;
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
            return 1248;
        }

        @Override
        public Item getSchematic() {
            return JSGItems.SCHEMATIC_TR_ORI;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(JSGItems.FRAGMENT_TR_ORI));
            array.add(new ItemStack(JSGItems.CRYSTAL_YELLOW));
            array.add(new ItemStack(JSGItems.FRAGMENT_TR_ORI));

            array.add(new ItemStack(JSGItems.CRYSTAL_BLUE));
            array.add(new ItemStack(JSGItems.CIRCUIT_CONTROL_NAQUADAH));
            array.add(new ItemStack(JSGItems.CRYSTAL_ENDER));

            array.add(new ItemStack(JSGItems.TRINIUM_INGOT));
            array.add(new ItemStack(JSGItems.CRYSTAL_RED));
            array.add(new ItemStack(JSGItems.TRINIUM_INGOT));
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_HAMMER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.TRANSPORT_RINGS_ORI_BLOCK);
        }
    };

    public static AssemblerRecipe TR_GOAULD_CONTROLLER = new AssemblerRecipe() {
        @Override
        public String getUnlocalizedName() {
            return "tile.jsg.transportrings_controller_goauld_block.name";
        }

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
            return 1248;
        }

        @Override
        public Item getSchematic() {
            return JSGItems.SCHEMATIC_TR_GOAULD;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            ArrayList<ItemStack> array = new ArrayList<>();

            array.add(new ItemStack(Blocks.STONE_BUTTON));
            array.add(new ItemStack(Items.GOLD_INGOT));
            array.add(new ItemStack(Blocks.STONE_BUTTON));

            array.add(new ItemStack(Items.GOLD_INGOT));
            array.add(new ItemStack(JSGItems.CIRCUIT_CONTROL_NAQUADAH));
            array.add(new ItemStack(Items.GOLD_INGOT));

            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            array.add(new ItemStack(JSGItems.CRYSTAL_ENDER));
            array.add(new ItemStack(JSGItems.NAQUADAH_ALLOY));
            return array;
        }

        @Override
        public ItemStack getSubItemStack() {
            return new ItemStack(JSGItems.JSG_SCREWDRIVER);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGBlocks.TR_CONTROLLER_GOAULD_BLOCK);
        }
    };

    public static AssemblerRecipe[] RECIPES = {
            SG_MW_BASE_BLOCK,
            SG_MW_CHEVRON_BLOCK,
            SG_MW_RING_BLOCK,
            SG_MW_DHD,

            SG_PEG_BASE_BLOCK,
            SG_PEG_CHEVRON_BLOCK,
            SG_PEG_RING_BLOCK,
            SG_PEG_DHD,

            SG_UNI_BASE_BLOCK,
            SG_UNI_CHEVRON_BLOCK,
            SG_UNI_RING_BLOCK,

            DESTINY_COUNTDOWN,

            TR_GOAULD,
            TR_ORI,
            TR_GOAULD_CONTROLLER
    };

    public static void addToConfig(){
        CraftingConfig config = new CraftingConfig(AssemblerRecipe.ID);
        for(AssemblerRecipe recipe : RECIPES){
            config.addKey(recipe.getResult().getItem().getRegistryName());
        }
        CraftingConfig.addConfig(config);
    }

}
