package mrjake.aunis;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.capability.endpoint.ItemEndpointCapability;
import mrjake.aunis.chunkloader.ChunkLoadingCallback;
import mrjake.aunis.command.AunisCommands;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.StargateDimensionConfig;
import mrjake.aunis.config.StargateSizeEnum;
import mrjake.aunis.creativetabs.*;
import mrjake.aunis.datafixer.TileNamesFixer;
import mrjake.aunis.entity.EntityRegister;
import mrjake.aunis.fluid.AunisFluids;
import mrjake.aunis.gui.base.AunisGuiHandler;
import mrjake.aunis.integration.OCWrapperInterface;
import mrjake.aunis.integration.ThermalIntegration;
import mrjake.aunis.integration.TConstructIntegration;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.proxy.IProxy;
import mrjake.aunis.worldgen.AunisOresGenerator;
import mrjake.aunis.worldgen.structures.AunisStructuresGenerator;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

@Mod( modid = Aunis.ModID, name = Aunis.Name, version = Aunis.Version, acceptedMinecraftVersions = Aunis.MCVersion, dependencies = "after:cofhcore@[4.6.0,);after:opencomputers;after:thermalexpansion;after:tconstruct")
public class Aunis {	
    public static final String ModID = "aunis";
    public static final String Name = "Aunis";
    // I didn't manage to make it work
    //public static final String Version = "${version}"; // It works only in final builds.
    public static final String Version = "@VERSION@";
    public static final int DATA_VERSION = 14;

    //public static final String MCVersion = "${mcversion}";
    public static final String MCVersion = "@MCVERSION@";

    public static final String CLIENT = "mrjake.aunis.proxy.ProxyClient";
    public static final String SERVER = "mrjake.aunis.proxy.ProxyServer";

    public static SoundCategory AUNIS_SOUNDS;
    
    public static final AunisGatesCreativeTabBuilder aunisGatesCreativeTab = new AunisGatesCreativeTabBuilder();
    public static final AunisRingsCreativeTabBuilder aunisRingsCreativeTab = new AunisRingsCreativeTabBuilder();
    public static final AunisItemsCreativeTabBuilder aunisItemsCreativeTab = new AunisItemsCreativeTabBuilder();
    public static final AunisOresCreativeTabBuilder aunisOresCreativeTab = new AunisOresCreativeTabBuilder();
    public static final AunisEnergyCreativeTabBuilder aunisEnergyCreativeTab = new AunisEnergyCreativeTabBuilder();

    @Instance(ModID)
	public static Aunis instance;
    
    @SidedProxy(clientSide = Aunis.CLIENT, serverSide = Aunis.SERVER)
    public static IProxy proxy;
    public static Logger logger;
        
    // ------------------------------------------------------------------------
    // OpenComputers
    
    private static final String OC_WRAPPER_LOADED = "mrjake.aunis.integration.OCWrapperLoaded";
    private static final String OC_WRAPPER_NOT_LOADED = "mrjake.aunis.integration.OCWrapperNotLoaded";
    
    public static OCWrapperInterface ocWrapper;
    
	// ------------------------------------------------------------------------

    // Thermal Expansion recipes convert
    public static boolean isThermalLoaded = false;

    static {
    	FluidRegistry.enableUniversalBucket();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog(); // This is the recommended way of getting a logger
        File source = event.getSourceFile();
        Aunis.info("Started loading Aunis mod in " + source.getAbsolutePath());
        Aunis.info("Loading Aunis version " + Version);

        AUNIS_SOUNDS = SoundCategory.BLOCKS;

        AunisPacketHandler.registerPackets();
        Aunis.info("Successfully registered Packets!");

        AunisFluids.registerFluids();
        Aunis.info("Successfully registered Fluids!");

        // Tinkers Construct
        if (Loader.isModLoaded("tconstruct") && AunisConfig.integrationsConfig.tConstructIntegration) {
            Aunis.info("TConstruct found and connection is enabled... Connecting...");
            TConstructIntegration.initFluids();
            Aunis.info("Successfully connected into TConstruct!");
        }

    	StargateDimensionConfig.load(event.getModConfigurationDirectory());

        EntityRegister.registerEntities();
        Aunis.info("Successfully registered Entities!");
    	
        proxy.preInit(event);
    }
 
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	GameRegistry.registerWorldGenerator(new AunisOresGenerator(), 0);
    	GameRegistry.registerWorldGenerator(new AunisStructuresGenerator(), 0);
        Aunis.info("Successfully registered World Generation!");

    	NetworkRegistry.INSTANCE.registerGuiHandler(instance, new AunisGuiHandler());
    	ItemEndpointCapability.register();
		ForgeChunkManager.setForcedChunkLoadingCallback(Aunis.instance, ChunkLoadingCallback.INSTANCE);
        registerOreDictionary();
        Aunis.info("Successfully registered OreDictionary!");

        // ThermalExpansion
        if(Loader.isModLoaded("thermalexpansion") && AunisConfig.integrationsConfig.tExpansionIntegration) {
            Aunis.info("Thermal Expansion found and connection is enabled... Connecting...");

            ThermalIntegration.registerRecipes();
            isThermalLoaded = true;
            Aunis.info("Successfully connected into Thermal Expansion!");
        }

    	// ----------------------------------------------------------------------------------------------------------------
    	// OpenComputers
    	
    	try {
	    	if (Loader.isModLoaded("opencomputers") && AunisConfig.integrationsConfig.ocIntegration) {
                Aunis.info("OpenComputers found and connection is enabled... Connecting...");
                ocWrapper = (OCWrapperInterface) Class.forName(OC_WRAPPER_LOADED).newInstance();
                Aunis.info("Successfully connected into OpenComputers!");
            }
	    	else
	    		ocWrapper = (OCWrapperInterface) Class.forName(OC_WRAPPER_NOT_LOADED).newInstance();
    	}
    	
    	catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
    		logger.error("Exception loading OpenComputers wrapper");
    		e.printStackTrace();
    	}
    	
    	
    	// ----------------------------------------------------------------------------------------------------------------
    	// Data fixers
    	
		ModFixs modFixs = ((CompoundDataFixer) FMLCommonHandler.instance().getDataFixer()).init(ModID, DATA_VERSION);
		modFixs.registerFix(FixTypes.BLOCK_ENTITY, new TileNamesFixer());

        StargateSizeEnum.init();
        Aunis.info("Successfully registered Stargate sizes!");

        AunisFluids.registerWaterLogs();
        Aunis.info("Successfully registered Water logged blocks! - jk... maybe later");
    	proxy.init(event);
    }

    // register ores
    public void registerOreDictionary(){
        regODOre("oreNaquadahRaw", AunisBlocks.ORE_NAQUADAH_BLOCK);
        regODOre("oreNaquadahRaw", AunisBlocks.ORE_NAQUADAH_BLOCK_STONE);
        regODOre("gemNaquadahRaw", AunisItems.NAQUADAH_SHARD);
        regODOre("ingotNaquadahRaw", AunisItems.NAQUADAH_ALLOY_RAW);
        regODOre("ingotNaquadahRefined", AunisItems.NAQUADAH_ALLOY);
        regODOre("blockNaquadahRefined", AunisBlocks.NAQUADAH_BLOCK);
        regODOre("blockNaquadahRaw", AunisBlocks.NAQUADAH_BLOCK_RAW);

        regODOre("oreTrinium", AunisBlocks.ORE_TRINIUM_BLOCK);
        regODOre("oreTitanium", AunisBlocks.ORE_TITANIUM_BLOCK);
        regODOre("ingotTrinium", AunisItems.TRINIUM_INGOT);
        regODOre("ingotTitanium", AunisItems.TITANIUM_INGOT);
        regODOre("blockTrinium", AunisBlocks.TRINIUM_BLOCK);
        regODOre("blockTitanium", AunisBlocks.TITANIUM_BLOCK);
    }

    public void regODOre(String name, Item ore){
        OreDictionary.registerOre(name, ore);
    }
    public void regODOre(String name, Block ore){
        OreDictionary.registerOre(name, ore);
    }
 
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) throws IOException {    	
    	proxy.postInit(event);
        Aunis.info("Aunis loaded!");
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    	AunisCommands.registerCommands(event);
        Aunis.info("Successfully registered Commands!");
    }
    
    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) throws IOException {    	
    	StargateDimensionConfig.update();
        Aunis.info("Server started!");
    }

    /**
     * Shorthand for {@code Aunis.logger.info}.
     * Only for temporary logging info.
     */
	public static void info(String string) {
		logger.info(string);
	}

    public static void warn(String string) {
        logger.warn(string);
    }

    public static void error(String string){
	    logger.error(string);
    }
}
