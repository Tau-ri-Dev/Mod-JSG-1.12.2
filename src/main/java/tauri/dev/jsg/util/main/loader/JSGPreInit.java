package tauri.dev.jsg.util.main.loader;

import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.entity.EntityRegister;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.integration.TConstructIntegration;
import tauri.dev.jsg.machine.assembler.AssemblerRecipe;
import tauri.dev.jsg.machine.assembler.AssemblerRecipes;
import tauri.dev.jsg.machine.chamber.CrystalChamberRecipe;
import tauri.dev.jsg.machine.chamber.CrystalChamberRecipes;
import tauri.dev.jsg.packet.JSGPacketHandler;

import java.io.File;

public class JSGPreInit {

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        JSG.logger = event.getModLog();

        // mod file path - used in updater
        File source = event.getSourceFile();
        JSG.clientModPath = source.getAbsolutePath();

        JSG.JSG_SOUNDS = SoundCategory.BLOCKS;

        JSG.info("Started loading JSG mod in " + JSG.clientModPath);
        JSG.info("Loading JSG version " + JSG.MOD_VERSION);

        JSGPacketHandler.registerPackets();
        JSG.info("Successfully registered Packets!");

        JSGFluids.registerFluids();
        JSG.info("Successfully registered Fluids!");

        // Tinkers Construct
        registerTIC();

        StargateDimensionConfig.load(event.getModConfigurationDirectory());
        JSG.info("Successfully registered Dimensions!");

        EntityRegister.registerEntities();
        JSG.info("Successfully registered Entities!");
    }

    public static void registerTIC() {
        if (Loader.isModLoaded("tconstruct") && JSGConfig.integrationsConfig.tConstructIntegration) {
            JSG.info("TConstruct found and connection is enabled... Connecting...");
            TConstructIntegration.initFluids();
            JSG.info("Successfully connected into TConstruct!");
        }
    }
}
