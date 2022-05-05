package mrjake.aunis.util.main.loader;

import mrjake.aunis.Aunis;
import mrjake.aunis.capability.endpoint.ItemEndpointCapability;
import mrjake.aunis.chunkloader.ChunkLoadingCallback;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.stargate.StargateSizeEnum;
import mrjake.aunis.datafixer.TileNamesFixer;
import mrjake.aunis.gui.base.AunisGuiHandler;
import mrjake.aunis.integration.OCWrapperInterface;
import mrjake.aunis.integration.ThermalIntegration;
import mrjake.aunis.worldgen.AunisOresGenerator;
import mrjake.aunis.worldgen.structures.AunisStructuresGenerator;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class AunisInit {

    private static final String OC_WRAPPER_LOADED = "mrjake.aunis.integration.OCWrapperLoaded";
    private static final String OC_WRAPPER_NOT_LOADED = "mrjake.aunis.integration.OCWrapperNotLoaded";

    public static void init(FMLInitializationEvent event){
        GameRegistry.registerWorldGenerator(new AunisOresGenerator(), 0);
        GameRegistry.registerWorldGenerator(new AunisStructuresGenerator(), 0);
        Aunis.info("Successfully registered World Generation!");

        NetworkRegistry.INSTANCE.registerGuiHandler(Aunis.instance, new AunisGuiHandler());
        ItemEndpointCapability.register();
        ForgeChunkManager.setForcedChunkLoadingCallback(Aunis.instance, ChunkLoadingCallback.INSTANCE);
        AunisOreDictionary.registerOreDictionary();
        Aunis.info("Successfully registered OreDictionary!");

        // ThermalExpansion
        registerThermal();

        // OpenComputers
        registerOC();

        // Data fixers
        fixData();

        StargateSizeEnum.init();
        Aunis.info("Successfully registered Stargate sizes!");

        //AunisFluids.registerWaterLogs();
        //Aunis.info("Successfully registered Water logged blocks! - jk... maybe later");
    }

    public static void fixData(){
        ModFixs modFixs = ((CompoundDataFixer) FMLCommonHandler.instance().getDataFixer()).init(Aunis.MOD_ID, Aunis.DATA_VERSION);
        modFixs.registerFix(FixTypes.BLOCK_ENTITY, new TileNamesFixer());
    }

    public static void registerThermal(){
        if(Loader.isModLoaded("thermalexpansion") && AunisConfig.integrationsConfig.tExpansionIntegration) {
            Aunis.info("Thermal Expansion found and connection is enabled... Connecting...");

            ThermalIntegration.registerRecipes();
            Aunis.isThermalLoaded = true;
            Aunis.info("Successfully connected into Thermal Expansion!");
        }
    }

    public static void registerOC(){
        try {
            if (Loader.isModLoaded("opencomputers") && AunisConfig.integrationsConfig.ocIntegration) {
                Aunis.info("OpenComputers found and connection is enabled... Connecting...");
                Aunis.ocWrapper = (OCWrapperInterface) Class.forName(OC_WRAPPER_LOADED).newInstance();
                Aunis.info("Successfully connected into OpenComputers!");
            }
            else
                Aunis.ocWrapper = (OCWrapperInterface) Class.forName(OC_WRAPPER_NOT_LOADED).newInstance();
        }

        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Aunis.logger.error("Exception loading OpenComputers wrapper");
            e.printStackTrace();
        }
    }
}
