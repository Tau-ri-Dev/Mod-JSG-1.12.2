package mrjake.aunis;

import mrjake.aunis.command.AunisCommands;
import mrjake.aunis.config.stargate.StargateDimensionConfig;
import mrjake.aunis.integration.OCWrapperInterface;
import mrjake.aunis.proxy.IProxy;
import mrjake.aunis.util.main.loader.AunisInit;
import mrjake.aunis.util.main.loader.AunisPreInit;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(modid = Aunis.MOD_ID, name = Aunis.MOD_NAME, version = Aunis.MOD_VERSION, acceptedMinecraftVersions = Aunis.MC_VERSION, dependencies = "after:cofhcore@[4.6.0,);after:opencomputers;after:thermalexpansion;after:tconstruct")
public class Aunis {

    // --------------------------------------------
    // CONSTANTS

    public static final String MOD_ID = "aunis";
    public static final String MOD_NAME = "Just Stargate Mod";
    public static final String MOD_VERSION = "@VERSION@";
    public static final int DATA_VERSION = 18;
    public static final String CONFIG_VERSION = "1.0";
    public static final String MC_VERSION = "@MCVERSION@";
    public static final String CLIENT = "mrjake.aunis.proxy.ProxyClient";
    public static final String SERVER = "mrjake.aunis.proxy.ProxyServer";
    public static final String AGS_PATH = "pastebin run pAqHB264";
    public static SoundCategory AUNIS_SOUNDS;

    // --------------------------------------------
    // VARIABLES

    public static Logger logger;
    public static OCWrapperInterface ocWrapper;
    public static boolean isThermalLoaded = false;
    public static String clientModPath;

    // --------------------------------------------
    // PROXY

    @SidedProxy(clientSide = Aunis.CLIENT, serverSide = Aunis.SERVER)
    public static IProxy proxy;

    // --------------------------------------------
    // INSTANCE

    @Instance(MOD_ID)
    public static Aunis instance;

    // --------------------------------------------
    // Enable forge buckets

    static {
        FluidRegistry.enableUniversalBucket();
    }

    // --------------------------------------------
    // SHORTHAND

    public static void info(String string) {
        logger.info(string);
    }

    public static void warn(String string) {
        logger.warn(string);
    }

    public static void error(String string) {
        logger.error(string);
    }

    public static void debug(String string) {
        logger.debug(string);
    }

    // --------------------------------------------
    // USED IN ITEMS

    public static String getInProgress() {
        return TextFormatting.AQUA + "Work In Progress Item!";
    }

    // --------------------------------------------
    // REGISTER

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        AunisPreInit.preInit(event);
        Aunis.proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        AunisInit.init(event);
        Aunis.proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event){
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

    @EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        Aunis.info("Good bye! Thank you for using Aunis: Resurrection :)");
    }
}
