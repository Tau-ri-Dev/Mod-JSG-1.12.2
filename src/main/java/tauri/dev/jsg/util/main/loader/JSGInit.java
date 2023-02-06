package tauri.dev.jsg.util.main.loader;

import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.advancements.JSGAdvancements;
import tauri.dev.jsg.capability.CapabilityEnergyZPM;
import tauri.dev.jsg.capability.endpoint.ItemEndpointCapability;
import tauri.dev.jsg.chunkloader.ChunkLoadingCallback;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.craftings.CraftingConfig;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.config.structures.StructureConfig;
import tauri.dev.jsg.datafixer.TileNamesFixer;
import tauri.dev.jsg.gui.JSGGuiHandler;
import tauri.dev.jsg.integration.OCWrapperInterface;
import tauri.dev.jsg.integration.ThermalIntegration;
import tauri.dev.jsg.machine.assembler.AssemblerRecipes;
import tauri.dev.jsg.machine.chamber.CrystalChamberRecipes;
import tauri.dev.jsg.machine.orewashing.OreWashingRecipes;
import tauri.dev.jsg.machine.pcbfabricator.PCBFabricatorRecipes;
import tauri.dev.jsg.worldgen.JSGOresGenerator;
import tauri.dev.jsg.worldgen.structures.EnumStructures;
import tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator;

public class JSGInit {

    private static final String OC_WRAPPER_LOADED = "tauri.dev.jsg.integration.OCWrapperLoaded";
    private static final String OC_WRAPPER_NOT_LOADED = "tauri.dev.jsg.integration.OCWrapperNotLoaded";

    @SuppressWarnings("unused")
    public static void init(FMLInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new JSGOresGenerator(), 0);
        GameRegistry.registerWorldGenerator(new JSGStructuresGenerator(), 0);
        JSG.info("Successfully registered World Generation!");

        NetworkRegistry.INSTANCE.registerGuiHandler(JSG.instance, new JSGGuiHandler());
        ForgeChunkManager.setForcedChunkLoadingCallback(JSG.instance, ChunkLoadingCallback.INSTANCE);
        JSGOreDictionary.registerOreDictionary();
        JSG.info("Successfully registered OreDictionary!");

        ItemEndpointCapability.register();
        JSG.info("Successfully registered Capabilities (phase 2)!");

        // Advancements
        JSGAdvancements.register();

        // ThermalExpansion
        registerThermal();

        // OpenComputers
        registerOC();

        // Data fixers
        fixData();

        // Crafting by our machines
        AssemblerRecipes.addToConfig();
        CrystalChamberRecipes.addToConfig();
        PCBFabricatorRecipes.addToConfig();
        OreWashingRecipes.addToConfig();

        CraftingConfig.load(JSG.modConfigDir);
        JSG.info("Successfully loaded CraftingConfig!");

        // Structures config
        EnumStructures.initConfig();

        StructureConfig.load(JSG.modConfigDir);
        JSG.info("Successfully loaded StructureConfig!");

        StargateSizeEnum.init();
        JSG.info("Successfully registered Stargate sizes!");

        ConfigManager.sync(JSG.MOD_ID, Config.Type.INSTANCE);
    }

    public static void fixData() {
        ModFixs modFixs = FMLCommonHandler.instance().getDataFixer().init(JSG.MOD_ID, JSG.DATA_VERSION);
        modFixs.registerFix(FixTypes.BLOCK_ENTITY, new TileNamesFixer());
    }

    public static void registerThermal() {
        if (Loader.isModLoaded("thermalexpansion") && JSGConfig.General.integration.tExpansionIntegration) {
            JSG.info("Thermal Expansion found and connection is enabled... Connecting...");

            ThermalIntegration.registerRecipes();
            JSG.info("Successfully connected into Thermal Expansion!");
        }
    }

    public static void registerOC() {
        try {
            if (Loader.isModLoaded("opencomputers") && JSGConfig.General.integration.ocIntegration) {
                JSG.info("OpenComputers found and connection is enabled... Connecting...");
                JSG.ocWrapper = (OCWrapperInterface) Class.forName(OC_WRAPPER_LOADED).newInstance();
                JSG.info("Successfully connected into OpenComputers!");
            } else
                JSG.ocWrapper = (OCWrapperInterface) Class.forName(OC_WRAPPER_NOT_LOADED).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            JSG.error("Exception loading OpenComputers wrapper");
            e.printStackTrace();
        }
    }
}
