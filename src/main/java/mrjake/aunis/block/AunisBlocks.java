package mrjake.aunis.block;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.beamer.BeamerBlock;
import mrjake.aunis.block.capacitor.CapacitorBlock;
import mrjake.aunis.block.capacitor.CapacitorBlockEmpty;
import mrjake.aunis.block.dialhomedevice.DHDBlock;
import mrjake.aunis.block.dialhomedevice.DHDPegasusBlock;
import mrjake.aunis.block.invisible.InvisibleBlock;
import mrjake.aunis.block.invisible.IrisBlock;
import mrjake.aunis.block.ore.NaquadahOreBlock;
import mrjake.aunis.block.ore.TitaniumOreBlock;
import mrjake.aunis.block.ore.TriniumOreBlock;
import mrjake.aunis.block.stargate.*;
import mrjake.aunis.block.transportrings.TRControllerGoauldBlock;
import mrjake.aunis.block.transportrings.TransportRingsGoauldBlock;
import mrjake.aunis.block.transportrings.TransportRingsOriBlock;
import mrjake.aunis.block.zpm.ZPMBlock;
import mrjake.aunis.block.zpm.ZPMHubBlock;
import mrjake.aunis.item.*;
import mrjake.aunis.tileentity.*;
import mrjake.aunis.tileentity.dialhomedevice.DHDPegasusTile;
import mrjake.aunis.tileentity.dialhomedevice.DHDMilkyWayTile;
import mrjake.aunis.tileentity.energy.CapacitorTile;
import mrjake.aunis.tileentity.stargate.*;
import mrjake.aunis.tileentity.transportrings.TRControllerGoauldTile;
import mrjake.aunis.tileentity.transportrings.TransportRingsGoauldTile;
import mrjake.aunis.tileentity.transportrings.TransportRingsOriTile;
import mrjake.aunis.util.BlockHelpers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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

import static mrjake.aunis.Aunis.aunisOresCreativeTab;

@EventBusSubscriber
public class AunisBlocks {
    public static final NaquadahOreBlock ORE_NAQUADAH_BLOCK = new NaquadahOreBlock("naquadah_ore");
    public static final NaquadahOreBlock ORE_NAQUADAH_BLOCK_STONE = new NaquadahOreBlock("naquadah_ore_stone");
    public static final Block NAQUADAH_BLOCK = new Block(Material.IRON).setRegistryName(Aunis.ModID, "naquadah_block").setUnlocalizedName(Aunis.ModID + ".naquadah_block").setCreativeTab(aunisOresCreativeTab);

    public static final TriniumOreBlock ORE_TRINIUM_BLOCK = new TriniumOreBlock("trinium_ore");
    public static final TitaniumOreBlock ORE_TITANIUM_BLOCK = new TitaniumOreBlock("titanium_ore");

    // -----------------------------------------------------------------------------

    public static final Block NAQUADAH_BLOCK_RAW = BlockHelpers.createSimpleBlock("naquadah_block_raw", Material.IRON, aunisOresCreativeTab).setHardness(3);
    public static final Block TRINIUM_BLOCK = BlockHelpers.createSimpleBlock("trinium_block", Material.IRON, aunisOresCreativeTab);
    public static final Block TITANIUM_BLOCK = BlockHelpers.createSimpleBlock("titanium_block", Material.IRON, aunisOresCreativeTab);

    // -----------------------------------------------------------------------------

    public static final StargateMilkyWayBaseBlock STARGATE_MILKY_WAY_BASE_BLOCK = new StargateMilkyWayBaseBlock();
    public static final StargateUniverseBaseBlock STARGATE_UNIVERSE_BASE_BLOCK = new StargateUniverseBaseBlock();
    public static final StargateOrlinBaseBlock STARGATE_ORLIN_BASE_BLOCK = new StargateOrlinBaseBlock();
    public static final StargatePegasusBaseBlock STARGATE_PEGASUS_BASE_BLOCK = new StargatePegasusBaseBlock();

    public static final DHDBlock DHD_BLOCK = new DHDBlock();
    public static final DHDPegasusBlock DHD_PEGASUS_BLOCK = new DHDPegasusBlock();

    public static final TransportRingsGoauldBlock TRANSPORT_RINGS_BLOCK = new TransportRingsGoauldBlock();
    public static final TransportRingsOriBlock TRANSPORT_RINGS_ORI_BLOCK = new TransportRingsOriBlock();
    public static final TRControllerGoauldBlock TR_CONTROLLER_BLOCK = new TRControllerGoauldBlock();
    public static final InvisibleBlock INVISIBLE_BLOCK = new InvisibleBlock();
    public static final IrisBlock IRIS_BLOCK = new IrisBlock();

    // -----------------------------------------------------------------------------

    public static final ZPMHubBlock ZPM_HUB = new ZPMHubBlock();
    public static final ZPMBlock ZPM = new ZPMBlock();

    // -----------------------------------------------------------------------------

    public static final StargateMilkyWayMemberBlock STARGATE_MILKY_WAY_MEMBER_BLOCK = new StargateMilkyWayMemberBlock();
    public static final StargateUniverseMemberBlock STARGATE_UNIVERSE_MEMBER_BLOCK = new StargateUniverseMemberBlock();
    public static final StargateOrlinMemberBlock STARGATE_ORLIN_MEMBER_BLOCK = new StargateOrlinMemberBlock();
    public static final StargatePegasusMemberBlock STARGATE_PEGASUS_MEMBER_BLOCK = new StargatePegasusMemberBlock();

    public static final CapacitorBlockEmpty CAPACITOR_BLOCK_EMPTY = new CapacitorBlockEmpty();
    public static final CapacitorBlock CAPACITOR_BLOCK = new CapacitorBlock();
    public static final BeamerBlock BEAMER_BLOCK = new BeamerBlock();

    /**
     * Used in {@link mrjake.aunis.tileentity.transportrings.TRControllerAbstractTile#updateLinkStatus()}
     */
    public static final Block[] RINGS_BLOCKS = {
            TRANSPORT_RINGS_BLOCK,
            TRANSPORT_RINGS_ORI_BLOCK
    };
    /**
     * Used in {@link mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile#updateLinkStatus()}
     */
    public static final Block[] RINGS_CONTROLLERS = {
            TR_CONTROLLER_BLOCK
    };

    public static boolean isInBlocksArray(Block block, Block[] array){
        for(Block b : array){
            if(block == b) {
                return true;
            }
        }
        return false;
    }


    private static final Block[] blocks = {
            ORE_NAQUADAH_BLOCK, ORE_NAQUADAH_BLOCK_STONE, NAQUADAH_BLOCK,

            ORE_TRINIUM_BLOCK, ORE_TITANIUM_BLOCK,

            NAQUADAH_BLOCK_RAW, TRINIUM_BLOCK, TITANIUM_BLOCK,

            STARGATE_MILKY_WAY_BASE_BLOCK, STARGATE_UNIVERSE_BASE_BLOCK, STARGATE_ORLIN_BASE_BLOCK, STARGATE_ORLIN_MEMBER_BLOCK, STARGATE_PEGASUS_BASE_BLOCK,

            DHD_BLOCK, DHD_PEGASUS_BLOCK,

            TRANSPORT_RINGS_BLOCK,

            TR_CONTROLLER_BLOCK,

            INVISIBLE_BLOCK, IRIS_BLOCK,

            CAPACITOR_BLOCK_EMPTY, BEAMER_BLOCK,

            // ZPM_HUB,

            TRANSPORT_RINGS_ORI_BLOCK,


    };

    @SubscribeEvent
    public static void onRegisterBlocks(Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.registerAll(blocks);
        registry.register(STARGATE_MILKY_WAY_MEMBER_BLOCK);
        registry.register(STARGATE_UNIVERSE_MEMBER_BLOCK);
        registry.register(STARGATE_PEGASUS_MEMBER_BLOCK);
        registry.register(CAPACITOR_BLOCK);
        //registry.register(ZPM);


        registerTile(StargateMilkyWayBaseTile.class, AunisBlocks.STARGATE_MILKY_WAY_BASE_BLOCK.getRegistryName());
        registerTile(StargateUniverseBaseTile.class, AunisBlocks.STARGATE_UNIVERSE_BASE_BLOCK.getRegistryName());
        registerTile(StargateOrlinBaseTile.class, AunisBlocks.STARGATE_ORLIN_BASE_BLOCK.getRegistryName());
        registerTile(StargatePegasusBaseTile.class, AunisBlocks.STARGATE_PEGASUS_BASE_BLOCK.getRegistryName());

        registerTile(StargateMilkyWayMemberTile.class, AunisBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK.getRegistryName());
        registerTile(StargateUniverseMemberTile.class, AunisBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK.getRegistryName());
        registerTile(StargateOrlinMemberTile.class, AunisBlocks.STARGATE_ORLIN_MEMBER_BLOCK.getRegistryName());
        registerTile(StargatePegasusMemberTile.class, AunisBlocks.STARGATE_PEGASUS_MEMBER_BLOCK.getRegistryName());
        registerTile(DHDMilkyWayTile.class, AunisBlocks.DHD_BLOCK.getRegistryName());
        registerTile(DHDPegasusTile.class, AunisBlocks.DHD_PEGASUS_BLOCK.getRegistryName());
        registerTile(TransportRingsGoauldTile.class, AunisBlocks.TRANSPORT_RINGS_BLOCK.getRegistryName());
        registerTile(TransportRingsOriTile.class, AunisBlocks.TRANSPORT_RINGS_ORI_BLOCK.getRegistryName());
        registerTile(TRControllerGoauldTile.class, AunisBlocks.TR_CONTROLLER_BLOCK.getRegistryName());
        registerTile(CapacitorTile.class, AunisBlocks.CAPACITOR_BLOCK.getRegistryName());
        registerTile(BeamerTile.class, AunisBlocks.BEAMER_BLOCK.getRegistryName());

        //registerTile(ZPMHubTile.class, AunisBlocks.ZPM_HUB.getRegistryName());
        //registerTile(ZPMTile.class, AunisBlocks.ZPM.getRegistryName());
    }

    public static void registerTile(Class<? extends TileEntity> tileEntityClass, ResourceLocation key){
        if(key == null) return;
        GameRegistry.registerTileEntity(tileEntityClass, key);
    }

    @SubscribeEvent
    public static void onRegisterItems(Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        for (Block block : blocks)
            registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));

        registry.register(new StargateMilkyWayMemberItemBlock(STARGATE_MILKY_WAY_MEMBER_BLOCK));
        registry.register(new StargateUniverseMemberItemBlock(STARGATE_UNIVERSE_MEMBER_BLOCK));
        registry.register(new StargatePegasusMemberItemBlock(STARGATE_PEGASUS_MEMBER_BLOCK));
        registry.register(new CapacitorItemBlock(CAPACITOR_BLOCK));
        //registry.register(new ZPMItemBlock(ZPM));
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        for (Block block : blocks) {
            ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
        }

        ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(STARGATE_MILKY_WAY_MEMBER_BLOCK), STARGATE_MILKY_WAY_MEMBER_BLOCK.RING_META, new ModelResourceLocation("aunis:stargate_milkyway_ring_block"));
        ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(STARGATE_MILKY_WAY_MEMBER_BLOCK), STARGATE_MILKY_WAY_MEMBER_BLOCK.CHEVRON_META, new ModelResourceLocation("aunis:stargate_milkyway_chevron_block"));
        ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(STARGATE_UNIVERSE_MEMBER_BLOCK), STARGATE_UNIVERSE_MEMBER_BLOCK.RING_META, new ModelResourceLocation("aunis:stargate_universe_ring_block"));
        ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(STARGATE_UNIVERSE_MEMBER_BLOCK), STARGATE_UNIVERSE_MEMBER_BLOCK.CHEVRON_META, new ModelResourceLocation("aunis:stargate_universe_chevron_block"));
        ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(STARGATE_PEGASUS_MEMBER_BLOCK), STARGATE_PEGASUS_MEMBER_BLOCK.RING_META, new ModelResourceLocation("aunis:stargate_pegasus_ring_block"));
        ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(STARGATE_PEGASUS_MEMBER_BLOCK), STARGATE_PEGASUS_MEMBER_BLOCK.CHEVRON_META, new ModelResourceLocation("aunis:stargate_pegasus_chevron_block"));

        ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(CAPACITOR_BLOCK), 0, new ModelResourceLocation(CAPACITOR_BLOCK.getRegistryName(), "inventory"));
        //ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(ZPM), 0, new ModelResourceLocation(ZPM.getRegistryName(), "inventory"));
    }

    @Nullable
    public static Block remapBlock(String oldBlockName) {
        switch (oldBlockName) {
            case "aunis:stargatebase_block":
                return STARGATE_MILKY_WAY_BASE_BLOCK;

            case "aunis:stargate_member_block":
                return STARGATE_MILKY_WAY_MEMBER_BLOCK;

            case "aunis:stargatebase_orlin_block":
                return STARGATE_ORLIN_BASE_BLOCK;

            case "aunis:stargatemember_orlin_block":
                return STARGATE_ORLIN_MEMBER_BLOCK;

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
            switch (mapping.key.toString()) {
                case "aunis:stargatebase_block":
                    mapping.remap(ItemBlock.getItemFromBlock(STARGATE_MILKY_WAY_BASE_BLOCK));
                    break;

                case "aunis:stargate_member_block":
                    mapping.remap(ItemBlock.getItemFromBlock(STARGATE_MILKY_WAY_MEMBER_BLOCK));
                    break;

                case "aunis:stargatebase_orlin_block":
                    mapping.remap(ItemBlock.getItemFromBlock(STARGATE_ORLIN_BASE_BLOCK));
                    break;

                case "aunis:stargatemember_orlin_block":
                    mapping.remap(ItemBlock.getItemFromBlock(STARGATE_ORLIN_MEMBER_BLOCK));
                    break;
            }
        }
    }
}

