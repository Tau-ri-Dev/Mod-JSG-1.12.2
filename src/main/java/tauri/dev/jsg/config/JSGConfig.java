package tauri.dev.jsg.config;

import net.minecraftforge.common.config.Config;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.config.stargate.StargateTimeLimitModeEnum;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.util.math.TemperatureHelper;

import java.util.HashMap;
import java.util.Map;

import static tauri.dev.jsg.JSG.AGS_DEFAULT_PATH;

public class JSGConfig {
    private static final String CONFIG_FILE_NAME = "jsg/jsgConfig_" + JSG.CONFIG_GENERAL_VERSION + "/";

    @Config(modid = JSG.MOD_ID, name = CONFIG_FILE_NAME + "general")
    @Config.LangKey("config.jsg.general")
    public static class General {
        @Config.Name("Check for updates")
        @Config.Comment({
                "Should JSG check for update on startup?",
                "SIDE: CLIENT"
        })
        public static boolean enableAutoUpdater = true;

        @Config.Name("AGS path")
        @Config.Comment({
                "The pastebin of AGS (used in /ags command)",
                "SIDE: SERVER"
        })
        public static String agsPath = AGS_DEFAULT_PATH;

        @Config.Name("Audio")
        @Config.Comment({
                "General Audio settings"
        })
        public static Audio audio = new Audio();

        @Config.Name("Visual")
        @Config.Comment({
                "General Video settings"
        })
        public static Visual visual = new Visual();

        @Config.Name("Integration")
        @Config.Comment({
                "Integration settings"
        })
        public static Integration integration = new Integration();

        @Config.Name("Debug")
        @Config.Comment({
                "Debug settings"
        })
        public static Debug debug = new Debug();

        @Config.Name("Advancements")
        @Config.Comment({
                "Advancements settings"
        })
        public static AdvancementsConfig advancementsConfig = new AdvancementsConfig();

        @Config.Name("Countdown")
        @Config.Comment({
                "Countdown settings"
        })
        public static CountDownConfig countdownConfig = new CountDownConfig();

        @Config.Name("MainMenu")
        @Config.Comment({
                "Main Menu settings"
        })
        public static MainMenuConfig mainMenuConfig = new MainMenuConfig();

        @Config.Name("Development")
        @Config.Comment({
                "Developer settings"
        })
        public static DevConfig devConfig = new DevConfig();

        public static class Audio {
            @Config.Name("JSG volume")
            @Config.Comment({
                    "Specifies volume of sounds from JSG",
                    "SIDE: CLIENT"
            })
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public float volume = 1;
        }

        public static class Visual {
            @Config.Name("Notebook page Glyph transparency")
            @Config.Comment({
                    "Specifies transparency of glyphs on notebook page",
                    "SIDE: CLIENT"
            })
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public double glyphTransparency = 0.75;

            @Config.Name("Notebook Page offset")
            @Config.Comment({
                    "Greater values render the Page more to the center of the screen, smaller render it closer to the borders.",
                    "0 - for standard 16:9 (default),",
                    "0.2 - for 4:3.",
                    "SIDE: CLIENT"
            })
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public float pageNarrowing = 0;

            @Config.Name("Render emissive textures")
            @Config.Comment({
                    "Render light of some textures.",
                    "Disable this if it causes lags.",
                    "SIDE: CLIENT"
            })
            public boolean renderEmissive = true;

            @Config.Name("Change title to w/ JSG")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean changeTitle = true;

            @Config.Name("Temperature unit")
            @Config.Comment({
                    "Specifies what unit will be used to display temperatures",
                    "SIDE: CLIENT"
            })
            public TemperatureHelper.EnumTemperatureUnit temperatureUnit = TemperatureHelper.EnumTemperatureUnit.CELSIUS;

            @Config.Name("Destiny CO2 blaster particles count/tick")
            @Config.RangeInt(min = 0, max = 100)
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public int destinyVentParticlesCount = 5;
        }

        public static class Debug {
            @Config.Name("Check gate merge")
            @Config.Comment({
                    "Check if gate is merged?",
                    "SIDE: SERVER"
            })
            public boolean checkGateMerge = true;

            @Config.Name("Render bounding boxes")
            @Config.Comment({
                    "Render bounding boxes of stargates?",
                    "SIDE: CLIENT"
            })
            public boolean renderBoundingBoxes = false;

            @Config.Name("Render whole kawoosh bounding box")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean renderWholeKawooshBoundingBox = false;

            @Config.Name("Render invisible blocks")
            @Config.Comment({
                    "Should render iris/stargate merged/rings barricade blocks?",
                    "SIDE: CLIENT"
            })
            public boolean renderInvisibleBlocks = false;

            @Config.Name("Show loading textures in log")
            @Config.Comment({
                    "Should log every single texture while loading the mod?",
                    "SIDE: SERVER/CLIENT"
            })
            public boolean logTexturesLoading = false;

            @Config.Name("Log debug messages as info")
            @Config.Comment({
                    "If debug console not working, should display debug as info?",
                    "SIDE: SERVER/CLIENT"
            })
            public boolean logDebugAsInfo = false;

            @Config.Name("Memory needed to run mod/modpack (GB)")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public float neededRAM = 2.0f;
        }

        public static class Integration {
            @Config.RequiresMcRestart
            @Config.Name("Enable Tinkers' Construct integration")
            @Config.Comment({
                    "WARNING! - Requires reloading!",
                    "SIDE: SERVER/CLIENT"
            })
            public boolean tConstructIntegration = true;

            @Config.RequiresMcRestart
            @Config.Name("Enable Open Computers integration")
            @Config.Comment({
                    "WARNING! - Requires reloading!",
                    "SIDE: SERVER/CLIENT"
            })
            public boolean ocIntegration = true;

            @Config.Name("OC wireless network range (in blocks)")
            @Config.RangeInt(min = 0, max = 100)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int ocIntegrationWirelessRange = 20;

            @Config.RequiresMcRestart
            @Config.Name("Enable Thermal Expansion integration")
            @Config.Comment({
                    "WARNING! - Requires reloading!",
                    "SIDE: SERVER/CLIENT"
            })
            public boolean tExpansionIntegration = true;

            @Config.RequiresMcRestart
            @Config.Name("Enable Fluid Logged API integration")
            @Config.Comment({
                    "WARNING! - Requires reloading!",
                    "SIDE: SERVER/CLIENT"
            })
            public boolean flapiIntegration = true;
        }

        public static class AdvancementsConfig {
            @Config.Name("Ranged Advancements radius")
            @Config.Comment({
                    "Players in this radius around triggered pos will get Advancement.",
                    "SIDE: SERVER"
            })
            @Config.RangeInt(min = 0, max = 100)
            @Config.SlidingOption
            public int radius = 25;
        }

        public static class CountDownConfig {
            @Config.Name("Delay after zero-time (seconds)")
            @Config.RangeInt(min = 0, max = 60)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int zeroDelay = 5;

            @Config.Name("Delay to start dialing after countdown start (seconds)")
            @Config.RangeInt(min = 0, max = 60)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int dialStartDelay = 5;
        }

        @SuppressWarnings("unused")

        public static class DevConfig {
            @Config.Name("Dev mode")
            public boolean enableDevMode = false;
            @Config.Name("t1")
            public boolean t1 = false;
            @Config.Name("x")
            public float x = 0f;
            @Config.Name("y")
            public float y = 0f;
            @Config.Name("z")
            public float z = 0f;
            @Config.Name("x2")
            public float x2 = 0f;
            @Config.Name("y2")
            public float y2 = 0f;
            @Config.Name("z2")
            public float z2 = 0f;
            @Config.Name("s")
            public float s = 1f;
            @Config.Name("sz")
            public float sz = 0f;
            @Config.Name("yz")
            public float yz = 0f;
            @Config.Name("tz")
            public float tz = 0f;
        }

        public static class MainMenuConfig {
            @Config.RequiresMcRestart
            @Config.Name("Disable JSG main menu")
            @Config.Comment({
                    "Disables showing custom main menu",
                    "WARNING! - Requires reloading!",
                    "SIDE: CLIENT"
            })
            public boolean disableJSGMainMenu = false;

            @Config.RequiresMcRestart
            @Config.Name("Background images count")
            @Config.Comment({
                    "Specifies how many images can be used as background of mainmenu. (starts from 0)",
                    "DO NOT CHANGE THIS IF YOU DO NOT KNOW WHAT ARE YOU DOING!",
                    "WARNING! - Requires reloading!",
                    "SIDE: CLIENT"
            })
            public int backgroundImagesCount = 7;

            @Config.RequiresMcRestart
            @Config.Name("Enable Tau'ri logo on startup")
            @Config.Comment({
                    "WARNING! - Requires reloading!",
                    "SIDE: CLIENT"
            })
            public boolean enableLogo = true;

            @Config.Name("Enable debug mode")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean debugMode = false;

            @Config.Name("Play music in main menu")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean playMusic = true;

            @Config.Name("Enable sync with minecraft ticks")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean syncEnabled = false;

            @Config.Name("Enable changing gate type")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean enableGateChanging = true;

            @Config.Name("Enable loading music")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean loadingMusic = true;


            @Config.Name("Gate ring rotation coefficient")
            @Config.RangeDouble(min = 0.1, max = 3.0)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public double ringRotationCoefficient = 1.0;
        }
    }

    @Config(modid = JSG.MOD_ID, name = CONFIG_FILE_NAME + "zpm", category = "zpm")
    @Config.LangKey("config.jsg.zpm")
    public static class ZPM {
        @Config.Name("Power")
        @Config.Comment({
                "ZPM Power settings"
        })
        public static Power power = new Power();

        public static class Power {
            @Config.Name("ZPM capacity (RF)")
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public double zpmCapacity = 4_398_046_511_104D;

            @Config.Name("ZPMHub's max power throughput")
            @Config.RangeInt(min = 1, max = 1043600)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int zpmHubMaxEnergyTransfer = 104360;
        }

    }

    @Config(modid = JSG.MOD_ID, name = CONFIG_FILE_NAME + "stargates", category = "stargates")
    @Config.LangKey("config.jsg.stargates")
    public static class Stargate {

        @Config.Name("Stargate size")
        @Config.Comment({
                "Defines size of stargate's model",
                "SIDE: SERVER/CLIENT"
        })
        @Config.RequiresWorldRestart
        public static StargateSizeEnum stargateSize = StargateSizeEnum.MEDIUM;

        @Config.Name("Mechanics")
        @Config.Comment({
                "Stargate Mechanics settings"
        })
        public static Mechanics mechanics = new Mechanics();

        @Config.Name("RIG")
        @Config.Comment({
                "Stargate Random Incoming Generator settings"
        })
        public static RIG rig = new RIG();

        @Config.Name("Iris")
        @Config.Comment({
                "Stargate Iris settings"
        })
        public static Iris iris = new Iris();

        @Config.Name("Power")
        @Config.Comment({
                "Stargate Power settings"
        })
        public static Power power = new Power();

        @Config.Name("Visual")
        @Config.Comment({
                "Stargate Video settings"
        })
        public static Visual visual = new Visual();

        @Config.Name("Origins")
        @Config.Comment({
                "Stargate Origins settings"
        })
        public static PointOfOrigins pointOfOrigins = new PointOfOrigins();

        @Config.Name("EventHorizon")
        @Config.Comment({
                "Stargate Event Horizon settings"
        })
        public static EventHorizon eventHorizon = new EventHorizon();

        @Config.Name("AutoClose")
        @Config.Comment({
                "Stargate Auto Close settings"
        })
        public static AutoCloseConfig autoClose = new AutoCloseConfig();

        @Config.Name("OpenTimeLimit")
        @Config.Comment({
                "Stargate Open Time Limit settings"
        })
        public static OpenLimitConfig openLimit = new OpenLimitConfig();

        public static class Mechanics {
            @Config.Name("Enable burried state for gates")
            @Config.Comment({
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                    "SIDE: SERVER"
            })
            public boolean enableBurriedState = true;

            @Config.Name("Orlin's gate max open count")
            @Config.RangeInt(min = 0, max = 15000)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public int stargateOrlinMaxOpenCount = 2;

            @Config.Name("Universe dialer nearby radius")
            @Config.RangeInt(min = 5)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int universeGateNearbyReach = 1024;

            @Config.Name("Enable gate overheat with explosion")
            @Config.Comment({
                    "Should gate explode when its overheated?",
                    "This method is not implemented yet!",
                    "SIDE: SERVER"
            })
            public boolean enableGateOverHeatExplosion = false;

            @Config.RequiresMcRestart
            @Config.Name("Max stargate heat")
            @Config.RangeDouble(min = 0)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public double gateMaxHeat = 83400;

            @Config.Name("Chance of lighting strike that charge a gate")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public float lightingBoldChance = 0.0005f;

            @Config.Name("DIM IDs where lighting strike should not charge gates")
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int[] lightingStrikeDisabledDims = new int[]{1, -1};

            @Config.Name("Connect to dialing gate")
            @Config.Comment({
                    "If target gate is dialing and this option is set to true,",
                    "the target gate will stop dialing and open incoming wormhole.",
                    "If this is set to false and the dialed gate dialing address,",
                    "the connection will not established.",
                    "If it cause issues, set it to false.",
                    "SIDE: SERVER"
            })
            public boolean allowConnectToDialing = true;

            @Config.Name("Use 8 chevrons between MW and PG gates")
            @Config.Comment({
                    "Change this to true, if you want to use 8 chevrons between pegasus and milkyway gates",
                    "SIDE: SERVER"
            })
            public boolean pegAndMilkUseEightChevrons = true;

            @Config.Name("Need only 7 symbols between Uni gates")
            @Config.Comment({
                    "If you want to dial UNI-UNI only with seven symbols (interdimensional for example), set this to true",
                    "SIDE: SERVER"
            })
            public boolean useStrictSevenSymbolsUniGate = false;
        }

        public static class Iris {
            @Config.Name("Iris kills at destination")
            @Config.Comment({
                    "If set to 'false' player get killed by iris on entering event horizon",
                    "SIDE: SERVER"
            })
            public boolean killAtDestination = true;

            @Config.Name("Titanium iris durability")
            @Config.Comment({
                    "Durability of Titanium iris",
                    "set it to 0, if u want to make it unbreakable",
                    "SIDE: SERVER/CLIENT"
            })
            @Config.RangeInt(min = 0, max = 50000)
            public int titaniumIrisDurability = 500;

            @Config.Name("Trinium iris durability")
            @Config.Comment({
                    "Durability of Trinium iris",
                    "set it to 0, if u want to make it unbreakable",
                    "SIDE: SERVER/CLIENT"
            })
            @Config.RangeInt(min = 0, max = 50000)
            public int triniumIrisDurability = 1000;

            @Config.Name("Shield power draw")
            @Config.Comment({
                    "Energy/tick used for make shield closed",
                    "SIDE: SERVER"
            })
            @Config.RangeInt(min = 0, max = 500000)
            public int shieldPowerDraw = 500;

            @Config.Name("Allow creative bypass")
            @Config.Comment({
                    "Set it to true, if u want to bypass",
                    "shield/iris damage by creative gamemode",
                    "SIDE: SERVER"
            })
            public boolean allowCreative = false;

            @Config.Name("Maximum iris code length")
            @Config.RangeInt(min = 0, max = 32)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public int irisCodeLength = 9;

            @Config.Name("Can iris destroy blocks")
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public boolean irisDestroysBlocks = false;

            @Config.Name("Unbreaking chance per level")
            @Config.Comment({
                    "0 - disables unbreaking on iris",
                    "100 - unbreaking makes iris unbreakable",
                    "SIDE: SERVER"
            })
            @Config.RangeInt(min = 0, max = 100)
            @Config.SlidingOption
            public int unbreakingChance = 10;

            @Config.Name("Enable iris overheat collapse")
            @Config.Comment({
                    "Should iris break when its overheated?",
                    "SIDE: SERVER"
            })
            public boolean enableIrisOverHeatCollapse = true;

            @Config.RequiresMcRestart
            @Config.Name("Max titanium iris heat")
            @Config.RangeDouble(min = 0)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public double irisTitaniumMaxHeat = 1668;

            @Config.RequiresMcRestart
            @Config.Name("Max trinium iris heat")
            @Config.RangeDouble(min = 0)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public double irisTriniumMaxHeat = 3336;
        }

        public static class Power {
            @Config.Name("Stargate's internal buffer size")
            @Config.RangeInt(min = 4608)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public int stargateEnergyStorage = 71280000;

            @Config.Name("Stargate's max power throughput")
            @Config.RangeInt(min = 1, max = 500000)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int stargateMaxEnergyTransfer = 26360;

            @Config.Name("Stargate wormhole open power draw")
            @Config.RangeInt(min = 0, max = 500000)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int openingBlockToEnergyRatio = 4608;

            @Config.Name("Stargate wormhole sustain power draw")
            @Config.RangeInt(min = 0, max = 50)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int keepAliveBlockToEnergyRatioPerTick = 2;

            @Config.Name("Stargate instability threshold")
            @Config.Comment({
                    "Seconds of energy left before gate becomes unstable",
                    "SIDE: SERVER"
            })
            @Config.RangeInt(min = 1, max = 120)
            @Config.SlidingOption
            public int instabilitySeconds = 20;

            @Config.Name("Orlin's gate energy multiplier")
            @Config.RangeDouble(min = 0, max = 100)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public double stargateOrlinEnergyMul = 2.0;

            @Config.Name("Universe gate energy multiplier")
            @Config.RangeDouble(min = 0, max = 100)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public double stargateUniverseEnergyMul = 1.5;

            @Config.Name("Capacitors supported by Universe gates")
            @Config.Comment({
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                    "SIDE: SERVER/CLIENT"
            })
            @Config.RangeInt(min = 0, max = 3)
            @Config.SlidingOption
            public int universeCapacitors = 0;

            @Config.Name("Stargate eight symbols address power mul")
            @Config.Comment({
                    "Specifies the multiplier of power needed to keep the gate alive",
                    "when 8-symbols address is dialed",
                    "SIDE: SERVER"
            })
            @Config.RangeDouble(min = 0, max = 100)
            @Config.SlidingOption
            public float eightSymbolAddressMul = 1.3f;

            @Config.Name("Stargate nine symbols address power mul")
            @Config.Comment({
                    "Specifies the multiplier of power needed to keep the gate alive",
                    "when 9-symbols address is dialed",
                    "SIDE: SERVER"
            })
            @Config.RangeDouble(min = 0, max = 100)
            @Config.SlidingOption
            public float nineSymbolAddressMul = 1.7f;
        }

        public static class Visual {
            @Config.Name("Temperature threshold for frosty overlay")
            @Config.Comment({
                    "Below this biome temperature the gate will receive frosty texture.",
                    "Set to negative value to disable.",
                    "SIDE: CLIENT"
            })
            @Config.RangeDouble(min = 0, max = 5)
            @Config.SlidingOption
            public float frostyTemperatureThreshold = 0.1f;

            @Config.Name("Camo blocks blacklist")
            @Config.Comment({
                    "Specify what blocks can not be used as camo for gates.",
                    "These blocks are only additional. There are also blocks that are internally coded",
                    "and can not be deleted from the blacklist!",
                    "Format: \"modid:block[:meta/*]\", for example: ",
                    "\"minecraft:stone:2\"",
                    "\"minecraft:cobblestone\"",
                    "\"minecraft:concrete:*\"",
                    "SIDE: SERVER/CLIENT"
            })
            public String[] camoBlacklist = new String[]{};

            @Config.Name("Biome overlay biome matches")
            @Config.Comment({
                    "This check comes last (after block is directly under sky (except Nether) and temperature is high enough).",
                    "You can disable the temperature check by setting it to a negative value.",
                    "Format: \"modid:biomename\", for example: ",
                    "\"minecraft:dark_forest\"",
                    "\"minecraft:forest\"",
                    "SIDE: SERVER/CLIENT"
            })
            public Map<String, String[]> biomeMatches = new HashMap<String, String[]>() {
                {
                    put(BiomeOverlayEnum.NORMAL.toString(), new String[]{});
                    put(BiomeOverlayEnum.FROST.toString(), new String[]{});
                    put(BiomeOverlayEnum.MOSSY.toString(), new String[]{
                            "minecraft:jungle",
                            "minecraft:jungle_hills",
                            "minecraft:jungle_edge",
                            "minecraft:mutated_jungle",
                            "minecraft:mutated_jungle_edge"
                    });
                    put(BiomeOverlayEnum.AGED.toString(), new String[]{});
                    put(BiomeOverlayEnum.SOOTY.toString(), new String[]{
                            "minecraft:hell"
                    });
                }
            };

            @Config.Name("Biome overlay override blocks")
            @Config.Comment({
                    "Format: \"modid:blockid[:meta]\", for example: ",
                    "\"minecraft:wool:7\"",
                    "\"minecraft:stone\"",
                    "SIDE: SERVER/CLIENT"
            })
            public Map<String, String[]> biomeOverrideBlocks = new HashMap<String, String[]>() {
                {
                    put(BiomeOverlayEnum.NORMAL.toString(), new String[]{"minecraft:stone"});
                    put(BiomeOverlayEnum.FROST.toString(), new String[]{"minecraft:ice"});
                    put(BiomeOverlayEnum.MOSSY.toString(), new String[]{"minecraft:vine"});
                    put(BiomeOverlayEnum.AGED.toString(), new String[]{"minecraft:cobblestone"});
                    put(BiomeOverlayEnum.SOOTY.toString(), new String[]{"minecraft:coal_block"});
                }
            };

            @Config.Name("Allow incoming animations")
            @Config.Comment({
                    "If the incoming animations of gates generate issues, set it to false",
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                    "SIDE: SERVER"
            })
            public boolean allowIncomingAnimations = true;

            @Config.Name("Faster MilkyWay and Universe gates computer dial")
            @Config.RequiresMcRestart
            @Config.Comment({
                    "Speed up dialing with computer on MW and UNI gates",
                    "SIDE: SERVER/CLIENT"
            })
            public boolean fasterMWGateDial = false;

            @Config.Name("Enable fast dialing of gates")
            @Config.Comment({
                    "Enable fast dialing on gates by default",
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                    "SIDE: SERVER/CLIENT"
            })
            public boolean enableFastDialing = false;

            @Config.Name("Render not placed blocks of s stargate")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean renderStargateNotPlaced = true;
        }

        public static class PointOfOrigins {
            @Config.Name("Enable different Point Of Origins for MW gate")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean enableDiffOrigins = true;

            @Config.RequiresMcRestart
            @Config.Name("Custom added points of origin")
            @Config.Comment({
                    "Specifies Point Of Origins that were added by any resource pack.",
                    "This options is required to load all models of added origins!",
                    "Format: \"id:name\", for example: ",
                    "\"6:Tollan\"",
                    "\"7:P4X-256\"",
                    "!DO NOT CHANGE ANYTHING IF YOU DON'T KNOW WHAT ARE YOU DOING!",
                    "SIDE: CLIENT/SERVER"
            })
            public String[] additionalOrigins = {};
        }

        public static class EventHorizon {
            @Config.Name("Disable animated Event Horizon")
            @Config.RequiresMcRestart
            @Config.Comment({
                    "Changing this option will require you to reload resources manually.",
                    "Just restart your game",
                    "SIDE: CLIENT"
            })
            public boolean disableAnimatedEventHorizon = false;

            @Config.Name("Enable wrong side killing")
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public boolean wrongSideKilling = true;

            @Config.Name("Unstable Event Horizon chance of death")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public float ehDeathChance = 0.07f;

            @Config.Name("Disable new kawoosh model (from 4.11.0.0)")
            @Config.RequiresMcRestart
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean disableNewKawoosh = false;

            @Config.Name("Kawoosh invincible blocks")
            @Config.Comment({
                    "Format: \"modid:blockid[:meta/*]\", for example: ",
                    "\"minecraft:wool:7\"",
                    "\"minecraft:stone\"",
                    "\"minecraft:concrete:*\"",
                    "SIDE: SERVER"
            })
            public String[] kawooshInvincibleBlocks = {
                    "minecraft:snow_layer:*",
                    "minecraft:rail:*",
                    "minecraft:golden_rail:*",
                    "minecraft:detector_rail:*",
                    "minecraft:activator_rail:*",
                    "minecraft:carpet:*",
                    "minecraft:stone_pressure_plate:*",
                    "minecraft:wooden_pressure_plate:*",
                    "minecraft:light_weighted_pressure_plate:*",
                    "minecraft:heavy_weighted_pressure_plate:*"
            };

            @Config.Name("Render EHs even if they are not rendering")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean renderEHifTheyNot = true;
        }

        public static class AutoCloseConfig {
            @Config.Name("Autoclose enabled")
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public boolean autocloseEnabled = true;

            @Config.Name("Seconds to autoclose with no players nearby")
            @Config.RangeInt(min = 1, max = 300)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int secondsToAutoclose = 5;
        }

        public static class OpenLimitConfig {
            @Config.Name("Maximum seconds of gate should be open")
            @Config.Comment({
                    "(in seconds (2280 = 38 minutes))",
                    "SIDE: SERVER"
            })
            @Config.RangeInt(min = 5, max = 3000)
            public int maxOpenedSeconds = 240;

            @Config.Name("Gate open time limit mode")
            @Config.Comment({
                    "What happens after gate's open time reaches limit?",
                    "SIDE: SERVER"
            })
            public StargateTimeLimitModeEnum maxOpenedWhat = StargateTimeLimitModeEnum.DRAW_MORE_POWER;

            @Config.Name("Power draw after opened time limit")
            @Config.RangeInt(min = 0, max = 50000)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int maxOpenedPowerDrawAfterLimit = 10000;
        }

        public static class RIG {
            @Config.Name("Enable random incoming wormholes")
            @Config.Comment({
                    "Enable random incoming wormholes generator",
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                    "SIDE: SERVER"
            })
            public boolean enableRandomIncoming = true;

            @Config.Name("Chance of spawning")
            @Config.Comment({
                    "10 = 1%",
                    "SIDE: SERVER"
            })
            @Config.RangeInt(min = 1, max = 100)
            @Config.SlidingOption
            public int chance = 1;

            @Config.Name("Entities to spawn")
            @Config.Comment({
                    "Format: \"modid:entityid\", for example: ",
                    "\"minecraft:zombie\"",
                    "\"minecraft:creeper\"",
                    "SIDE: SERVER"
            })
            public String[] entitiesToSpawn = {
                    "minecraft:zombie",
                    "minecraft:skeleton"
            };
        }
    }

    @Config(modid = JSG.MOD_ID, name = CONFIG_FILE_NAME + "transportrings", category = "transportrings")
    @Config.LangKey("config.jsg.rings")
    public static class Rings {
        @Config.Name("Mechanics")
        @Config.Comment({
                "Rings Mechanics settings"
        })
        public static Mechanics mechanics = new Mechanics();

        @Config.Name("Power")
        @Config.Comment({
                "Rings Power settings"
        })
        public static Power power = new Power();

        public static class Mechanics {
            @Config.Name("Rings range's radius horizontal")
            @Config.RangeInt(min = 1, max = 256)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int rangeFlat = 25;

            @Config.Name("Rings vertical reach")
            @Config.RangeInt(min = 1, max = 256)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int rangeVertical = 256;

            @Config.Name("Ignore rings check for blocks to replace")
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public boolean ignoreObstructionCheck = false;
        }

        public static class Power {
            @Config.Name("Transport Rings active power draw")
            @Config.Comment({
                    "Energy extracted from rings every tick when they are active (calculated by distance from these rings)",
                    "SIDE: SERVER"
            })
            @Config.RangeInt(min = 0, max = 100)
            @Config.SlidingOption
            public int ringsKeepAliveBlockToEnergyRatioPerTick = 2;

            @Config.Name("Transport Rings teleport power draw")
            @Config.Comment({
                    "Energy extracted from rings when they teleport LIVING entity (not drop)",
                    "SIDE: SERVER"
            })
            @Config.RangeInt(min = 0, max = 25600)
            public int ringsTeleportPowerDraw = 640;
        }
    }

    @Config(modid = JSG.MOD_ID, name = CONFIG_FILE_NAME + "dhd", category = "dhd")
    @Config.LangKey("config.jsg.dhd")
    public static class DialHomeDevice {
        @Config.Name("Mechanics")
        @Config.Comment({
                "DHD Mechanics settings"
        })
        public static Mechanics mechanics = new Mechanics();

        @Config.Name("Power")
        @Config.Comment({
                "DHD Power settings"
        })
        public static Power power = new Power();

        @Config.Name("Audio")
        @Config.Comment({
                "DHD Audio settings"
        })
        public static Audio audio = new Audio();

        @Config.Name("Visual")
        @Config.Comment({
                "DHD Video settings"
        })
        public static Visual visual = new Visual();

        public static class Mechanics {
            @Config.Name("DHD range's radius horizontal")
            @Config.RangeInt(min = 1, max = 64)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int rangeFlat = 25;

            @Config.Name("DHD range's radius vertical")
            @Config.RangeInt(min = 1, max = 64)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int rangeVertical = 15;

            @Config.Name("Universe dialer max horizontal reach radius")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int universeDialerReach = 10;

            @Config.Name("DHD's max fluid capacity")
            @Config.RangeInt(min = 1, max = 128000)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public int fluidCapacity = 16000;
        }

        public static class Power {
            @Config.Name("Capacity upgrade multiplier")
            @Config.RangeDouble(min = 1.0, max = 5.0)
            @Config.Comment({
                    "When capacity upgrade is placed in the DHD,",
                    "then multiply internal capacity by this number",
                    "SIDE: SERVER/CLIENT"
            })
            @Config.SlidingOption
            public float capacityUpgradeMultiplier = 2f;

            @Config.Name("Energy per 1mB Naquadah")
            @Config.RangeInt(min = 1, max = 50000)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int energyPerNaquadah = 10240;

            @Config.Name("Generation multiplier")
            @Config.RangeInt(min = 1, max = 5)
            @Config.Comment({
                    "Energy per 1mB is multiplied by this",
                    "Consumed mB/t is equal to this",
                    "SIDE: SERVER"
            })
            @Config.SlidingOption
            public int powerGenerationMultiplier = 1;

            @Config.Name("Efficiency upgrade multiplier")
            @Config.RangeDouble(min = 1.0, max = 5.0)
            @Config.Comment({
                    "Energy per 1mB is multiplied by this",
                    "when efficiency upgrade is placed in the DHD",
                    "SIDE: SERVER"
            })
            @Config.SlidingOption
            public float efficiencyUpgradeMultiplier = 1.4f;

            @Config.Name("Cold fusion reactor activation energy level")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public double activationLevel = 0.9;

            @Config.Name("Cold fusion reactor deactivation energy level")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public double deactivationLevel = 0.98;
        }

        public static class Audio {
            @Config.Name("Enable press sound when dialing with computer")
            @Config.Comment({
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                    "SIDE: SERVER"
            })
            public boolean computerDialSound = false;
        }

        public static class Visual {
            @Config.Name("Enable hint when dialing on DHDs with notebook page")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean enablePageHint = true;

            @Config.Name("Dialing helper colors")
            @Config.Comment({
                    "Set colors of each dial helper button",
                    "You should use HEX values",
                    "SIDE: CLIENT"
            })
            public Map<String, String> pageHintColors = new HashMap<String, String>() {
                {
                    put("Normal", "#7FFFFF");
                    put("ExtraSymbols", "#E56BEE");
                    put("Origin", "#7FFF7F");
                }
            };

            @Config.Name("Enable opening last chevron while dialing with dhd")
            @Config.Comment({
                    "Enable opening last chevron while dialing milkyway gate with dhd",
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI",
                    "SIDE: SERVER"
            })
            public boolean dhdLastOpen = true;

            @Config.Name("Enable old mechanics of DHD OC dialing")
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public boolean enableOldBug = false;
        }
    }

    @Config(modid = JSG.MOD_ID, name = CONFIG_FILE_NAME + "beamers", category = "beamers")
    @Config.LangKey("config.jsg.beamers")
    public static class Beamer {
        @Config.Name("Mechanics")
        @Config.Comment({
                "Beamers Mechanics settings"
        })
        public static Mechanics mechanics = new Mechanics();

        @Config.Name("Container")
        @Config.Comment({
                "Beamers Container settings"
        })
        public static Container container = new Container();

        @Config.Name("Visual")
        @Config.Comment({
                "Beamers Video settings"
        })
        public static Visual visual = new Visual();

        @Config.Name("Power")
        @Config.Comment({
                "Beamers Power settings"
        })
        public static Power power = new Power();

        public static class Mechanics {
            @Config.Name("Max gate-beamer distance")
            @Config.RangeInt(min = 3, max = 50)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int reach = 10;

            @Config.Name("Interval of signals being send to OC about transfers (in ticks)")
            @Config.RangeInt(min = 10, max = 400)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int signalIntervalTicks = 20;

            @Config.Name("Damage entities in a beam")
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public boolean damageEntities = true;

            @Config.Name("Destroy blocks in a beam")
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public boolean destroyBlocks = true;
        }

        public static class Container {
            @Config.Name("Fluid buffer capacity")
            @Config.RangeInt(min = 3000, max = 600000)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public int fluidCapacity = 60000;

            @Config.Name("Energy buffer capacity")
            @Config.RangeInt(min = 3000)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public int energyCapacity = 17820000;

            @Config.Name("Energy buffer max transfer")
            @Config.RangeInt(min = 1)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int energyTransfer = 26360;

            @Config.Name("Fluid max transfer")
            @Config.RangeInt(min = 1, max = 5000)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int fluidTransfer = 100;

            @Config.Name("Item max transfer")
            @Config.RangeInt(min = 1, max = 64)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int itemTransfer = 4;
        }

        public static class Visual {
            @Config.Name("Should the beam be responsive to fluid color")
            @Config.Comment({
                    "SIDE: CLIENT"
            })
            public boolean enableFluidBeamColorization = true;
        }

        public static class Power {
            @Config.Name("Energy/tick needed to keep laser alive")
            @Config.RangeInt(min = 10000, max = 500000)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int laserEnergy = 25360;
        }
    }

    @Config(modid = JSG.MOD_ID, name = CONFIG_FILE_NAME + "items", category = "items")
    @Config.LangKey("config.jsg.items")
    public static class Items {
        @Config.Name("Ancient Shield")
        @Config.Comment({
                "Ancient Shield settings"
        })
        public static Shield shield = new Shield();

        public static class Shield {
            @Config.Name("Time after apply hungry")
            @Config.RangeInt(min = 2, max = 20)
            @Config.SlidingOption
            @Config.Comment({
                    "Time in minutes after hungry should be applied to player",
                    "SIDE: SERVER"
            })
            public int hungryAfter = 8;

            @Config.Name("Hungry length")
            @Config.RangeDouble(min = 0.25, max = 2)
            @Config.SlidingOption
            @Config.Comment({
                    "How many minutes should be hungry applied for?",
                    "SIDE: SERVER"
            })
            public double hungryLength = 0.5;


            @Config.Name("Energy capacity")
            @Config.RangeInt(min = 1000, max = 2000000)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public int energy = 24000;


            @Config.Name("Energy per tick when using")
            @Config.RangeInt(min = 0, max = 20000)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public int energyPerTick = 40;


            @Config.Name("Energy on damage")
            @Config.RangeInt(min = 0, max = 20000)
            @Config.Comment({
                    "SIDE: SERVER/CLIENT"
            })
            public int energyDamage = 400;
        }

    }

    @Config(modid = JSG.MOD_ID, name = CONFIG_FILE_NAME + "worldgen", category = "worldgen")
    @Config.LangKey("config.jsg.worldgen")
    public static class WorldGen {
        @Config.Name("Mysterious pages")
        @Config.Comment({
                "Mysterious pages settings"
        })
        public static MystPage mystPage = new MystPage();

        @Config.Name("Ores")
        @Config.Comment({
                "Ores generator settings"
        })
        public static Ores ores = new Ores();

        @Config.Name("Structures")
        @Config.Comment({
                "Structures generator settings"
        })
        public static Structures structures = new Structures();

        @Config.Name("Other DIM Stargate Generator")
        @Config.Comment({
                "Stargates generated in other dimensions"
        })
        public static OtherDimGenerator otherDimGenerator = new OtherDimGenerator();

        public static class MystPage {
            @Config.Name("Max XZ-coords generation")
            @Config.RangeInt(min = 1000, max = 30000)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int maxOverworldCoords = 15000;

            @Config.Name("Min XZ-coords generation")
            @Config.RangeInt(min = 1000, max = 30000)
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int minOverworldCoords = 5000;

            @Config.Name("Chance of despawning DHD")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public double despawnDhdChance = 0.05;

            @Config.Name("Chance of force unstable gate")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public double forcedUnstableGateChance = 0.05;

            @Config.Name("Mysterious page cooldown")
            @Config.RangeInt(min = 0, max = 400)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int pageCooldown = 40;
        }

        public static class Ores {
            @Config.Name("Enable Naquadah ore generation")
            @Config.Comment({
                    "Do you want to spawn naquadah ores in the Nether?",
                    "SIDE: SERVER"
            })
            public boolean naquadahEnable = true;

            @Config.Name("Naquadah vein size")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int naquadahVeinSize = 8;

            @Config.Name("Naquadah max veins in chunk")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int naquadahMaxVeinInChunk = 16;

            @Config.Name("Enable Trinium ore generation")
            @Config.Comment({
                    "Do you want to spawn trinium ores in the End?",
                    "SIDE: SERVER"
            })
            public boolean triniumEnabled = true;

            @Config.Name("Trinium vein size")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int triniumVeinSize = 4;

            @Config.Name("Trinium max veins in chunk")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int triniumMaxVeinInChunk = 16;

            @Config.Name("Enable Titanium ore generation")
            @Config.Comment({
                    "Do you want to spawn titanium ores in the Overworld?",
                    "SIDE: SERVER"
            })
            public boolean titaniumEnable = true;

            @Config.Name("Titanium vein size")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int titaniumVeinSize = 4;

            @Config.Name("Titanium max veins in chunk")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            @Config.Comment({
                    "SIDE: SERVER"
            })
            public int titaniumMaxVeinInChunk = 16;
        }

        public static class Structures {
            @Config.Name("Enable random stargate generator")
            @Config.RequiresMcRestart
            @Config.Comment({
                    "Generate stargate in world random.",
                    "CAN BE OVERRIDE IN STRUCTURES CONFIG FILE",
                    "SIDE: SERVER"
            })
            public boolean stargateRandomGeneratorEnabled = true;

            @Config.Name("Enable random structures generator")
            @Config.RequiresMcRestart
            @Config.Comment({
                    "Enable generation of structures in the world.",
                    "This will not disable the stargate generation!",
                    "CAN BE OVERRIDE IN STRUCTURES CONFIG FILE",
                    "SIDE: SERVER"
            })
            public boolean structuresRandomGeneratorEnabled = true;


            @Config.Name("Chance of generating stargates in Overworld")
            @Config.RequiresMcRestart
            @Config.RangeDouble(min = 0, max = 1f)
            @Config.SlidingOption
            @Config.Comment({
                    "CAN BE OVERRIDE IN STRUCTURES CONFIG FILE",
                    "SIDE: SERVER"
            })
            public float stargateRGChanceOverworld = 0.0001f;

            @Config.Name("Chance of generating stargates in End")
            @Config.RequiresMcRestart
            @Config.RangeDouble(min = 0, max = 1f)
            @Config.SlidingOption
            @Config.Comment({
                    "CAN BE OVERRIDE IN STRUCTURES CONFIG FILE",
                    "SIDE: SERVER"
            })
            public float stargateRGChanceTheEnd = 0.00007f;
        }

        public static class OtherDimGenerator {
            @Config.Name("Enable other DIM stargate generator")
            @Config.RequiresMcRestart
            @Config.Comment({
                    "Generate stargate in other dimensions (other than END, NETHER, OVERWORLD).",
                    "SIDE: SERVER"
            })
            public boolean generatorEnabled = true;

            @Config.Name("Blacklisted Dimensions")
            @Config.RequiresMcRestart
            @Config.Comment({
                    "List of disabled dimensions",
                    "SIDE: SERVER"
            })
            public int[] blacklistDims = new int[]{};
        }
    }
}
