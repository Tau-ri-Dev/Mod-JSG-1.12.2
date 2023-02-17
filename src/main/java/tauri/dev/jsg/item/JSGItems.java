package tauri.dev.jsg.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerItem;
import tauri.dev.jsg.item.linkable.gdo.GDOItem;
import tauri.dev.jsg.item.mysterious.PegasusPageMysteriousItem;
import tauri.dev.jsg.item.mysterious.UniversePageMysteriousItem;
import tauri.dev.jsg.item.notebook.NotebookItem;
import tauri.dev.jsg.item.notebook.PageNotebookItem;
import tauri.dev.jsg.item.renderer.CustomModelItemInterface;
import tauri.dev.jsg.item.mysterious.MilkyWayPageMysteriousItem;
import tauri.dev.jsg.item.stargate.UpgradeIris;
import tauri.dev.jsg.item.tools.staff.StaffItem;
import tauri.dev.jsg.item.tools.zat.ZatItem;

import javax.annotation.Nullable;
import java.util.Objects;

import static tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler.*;

@EventBusSubscriber
public class JSGItems {

    /**
     * DHD power/control crystal
     */
    public static final Item CRYSTAL_CONTROL_MILKYWAY_DHD = ItemHelper.createGenericItem("crystal_control_dhd", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_CONTROL_PEGASUS_DHD = ItemHelper.createGenericItem("crystal_control_pegasus_dhd", JSG_ITEMS_CREATIVE_TAB);

    /**
     * Iris/Shield upgrade
     */
    public static final Item IRIS_BLADE = ItemHelper.createGenericItem("iris_blade", JSG_ITEMS_CREATIVE_TAB);
    public static final Item QUAD_IRIS_BLADE = ItemHelper.createGenericItem("quad_iris_blade", JSG_ITEMS_CREATIVE_TAB);
    public static final Item UPGRADE_IRIS = new UpgradeIris("upgrade_iris", JSGConfig.Stargate.iris.titaniumIrisDurability);

    public static final Item IRIS_BLADE_TRINIUM = ItemHelper.createGenericItem("iris_blade_trinium", JSG_ITEMS_CREATIVE_TAB);
    public static final Item QUAD_IRIS_BLADE_TRINIUM = ItemHelper.createGenericItem("quad_iris_blade_trinium", JSG_ITEMS_CREATIVE_TAB);
    public static final Item UPGRADE_IRIS_TRINIUM = new UpgradeIris("upgrade_iris_trinium", JSGConfig.Stargate.iris.triniumIrisDurability);

    public static final Item UPGRADE_SHIELD = ItemHelper.createGenericItem("upgrade_shield", JSG_ITEMS_CREATIVE_TAB);
    public static final Item SHIELD_EMITTER = ItemHelper.createGenericItem("shield_emitter", JSG_ITEMS_CREATIVE_TAB);

    /**
     * These allow for dialing 8th glyph(cross dimension travel) and show different address spaces
     */
    public static final Item CRYSTAL_GLYPH_DHD = ItemHelper.createGenericItem("crystal_glyph_dhd", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_GLYPH_STARGATE = ItemHelper.createGenericItem("crystal_glyph_stargate", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_GLYPH_MILKYWAY = ItemHelper.createGenericItem("crystal_glyph_milkyway", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_GLYPH_PEGASUS = ItemHelper.createGenericItem("crystal_glyph_pegasus", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_GLYPH_UNIVERSE = ItemHelper.createGenericItem("crystal_glyph_universe", JSG_ITEMS_CREATIVE_TAB);

    // transportrings
    public static final Item CRYSTAL_GLYPH_GOAULD = ItemHelper.createGenericItem("crystal_glyph_goauld", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_GLYPH_ORI = ItemHelper.createGenericItem("crystal_glyph_ori", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_GLYPH_ANCIENT = ItemHelper.createGenericItem("crystal_glyph_ancient", JSG_ITEMS_CREATIVE_TAB);

    /**
     * Upgrades
     */
    public static final Item CRYSTAL_UPGRADE_CAPACITY = ItemHelper.createGenericItem("crystal_upgrade_capacity", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_UPGRADE_EFFICIENCY = ItemHelper.createGenericItem("crystal_upgrade_efficiency", JSG_ITEMS_CREATIVE_TAB);

    /**
     * Different Naquadah(main Stargate building material) stages of purity
     */
    public static final Item NAQUADAH_ORE_IMPURE = ItemHelper.createGenericItem("naquadah_impure", JSG_ORES_CREATIVE_TAB);
    public static final Item NAQUADAH_ORE_PURIFIED = ItemHelper.createGenericItem("naquadah_purified", JSG_ORES_CREATIVE_TAB);
    public static final Item NAQUADAH_ORE_RAW = ItemHelper.createGenericItem("naquadah_raw", JSG_ORES_CREATIVE_TAB);
    public static final Item NAQUADAH_ALLOY_RAW = ItemHelper.createGenericItem("naquadah_alloy_raw", JSG_ITEMS_CREATIVE_TAB);
    public static final Item NAQUADAH_RAW_DUST = ItemHelper.createGenericItem("naquadah_raw_dust", JSG_ITEMS_CREATIVE_TAB);
    public static final Item NAQUADAH_RAW_NUGGET = ItemHelper.createGenericItem("naquadah_raw_nugget", JSG_ITEMS_CREATIVE_TAB);

    public static final Item NAQUADAH_ALLOY = ItemHelper.createGenericItem("naquadah_alloy", JSG_ITEMS_CREATIVE_TAB);
    public static final Item NAQUADAH_DUST = ItemHelper.createGenericItem("naquadah_dust", JSG_ITEMS_CREATIVE_TAB);
    public static final Item NAQUADAH_NUGGET = ItemHelper.createGenericItem("naquadah_nugget", JSG_ITEMS_CREATIVE_TAB);

    /**
     * Titanium & Trinium
     */
    public static final Item TITANIUM_ORE_IMPURE = ItemHelper.createGenericItem("titanium_impure", JSG_ORES_CREATIVE_TAB);
    public static final Item TITANIUM_ORE_PURIFIED = ItemHelper.createGenericItem("titanium_purified", JSG_ORES_CREATIVE_TAB);
    public static final Item TITANIUM_ORE_RAW = ItemHelper.createGenericItem("titanium_raw", JSG_ORES_CREATIVE_TAB);
    public static final Item TITANIUM_INGOT = ItemHelper.createGenericItem("titanium_ingot", JSG_ITEMS_CREATIVE_TAB);
    public static final Item TITANIUM_DUST = ItemHelper.createGenericItem("titanium_dust", JSG_ITEMS_CREATIVE_TAB);
    public static final Item TITANIUM_NUGGET = ItemHelper.createGenericItem("titanium_nugget", JSG_ITEMS_CREATIVE_TAB);

    public static final Item TRINIUM_ORE_IMPURE = ItemHelper.createGenericItem("trinium_impure", JSG_ORES_CREATIVE_TAB);
    public static final Item TRINIUM_ORE_PURIFIED = ItemHelper.createGenericItem("trinium_purified", JSG_ORES_CREATIVE_TAB);
    public static final Item TRINIUM_ORE_RAW = ItemHelper.createGenericItem("trinium_raw", JSG_ORES_CREATIVE_TAB);
    public static final Item TRINIUM_INGOT = ItemHelper.createGenericItem("trinium_ingot", JSG_ITEMS_CREATIVE_TAB);
    public static final Item TRINIUM_DUST = ItemHelper.createGenericItem("trinium_dust", JSG_ITEMS_CREATIVE_TAB);
    public static final Item TRINIUM_NUGGET = ItemHelper.createGenericItem("trinium_nugget", JSG_ITEMS_CREATIVE_TAB);

    /**
     * Crafting items
     */
    public static final Item CRYSTAL_SEED = ItemHelper.createGenericItem("crystal_fragment", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_BLUE = ItemHelper.createGenericItem("crystal_blue", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_RED = ItemHelper.createGenericItem("crystal_red", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_ENDER = ItemHelper.createGenericItem("crystal_ender", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_YELLOW = ItemHelper.createGenericItem("crystal_yellow", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_WHITE = ItemHelper.createGenericItem("crystal_white", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CRYSTAL_BLUE_PEGASUS = ItemHelper.createGenericItem("crystal_blue_pegasus", JSG_ITEMS_CREATIVE_TAB);

    public static final Item CRYSTAL_TOKRA = ItemHelper.createGenericItem("crystal_tokra", JSG_ITEMS_CREATIVE_TAB);

    public static final Item CIRCUIT_CONTROL_BASE = ItemHelper.createGenericItem("circuit_control_base", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CIRCUIT_CONTROL_CRYSTAL = ItemHelper.createGenericItem("circuit_control_crystal", JSG_ITEMS_CREATIVE_TAB);
    public static final Item CIRCUIT_CONTROL_NAQUADAH = ItemHelper.createGenericItem("circuit_control_naquadah", JSG_ITEMS_CREATIVE_TAB);

    public static final Item HOLDER_CRYSTAL = ItemHelper.createGenericItem("holder_crystal", JSG_ITEMS_CREATIVE_TAB);
    public static final Item HOLDER_CRYSTAL_PEGASUS = ItemHelper.createGenericItem("holder_crystal_pegasus", JSG_ITEMS_CREATIVE_TAB);

    public static final Item DHD_BRB = ItemHelper.createGenericItem("dhd_brb", JSG_ITEMS_CREATIVE_TAB);
    public static final Item DHD_BBB = ItemHelper.createGenericItem("dhd_bbb", JSG_ITEMS_CREATIVE_TAB);

    public static final NotebookItem NOTEBOOK_ITEM = new NotebookItem();
    public static final PageNotebookItem PAGE_NOTEBOOK_ITEM = new PageNotebookItem();
    public static final MilkyWayPageMysteriousItem PAGE_MYSTERIOUS_ITEM_MILKYWAY = new MilkyWayPageMysteriousItem();
    public static final PegasusPageMysteriousItem PAGE_MYSTERIOUS_ITEM_PEGASUS = new PegasusPageMysteriousItem();
    public static final UniversePageMysteriousItem PAGE_MYSTERIOUS_ITEM_UNIVERSE = new UniversePageMysteriousItem();
    public static final UniverseDialerItem UNIVERSE_DIALER = new UniverseDialerItem();

    public static final GDOItem GDO = new GDOItem();

    public static final Item BEAMER_CRYSTAL_POWER = ItemHelper.createGenericItem("beamer_crystal_power", JSG_ITEMS_CREATIVE_TAB);
    public static final Item BEAMER_CRYSTAL_FLUID = ItemHelper.createGenericItem("beamer_crystal_fluid", JSG_ITEMS_CREATIVE_TAB);
    public static final Item BEAMER_CRYSTAL_ITEMS = ItemHelper.createGenericItem("beamer_crystal_items", JSG_ITEMS_CREATIVE_TAB);
    public static final Item BEAMER_CRYSTAL_LASER = ItemHelper.createGenericItem("beamer_crystal_laser", JSG_ITEMS_CREATIVE_TAB);

    /**
     * TOOLS
     */
    public static final ZatItem ZAT = new ZatItem();
    public static final StaffItem STAFF = new StaffItem();

    public static final Item JSG_HAMMER = ItemHelper.createDurabilityItem("hammer", JSG_TOOLS_CREATIVE_TAB, 25, true).setMaxStackSize(1);
    public static final Item JSG_SCREWDRIVER = ItemHelper.createDurabilityItem("screwdriver", JSG_TOOLS_CREATIVE_TAB, 150, true).setMaxStackSize(1);
    public static final Item JSG_WRENCH = ItemHelper.createDurabilityItem("wrench", JSG_TOOLS_CREATIVE_TAB, 225, true).setMaxStackSize(1);

    public static final Item TAURI_ANCIENT_ADAPTER = ItemHelper.createGenericItem("tauri_ancient_adapter", JSG_ITEMS_CREATIVE_TAB);

    /**
     * FRAGMENTS
     */
    public static final Item FRAGMENT_MILKYWAY = ItemHelper.createGenericItem("fragment_stargate_milkyway", JSG_ITEMS_CREATIVE_TAB);
    public static final Item FRAGMENT_PEGASUS = ItemHelper.createGenericItem("fragment_stargate_pegasus", JSG_ITEMS_CREATIVE_TAB);
    public static final Item FRAGMENT_UNIVERSE = ItemHelper.createGenericItem("fragment_stargate_universe", JSG_ITEMS_CREATIVE_TAB);

    public static final Item FRAGMENT_TR_GOAULD = ItemHelper.createGenericItem("fragment_transportrings_goauld", JSG_ITEMS_CREATIVE_TAB);
    public static final Item FRAGMENT_TR_ORI = ItemHelper.createGenericItem("fragment_transportrings_ori", JSG_ITEMS_CREATIVE_TAB);
    public static final Item FRAGMENT_TR_ANCIENT = ItemHelper.createGenericItem("fragment_transportrings_ancient", JSG_ITEMS_CREATIVE_TAB);

    /**
     * GEARS
     */
    public static final Item GEAR_TITANIUM = ItemHelper.createGenericItem("gear_titanium", JSG_ITEMS_CREATIVE_TAB);
    public static final Item GEAR_TRINIUM = ItemHelper.createGenericItem("gear_trinium", JSG_ITEMS_CREATIVE_TAB);
    public static final Item GEAR_NAQUADAH_RAW = ItemHelper.createGenericItem("gear_naquadah_raw", JSG_ITEMS_CREATIVE_TAB);
    public static final Item GEAR_NAQUADAH = ItemHelper.createGenericItem("gear_naquadah", JSG_ITEMS_CREATIVE_TAB);

    /**
     * PLATES
     */
    public static final Item PLATE_TITANIUM = ItemHelper.createGenericItem("plate_titanium", JSG_ITEMS_CREATIVE_TAB);
    public static final Item PLATE_TRINIUM = ItemHelper.createGenericItem("plate_trinium", JSG_ITEMS_CREATIVE_TAB);
    public static final Item PLATE_NAQUADAH_RAW = ItemHelper.createGenericItem("plate_naquadah_raw", JSG_ITEMS_CREATIVE_TAB);
    public static final Item PLATE_NAQUADAH = ItemHelper.createGenericItem("plate_naquadah", JSG_ITEMS_CREATIVE_TAB);

    /**
     * SCHEMATICS
     */
    public static final Item SCHEMATIC_MILKYWAY = ItemHelper.createGenericItem("schematic_milkyway", JSG_ITEMS_CREATIVE_TAB);
    public static final Item SCHEMATIC_PEGASUS = ItemHelper.createGenericItem("schematic_pegasus", JSG_ITEMS_CREATIVE_TAB);
    public static final Item SCHEMATIC_UNIVERSE = ItemHelper.createGenericItem("schematic_universe", JSG_ITEMS_CREATIVE_TAB);

    public static final Item SCHEMATIC_TR_GOAULD = ItemHelper.createGenericItem("schematic_goauld", JSG_ITEMS_CREATIVE_TAB);
    public static final Item SCHEMATIC_TR_ORI = ItemHelper.createGenericItem("schematic_ori", JSG_ITEMS_CREATIVE_TAB);
    public static final Item SCHEMATIC_TR_ANCIENT = ItemHelper.createGenericItem("schematic_ancient", JSG_ITEMS_CREATIVE_TAB);

    public static boolean isInItemsArray(Item block, Item[] array) {
        for (Item b : array) {
            if (block == b) {
                return true;
            }
        }
        return false;
    }

    public static final Item[] ITEMS = {
            CRYSTAL_CONTROL_MILKYWAY_DHD,
            CRYSTAL_CONTROL_PEGASUS_DHD,

            IRIS_BLADE,
            QUAD_IRIS_BLADE,
            UPGRADE_IRIS,
            IRIS_BLADE_TRINIUM,
            QUAD_IRIS_BLADE_TRINIUM,
            UPGRADE_IRIS_TRINIUM,
            UPGRADE_SHIELD,
            SHIELD_EMITTER,

            CRYSTAL_GLYPH_DHD,
            CRYSTAL_GLYPH_STARGATE,
            CRYSTAL_GLYPH_MILKYWAY,
            CRYSTAL_GLYPH_PEGASUS,
            CRYSTAL_GLYPH_UNIVERSE,
            CRYSTAL_GLYPH_GOAULD,
            CRYSTAL_GLYPH_ORI,
            CRYSTAL_GLYPH_ANCIENT,

            NAQUADAH_ORE_IMPURE,
            NAQUADAH_ORE_PURIFIED,
            NAQUADAH_ORE_RAW,
            NAQUADAH_RAW_DUST,
            NAQUADAH_RAW_NUGGET,

            NAQUADAH_ALLOY_RAW,
            NAQUADAH_ALLOY,
            NAQUADAH_DUST,
            NAQUADAH_NUGGET,

            TRINIUM_ORE_IMPURE,
            TRINIUM_ORE_PURIFIED,
            TRINIUM_ORE_RAW,
            TRINIUM_INGOT,
            TRINIUM_DUST,
            TRINIUM_NUGGET,

            TITANIUM_ORE_IMPURE,
            TITANIUM_ORE_PURIFIED,
            TITANIUM_ORE_RAW,
            TITANIUM_INGOT,
            TITANIUM_DUST,
            TITANIUM_NUGGET,

            CRYSTAL_SEED,
            CRYSTAL_BLUE,
            CRYSTAL_RED,
            CRYSTAL_ENDER,
            CRYSTAL_YELLOW,
            CRYSTAL_WHITE,
            CRYSTAL_BLUE_PEGASUS,
            CRYSTAL_TOKRA,

            CRYSTAL_UPGRADE_CAPACITY,
            CRYSTAL_UPGRADE_EFFICIENCY,

            BEAMER_CRYSTAL_POWER,
            BEAMER_CRYSTAL_FLUID,
            BEAMER_CRYSTAL_ITEMS,
            BEAMER_CRYSTAL_LASER,

            CIRCUIT_CONTROL_BASE,
            CIRCUIT_CONTROL_CRYSTAL,
            CIRCUIT_CONTROL_NAQUADAH,

            FRAGMENT_MILKYWAY,
            FRAGMENT_PEGASUS,
            FRAGMENT_UNIVERSE,

            FRAGMENT_TR_GOAULD,
            FRAGMENT_TR_ORI,
            FRAGMENT_TR_ANCIENT,

            HOLDER_CRYSTAL,
            HOLDER_CRYSTAL_PEGASUS,

            DHD_BRB,
            DHD_BBB,
            NOTEBOOK_ITEM,
            PAGE_NOTEBOOK_ITEM,
            UNIVERSE_DIALER,

            GDO,

            ZAT,
            STAFF,
            JSG_HAMMER,
            JSG_SCREWDRIVER,
            JSG_WRENCH,

            GEAR_TITANIUM,
            GEAR_TRINIUM,
            GEAR_NAQUADAH_RAW,
            GEAR_NAQUADAH,

            PLATE_TITANIUM,
            PLATE_TRINIUM,
            PLATE_NAQUADAH_RAW,
            PLATE_NAQUADAH,

            SCHEMATIC_MILKYWAY,
            SCHEMATIC_PEGASUS,
            SCHEMATIC_UNIVERSE,
            SCHEMATIC_TR_GOAULD,
            SCHEMATIC_TR_ORI,
            SCHEMATIC_TR_ANCIENT,

            PAGE_MYSTERIOUS_ITEM_MILKYWAY,
            PAGE_MYSTERIOUS_ITEM_PEGASUS,
            PAGE_MYSTERIOUS_ITEM_UNIVERSE,


            // ----------------------
            // ICONS
            new JSGIconItem("main"),
            new JSGIconItem("mw_poo"),
            new JSGIconItem("pg_poo"),
            new JSGIconItem("uni_poo")

    };

    public static Item[] getItems() {
        return ITEMS;
    }

    @SubscribeEvent
    public static void onRegisterItems(Register<Item> event) {
        for (Item item : ITEMS) {
            if (item instanceof CustomModelItemInterface)
                ((CustomModelItemInterface) item).setTEISR();

            event.getRegistry().register(item);
        }
        for(Item record : CustomDiscsRegistry.getRecordItems()){
            event.getRegistry().register(record);
        }
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        for (Item item : ITEMS) {
            if (item instanceof CustomModelItemInterface)
                ((CustomModelItemInterface) item).setCustomModelLocation();
            else
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
        }
        for(Item record : CustomDiscsRegistry.getRecordItems()){
            ModelLoader.setCustomModelResourceLocation(record, 0, new ModelResourceLocation(Objects.requireNonNull(record.getRegistryName()), "inventory"));
        }
    }

    @Nullable
    public static Item remapItem(String oldName) {
        switch (oldName) {
            case "jsg:stargate_ring_fragment":
            case "aunis:stargate_ring_fragment":
                return FRAGMENT_MILKYWAY;

            case "jsg:universe_ring_fragment":
            case "aunis:universe_ring_fragment":
                return FRAGMENT_UNIVERSE;

            case "jsg:transportrings_ring_fragment":
            case "aunis:transportrings_ring_fragment":
                return FRAGMENT_TR_GOAULD;

            case "jsg:naquadah_shard":
            case "aunis:naquadah_shard":
                return NAQUADAH_ORE_IMPURE;

            case "jsg:page_mysterious":
            case "aunis:page_mysterious":
                return PAGE_MYSTERIOUS_ITEM_MILKYWAY;

            case "jsg:raw_pegasus_dhd_crystal":
            case "aunis:raw_pegasus_dhd_crystal":
                return CRYSTAL_BLUE_PEGASUS;

            default:
                break;
        }
        return null;
    }
}
