package mrjake.aunis.util.main.loader;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.stargate.StargateDimensionConfig;
import mrjake.aunis.entity.EntityRegister;
import mrjake.aunis.fluid.AunisFluids;
import mrjake.aunis.integration.TConstructIntegration;
import mrjake.aunis.packet.AunisPacketHandler;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class AunisPreInit {

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        Aunis.logger = event.getModLog();

        // mod file path - used in updater
        File source = event.getSourceFile();
        Aunis.clientModPath = source.getAbsolutePath();

        Aunis.AUNIS_SOUNDS = SoundCategory.BLOCKS;

        Aunis.info("Started loading Aunis mod in " + Aunis.clientModPath);
        Aunis.info("Loading Aunis version " + Aunis.MOD_VERSION);

        AunisPacketHandler.registerPackets();
        Aunis.info("Successfully registered Packets!");

        AunisFluids.registerFluids();
        Aunis.info("Successfully registered Fluids!");

        // Tinkers Construct
        registerTIC();

        StargateDimensionConfig.load(event.getModConfigurationDirectory());
        Aunis.info("Successfully registered Dimensions!");

        EntityRegister.registerEntities();
        Aunis.info("Successfully registered Entities!");
    }

    public static void registerTIC(){
        if (Loader.isModLoaded("tconstruct") && AunisConfig.integrationsConfig.tConstructIntegration) {
            Aunis.info("TConstruct found and connection is enabled... Connecting...");
            TConstructIntegration.initFluids();
            Aunis.info("Successfully connected into TConstruct!");
        }
    }
}
