package tauri.dev.jsg.util.main.loader;

import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.capability.CapabilityEnergyZPM;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.entity.EntityRegister;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.integration.TConstructIntegration;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.sound.JSGSoundCategory;

import java.io.File;

public class JSGPreInit {

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        JSG.logger = event.getModLog();
        JSG.modConfigDir = event.getModConfigurationDirectory();

        // mod file path - used in updater
        JSG.clientModPath = event.getSourceFile();
        JSG.modsDirectory = event.getSourceFile().getParentFile();

        //JSG.JSG_SOUNDS = JSGSoundCategory.add("jsg");
        JSG.JSG_SOUNDS = SoundCategory.BLOCKS;

        JSG.info("Started loading JSG mod in " + JSG.clientModPath.getAbsolutePath());
        JSG.info("Mods directory: " + JSG.modsDirectory.getAbsolutePath());
        JSG.info("Loading JSG version " + JSG.MOD_VERSION);

        JSG.displayWelcomeMessage();

        CapabilityEnergyZPM.register();
        JSG.info("Successfully registered Capabilities (phase 1)!");

        JSGPacketHandler.registerPackets();
        JSG.info("Successfully registered Packets!");

        JSGFluids.registerFluids();
        JSG.info("Successfully registered Fluids!");

        // Tinkers Construct
        registerTIC();

        StargateDimensionConfig.load(JSG.modConfigDir);
        JSG.info("Successfully registered Dimensions!");

        EntityRegister.registerEntities();
        JSG.info("Successfully registered Entities!");
    }

    public static void registerTIC() {
        if (Loader.isModLoaded("tconstruct") && JSGConfig.General.integration.tConstructIntegration) {
            JSG.info("TConstruct found and connection is enabled... Connecting...");
            TConstructIntegration.initFluids();
            JSG.info("Successfully connected into TConstruct!");
        }
    }
}
