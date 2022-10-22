package tauri.dev.jsg.util.main.loader;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.capability.endpoint.ItemEndpointCapability;
import tauri.dev.jsg.chunkloader.ChunkLoadingCallback;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.datafixer.TileNamesFixer;
import tauri.dev.jsg.gui.JSGGuiHandler;
import tauri.dev.jsg.integration.OCWrapperInterface;
import tauri.dev.jsg.integration.ThermalIntegration;
import tauri.dev.jsg.worldgen.JSGOresGenerator;
import tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class JSGInit {

    private static final String OC_WRAPPER_LOADED = "tauri.dev.jsg.integration.OCWrapperLoaded";
    private static final String OC_WRAPPER_NOT_LOADED = "tauri.dev.jsg.integration.OCWrapperNotLoaded";

    public static void init(FMLInitializationEvent event){
        GameRegistry.registerWorldGenerator(new JSGOresGenerator(), 0);
        GameRegistry.registerWorldGenerator(new JSGStructuresGenerator(), 0);
        JSG.info("Successfully registered World Generation!");

        NetworkRegistry.INSTANCE.registerGuiHandler(JSG.instance, new JSGGuiHandler());
        ItemEndpointCapability.register();
        ForgeChunkManager.setForcedChunkLoadingCallback(JSG.instance, ChunkLoadingCallback.INSTANCE);
        JSGOreDictionary.registerOreDictionary();
        JSG.info("Successfully registered OreDictionary!");

        // ThermalExpansion
        registerThermal();

        // OpenComputers
        registerOC();

        // Data fixers
        fixData();

        StargateSizeEnum.init();
        JSG.info("Successfully registered Stargate sizes!");

        //JSGFluids.registerWaterLogs();
        //JSG.info("Successfully registered Water logged blocks! - jk... maybe later");
    }

    public static void fixData(){
        ModFixs modFixs = ((CompoundDataFixer) FMLCommonHandler.instance().getDataFixer()).init(JSG.MOD_ID, JSG.DATA_VERSION);
        modFixs.registerFix(FixTypes.BLOCK_ENTITY, new TileNamesFixer());
    }

    public static void registerThermal(){
        if(Loader.isModLoaded("thermalexpansion") && tauri.dev.jsg.config.JSGConfig.integrationsConfig.tExpansionIntegration) {
            JSG.info("Thermal Expansion found and connection is enabled... Connecting...");

            ThermalIntegration.registerRecipes();
            JSG.info("Successfully connected into Thermal Expansion!");
        }
    }

    public static void registerOC(){
        try {
            if (Loader.isModLoaded("opencomputers") && JSGConfig.integrationsConfig.ocIntegration) {
                JSG.info("OpenComputers found and connection is enabled... Connecting...");
                JSG.ocWrapper = (OCWrapperInterface) Class.forName(OC_WRAPPER_LOADED).newInstance();
                JSG.info("Successfully connected into OpenComputers!");
            }
            else
                JSG.ocWrapper = (OCWrapperInterface) Class.forName(OC_WRAPPER_NOT_LOADED).newInstance();
        }

        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            JSG.logger.error("Exception loading OpenComputers wrapper");
            e.printStackTrace();
        }
    }
}
