package tauri.dev.jsg;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.command.JSGCommands;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.event.EventTickClient;
import tauri.dev.jsg.integration.OCWrapperInterface;
import tauri.dev.jsg.loader.ReloadListener;
import tauri.dev.jsg.proxy.IProxy;
import tauri.dev.jsg.util.main.loader.JSGInit;
import tauri.dev.jsg.util.main.loader.JSGPreInit;

import java.io.File;
import java.io.IOException;

@Mod(modid = JSG.MOD_ID, name = JSG.MOD_NAME, version = JSG.MOD_VERSION, acceptedMinecraftVersions = JSG.MC_VERSION, dependencies = "after:cofhcore@[4.6.0,);after:opencomputers;after:thermalexpansion;after:tconstruct")
@Mod.EventBusSubscriber
public class JSG {

    // --------------------------------------------
    // CONSTANTS

    public static final String MOD_ID = "jsg";
    public static final String MOD_NAME = "Just Stargate Mod";
    public static final String MOD_VERSION = "@VERSION@";
    public static final int DATA_VERSION = 23;
    public static final String CONFIG_GENERAL_VERSION = "2.4";
    public static final String CONFIG_CRAFTINGS_VERSION = "1.1";
    public static final String CONFIG_STRUCTURES_VERSION = "1.0";
    public static final String CONFIG_DIMENSIONS_VERSION = "1.1";
    public static final String MC_VERSION = "@MCVERSION@";
    public static final String CLIENT = "tauri.dev.jsg.proxy.ProxyClient";
    public static final String SERVER = "tauri.dev.jsg.proxy.ProxyServer";
    public static final String AGS_PATH = "pastebin run pAqHB264";
    public static SoundCategory JSG_SOUNDS;

    // --------------------------------------------
    // VARIABLES

    public static Logger logger;
    public static File modConfigDir;
    public static OCWrapperInterface ocWrapper;
    public static File clientModPath;
    public static File modsDirectory;

    // --------------------------------------------
    // PROXY

    @SidedProxy(clientSide = JSG.CLIENT, serverSide = JSG.SERVER)
    public static IProxy proxy;

    // --------------------------------------------
    // INSTANCE

    @Instance(MOD_ID)
    public static JSG instance;

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

    public static void info(Object... o) {
        logger.info(o);
    }

    public static void info(String string, Exception e) {
        logger.info(string, e);
    }

    public static void warn(String string) {
        logger.warn(string);
        ReloadListener.LoadingStats.warnings++;
    }

    public static void warn(Object... p) {
        logger.warn(p);
        ReloadListener.LoadingStats.warnings++;
    }

    public static void warn(String string, Exception e) {
        logger.warn(string, e);
        ReloadListener.LoadingStats.warnings++;
    }

    public static void error(String string) {
        logger.error(string);
        ReloadListener.LoadingStats.errors++;
    }

    public static void error(Object... o) {
        logger.error(o);
        ReloadListener.LoadingStats.errors++;
    }

    public static void error(String string, Exception e) {
        logger.error(string, e);
        ReloadListener.LoadingStats.errors++;
    }

    public static void debug(String string) {
        logger.debug(string);
        if(JSGConfig.debugConfig.logDebugAsInfo)
            info("[DEBUG] " + string);
    }

    public static void debug(Object... o) {
        logger.debug(o);
        if(JSGConfig.debugConfig.logDebugAsInfo)
            info("[DEBUG] ", o);
    }

    public static void debug(String string, Exception e) {
        logger.debug(string, e);
        if(JSGConfig.debugConfig.logDebugAsInfo)
            info("[DEBUG] " + string, e);
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
        JSGBlocks.load();
        JSGPreInit.preInit(event);
        JSG.proxy.preInit(event);

        Runtime.getRuntime().addShutdownHook(new Thread(JSG::shutDown));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        if (Loader.isModLoaded("aunis")) {
            Loader.instance().runtimeDisableMod("aunis");
            throw new LoaderException("Found two same mods! Just Stargate Mod and The Aunis mod are the SAME mods!");
        }

        JSGInit.init(event);
        JSG.proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        JSG.info("Just Stargate Mod loading completed!");
        proxy.loadCompleted();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        JSGCommands.registerCommands(event);
        JSG.info("Successfully registered Commands!");
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) throws IOException {
        StargateDimensionConfig.update();
        JSG.info("Server started!");
    }

    @EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
    }

    /**
     * Contains las pos of player (client side) - helps to debug sound in main menu.
     *
     * Updated in {@link EventTickClient}
     */
    public static BlockPos lastPlayerPosInWorld = new BlockPos(0, 0, 0);

    public static void shutDown() {
        JSG.proxy.shutDown();
        JSG.info("Good bye! Thank you for using Just Stargate Mod :)");
    }
}
