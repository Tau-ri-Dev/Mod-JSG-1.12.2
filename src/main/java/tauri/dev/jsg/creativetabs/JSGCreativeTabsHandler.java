package tauri.dev.jsg.creativetabs;

import net.minecraft.item.ItemStack;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.item.JSGItems;

import javax.annotation.Nonnull;

public class JSGCreativeTabsHandler {
    public static final JSGCreativeTabs JSG_GATES_CREATIVE_TAB = new JSGCreativeTabs("jsg_gates") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK);
        }
    };
    public static final JSGCreativeTabs JSG_RINGS_CREATIVE_TAB = new JSGCreativeTabs("jsg_rings") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.TRANSPORT_RINGS_GOAULD_BLOCK);
        }
    };
    public static final JSGCreativeTabs JSG_ORES_CREATIVE_TAB = new JSGCreativeTabs("jsg_ores") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.ORE_NAQUADAH_BLOCK);
        }
    };
    public static final JSGCreativeTabs JSG_ENERGY_CREATIVE_TAB = new JSGCreativeTabs("jsg_energy") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.CAPACITOR_BLOCK);
        }
    };
    public static final JSGCreativeTabs JSG_PROPS_CREATIVE_TAB = new JSGCreativeTabs("jsg_props") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.DESTINY_COUNTDOWN_BLOCK);
        }
    };
    public static final JSGCreativeTabs JSG_MACHINES_CREATIVE_TAB = new JSGCreativeTabs("jsg_machines") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.MACHINE_ASSEMBLER);
        }
    };
    public static final JSGCreativeTabs JSG_ITEMS_CREATIVE_TAB = (JSGCreativeTabs) new JSGCreativeTabs("jsg_items") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGItems.NAQUADAH_ALLOY);
        }

        @Override
        public boolean hasSearchBar(){
            return true;
        }
    }.setBackgroundImageName("item_search.png");
    public static final JSGCreativeTabs JSG_WEAPONS_CREATIVE_TAB = new JSGCreativeTabs("jsg_weapons") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGItems.ZAT);
        }
    };
    public static final JSGCreativeTabs JSG_TOOLS_CREATIVE_TAB = new JSGCreativeTabs("jsg_tools") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGItems.JSG_SCREWDRIVER);
        }
    };
}
