package tauri.dev.jsg.block;

import tauri.dev.jsg.block.beamer.BeamerBlock;
import tauri.dev.jsg.block.capacitor.CapacitorBlock;
import tauri.dev.jsg.block.capacitor.CapacitorBlockEmpty;
import tauri.dev.jsg.block.dialhomedevice.DHDBlock;
import tauri.dev.jsg.block.dialhomedevice.DHDPegasusBlock;
import tauri.dev.jsg.block.invisible.InvisibleBlock;
import tauri.dev.jsg.block.invisible.IrisBlock;
import tauri.dev.jsg.block.ore.NaquadahOreBlock;
import tauri.dev.jsg.block.ore.TitaniumOreBlock;
import tauri.dev.jsg.block.ore.TriniumOreBlock;
import tauri.dev.jsg.block.stargate.*;
import tauri.dev.jsg.block.transportrings.TRControllerGoauldBlock;
import tauri.dev.jsg.block.transportrings.TransportRingsGoauldBlock;
import tauri.dev.jsg.block.transportrings.TransportRingsOriBlock;
import tauri.dev.jsg.block.props.TRPlatformBlock;
import tauri.dev.jsg.item.linkable.gdo.GDOItem;
import tauri.dev.jsg.tileentity.transportrings.TRControllerAbstractTile;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.util.BlockHelpers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

import java.util.Map;

import static tauri.dev.jsg.util.main.loader.JSGCreativeTabsHandler.jsgOresCreativeTab;

@EventBusSubscriber
public class JSGBlocks {
    public static final NaquadahOreBlock ORE_NAQUADAH_BLOCK = new NaquadahOreBlock("naquadah_ore");
    public static final NaquadahOreBlock ORE_NAQUADAH_BLOCK_STONE = new NaquadahOreBlock("naquadah_ore_stone");
    public static final JSGBlock NAQUADAH_BLOCK = BlockHelpers.createSimpleBlock("naquadah_block", Material.IRON, jsgOresCreativeTab);

    public static final TriniumOreBlock ORE_TRINIUM_BLOCK = new TriniumOreBlock("trinium_ore");
    public static final TitaniumOreBlock ORE_TITANIUM_BLOCK = new TitaniumOreBlock("titanium_ore");

    // -----------------------------------------------------------------------------

    public static final JSGBlock NAQUADAH_BLOCK_RAW = (JSGBlock) BlockHelpers.createSimpleBlock("naquadah_block_raw", Material.IRON, jsgOresCreativeTab).setHardness(3);
    public static final JSGBlock TRINIUM_BLOCK = BlockHelpers.createSimpleBlock("trinium_block", Material.IRON, jsgOresCreativeTab);
    public static final JSGBlock TITANIUM_BLOCK = BlockHelpers.createSimpleBlock("titanium_block", Material.IRON, jsgOresCreativeTab);

    // -----------------------------------------------------------------------------

    public static final StargateMilkyWayBaseBlock STARGATE_MILKY_WAY_BASE_BLOCK = new StargateMilkyWayBaseBlock();
    public static final StargateUniverseBaseBlock STARGATE_UNIVERSE_BASE_BLOCK = new StargateUniverseBaseBlock();
    public static final StargateOrlinBaseBlock STARGATE_ORLIN_BASE_BLOCK = new StargateOrlinBaseBlock();
    public static final StargatePegasusBaseBlock STARGATE_PEGASUS_BASE_BLOCK = new StargatePegasusBaseBlock();

    public static final DHDBlock DHD_BLOCK = new DHDBlock();
    public static final DHDPegasusBlock DHD_PEGASUS_BLOCK = new DHDPegasusBlock();

    public static final TransportRingsGoauldBlock TRANSPORT_RINGS_GOAULD_BLOCK = new TransportRingsGoauldBlock();
    public static final TransportRingsOriBlock TRANSPORT_RINGS_ORI_BLOCK = new TransportRingsOriBlock();
    public static final TRControllerGoauldBlock TR_CONTROLLER_GOAULD_BLOCK = new TRControllerGoauldBlock();
    public static final InvisibleBlock INVISIBLE_BLOCK = new InvisibleBlock();
    public static final IrisBlock IRIS_BLOCK = new IrisBlock();

    // -----------------------------------------------------------------------------

    public static final StargateMilkyWayMemberBlock STARGATE_MILKY_WAY_MEMBER_BLOCK = new StargateMilkyWayMemberBlock();
    public static final StargateUniverseMemberBlock STARGATE_UNIVERSE_MEMBER_BLOCK = new StargateUniverseMemberBlock();
    public static final StargateOrlinMemberBlock STARGATE_ORLIN_MEMBER_BLOCK = new StargateOrlinMemberBlock();
    public static final StargatePegasusMemberBlock STARGATE_PEGASUS_MEMBER_BLOCK = new StargatePegasusMemberBlock();

    public static final CapacitorBlockEmpty CAPACITOR_BLOCK_EMPTY = new CapacitorBlockEmpty();
    public static final CapacitorBlock CAPACITOR_BLOCK = new CapacitorBlock();
    public static final BeamerBlock BEAMER_BLOCK = new BeamerBlock();

    // -----------------------------------------------------------------------------

    public static final TRPlatformBlock TR_PLATFORM_BLOCK = new TRPlatformBlock();

    // -----------------------------------------------------------------------------

    /**
     * Used in {@link TRControllerAbstractTile#updateLinkStatus()}
     */
    public static final JSGBlock[] RINGS_BLOCKS = {
            TRANSPORT_RINGS_GOAULD_BLOCK,
            TRANSPORT_RINGS_ORI_BLOCK
    };
    /**
     * Used in {@link TransportRingsAbstractTile#updateLinkStatus()}
     */
    public static final JSGBlock[] RINGS_CONTROLLERS = {
            TR_CONTROLLER_GOAULD_BLOCK
    };
    /**
     * Used in {@link GDOItem#onUpdate}
     */
    public static final JSGBlock[] STARGATE_BASE_BLOCKS = {
            STARGATE_PEGASUS_BASE_BLOCK,
            STARGATE_MILKY_WAY_BASE_BLOCK,
            STARGATE_UNIVERSE_BASE_BLOCK,
            STARGATE_ORLIN_BASE_BLOCK
    };
    /**
     * Used in {@link StargateClassicMemberBlock}
     */
    public static final JSGBlock[] CAMO_BLOCKS_BLACKLIST = {
            CAPACITOR_BLOCK,
            CAPACITOR_BLOCK_EMPTY,
            DHD_PEGASUS_BLOCK,
            DHD_BLOCK,
            TR_CONTROLLER_GOAULD_BLOCK,
            TRANSPORT_RINGS_GOAULD_BLOCK,
            TRANSPORT_RINGS_ORI_BLOCK,
            BEAMER_BLOCK
    };


    private static final JSGBlock[] blocks = {
            ORE_NAQUADAH_BLOCK,
            ORE_NAQUADAH_BLOCK_STONE,
            NAQUADAH_BLOCK,
            NAQUADAH_BLOCK_RAW,

            ORE_TRINIUM_BLOCK,
            TRINIUM_BLOCK,

            ORE_TITANIUM_BLOCK,
            TITANIUM_BLOCK,

            STARGATE_MILKY_WAY_BASE_BLOCK,
            STARGATE_MILKY_WAY_MEMBER_BLOCK,

            STARGATE_UNIVERSE_BASE_BLOCK,
            STARGATE_UNIVERSE_MEMBER_BLOCK,

            STARGATE_PEGASUS_BASE_BLOCK,
            STARGATE_PEGASUS_MEMBER_BLOCK,

            STARGATE_ORLIN_BASE_BLOCK,
            STARGATE_ORLIN_MEMBER_BLOCK,

            DHD_BLOCK,
            DHD_PEGASUS_BLOCK,

            TRANSPORT_RINGS_GOAULD_BLOCK,
            TRANSPORT_RINGS_ORI_BLOCK,

            TR_CONTROLLER_GOAULD_BLOCK,

            TR_PLATFORM_BLOCK,

            CAPACITOR_BLOCK_EMPTY,
            CAPACITOR_BLOCK,

            BEAMER_BLOCK,

            INVISIBLE_BLOCK,
            IRIS_BLOCK,

    };

    public static boolean isInBlocksArray(Block block, Block[] array) {
        for (Block b : array) {
            if (block == b) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onRegisterBlocks(Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.registerAll(blocks);

        for (JSGBlock block : blocks) {
            if (block.hasTileEntity(block.getDefaultState())) {
                Class<? extends TileEntity> tileEntityClass = block.getTileEntityClass();
                ResourceLocation key = block.getRegistryName();
                if (key != null && tileEntityClass != null)
                    GameRegistry.registerTileEntity(tileEntityClass, key);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterItems(Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        for (JSGBlock block : blocks) {
            if (block instanceof StargateClassicMemberBlock)
                registry.register(((StargateClassicMemberBlock) block).getItemBlock());
            else if (block instanceof JSGAbstractCustomItemBlock)
                registry.register(((JSGAbstractCustomItemBlock) block).getItemBlock());
            else if(block.getRegistryName() != null)
                registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        for (JSGBlock block : blocks) {
            if (block instanceof StargateClassicMemberBlock) {
                StargateClassicMemberBlock b = (StargateClassicMemberBlock) block;
                Map<Integer, String> values = b.getAllMetaTypes();
                for (Integer meta : values.keySet())
                    ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(b), meta, new ModelResourceLocation(values.get(meta)));
            }
            else if (block instanceof JSGAbstractCustomMetaItemBlock) {
                JSGAbstractCustomMetaItemBlock b = (JSGAbstractCustomMetaItemBlock) block;
                Map<Integer, String> values = b.getAllMetaTypes();
                for (Integer meta : values.keySet())
                    ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(b), meta, new ModelResourceLocation(values.get(meta)));
            } else {
                ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
            }
        }
    }

    @Nullable
    public static Block remapBlock(String oldBlockName) {
        switch (oldBlockName) {
            case "jsg:stargatebase_block":
                return STARGATE_MILKY_WAY_BASE_BLOCK;

            case "jsg:stargate_member_block":
                return STARGATE_MILKY_WAY_MEMBER_BLOCK;

            case "jsg:stargatebase_orlin_block":
                return STARGATE_ORLIN_BASE_BLOCK;

            case "jsg:stargatemember_orlin_block":
                return STARGATE_ORLIN_MEMBER_BLOCK;

            case "jsg:transportrings_block":
                return TRANSPORT_RINGS_GOAULD_BLOCK;

            case "jsg:transportrings_controller_block":
                return TR_CONTROLLER_GOAULD_BLOCK;

            case "jsg:zpm":
            case "jsg:zpmhub_block":
            case "jsg:connector_zpm":
            case "jsg:holder_zpm":
            case "jsg:circuit_control_zpm":
                return Blocks.AIR;

            default:
                return null;
        }
    }

    @SubscribeEvent
    public static void onMissingBlockMappings(RegistryEvent.MissingMappings<Block> event) {
        for (Mapping<Block> mapping : event.getMappings()) {
            Block newBlock = remapBlock(mapping.key.toString());

            if (newBlock != null) mapping.remap(newBlock);
        }
    }

    @SubscribeEvent
    public static void onMissingItemMappings(RegistryEvent.MissingMappings<Item> event) {
        for (Mapping<Item> mapping : event.getMappings()) {
            Block newBlock = remapBlock(mapping.key.toString());
            if (newBlock != null) mapping.remap(ItemBlock.getItemFromBlock(newBlock));
        }
    }
}

