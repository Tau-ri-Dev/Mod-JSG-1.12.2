package tauri.dev.jsg.creativetabs;

import net.minecraft.item.ItemStack;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.item.CustomDiscsRegistry;
import tauri.dev.jsg.item.JSGItems;

import javax.annotation.Nonnull;

public class JSGCreativeTabsHandler {
    public static final JSGCreativeTab JSG_GATES_CREATIVE_TAB = new JSGCreativeTab("jsg_gates") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK);
        }
    };
    public static final JSGCreativeTab JSG_RINGS_CREATIVE_TAB = new JSGCreativeTab("jsg_rings") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.TRANSPORT_RINGS_GOAULD_BLOCK);
        }
    };
    public static final JSGCreativeTab JSG_ORES_CREATIVE_TAB = new JSGCreativeTab("jsg_ores") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.ORE_NAQUADAH_BLOCK);
        }
    };
    public static final JSGCreativeTab JSG_ENERGY_CREATIVE_TAB = new JSGCreativeTab("jsg_energy") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.CAPACITOR_BLOCK);
        }
    };
    public static final JSGCreativeTab JSG_PROPS_CREATIVE_TAB = new JSGCreativeTab("jsg_props") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.DESTINY_COUNTDOWN_BLOCK);
        }
    };
    public static final JSGCreativeTab JSG_MACHINES_CREATIVE_TAB = new JSGCreativeTab("jsg_machines") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGBlocks.MACHINE_CHAMBER);
        }
    };
    public static final JSGCreativeTab JSG_ITEMS_CREATIVE_TAB = (JSGCreativeTab) new JSGCreativeTab("jsg_items") {
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
    public static final JSGCreativeTab JSG_WEAPONS_CREATIVE_TAB = new JSGCreativeTab("jsg_weapons") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGItems.ZAT);
        }
    };
    public static final JSGCreativeTab JSG_TOOLS_CREATIVE_TAB = new JSGCreativeTab("jsg_tools") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(JSGItems.JSG_SCREWDRIVER);
        }
    };
    public static final JSGCreativeTab JSG_RECORDS = new JSGCreativeTab("jsg_records") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(CustomDiscsRegistry.RECORD_DESTINY);
        }
    };
}
