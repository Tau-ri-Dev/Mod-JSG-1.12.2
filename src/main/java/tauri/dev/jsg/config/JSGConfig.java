package tauri.dev.jsg.config;

import net.minecraftforge.common.config.Config;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.config.stargate.StargateTimeLimitModeEnum;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;

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
                "Should JSG check for update on startup?"
        })
        public static boolean enableAutoUpdater = true;

        @Config.Name("AGS path")
        @Config.Comment({
                "The pastebin of AGS (used in /ags command)"
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
                    "Specifies volume of sounds from JSG"
            })
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public float volume = 1;
        }

        public static class Visual {
            @Config.Name("Notebook page Glyph transparency")
            @Config.Comment({
                    "Specifies transparency of glyphs on notebook page"
            })
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public double glyphTransparency = 0.75;

            @Config.Name("Notebook Page offset")
            @Config.Comment({
                    "Greater values render the Page more to the center of the screen, smaller render it closer to the borders.",
                    "0 - for standard 16:9 (default),",
                    "0.2 - for 4:3.",
            })
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public float pageNarrowing = 0;

            @Config.Name("Render emissive textures")
            @Config.Comment({
                    "Render light of some textures.",
                    "Disable this if it causes lags."
            })
            public boolean renderEmissive = true;
        }

        public static class Debug {
            @Config.Name("Check gate merge")
            @Config.Comment({
                    "Check if gate is merged?"
            })
            public boolean checkGateMerge = true;

            @Config.Name("Render bounding boxes")
            @Config.Comment({
                    "Render bounding boxes of stargates?"
            })
            public boolean renderBoundingBoxes = false;

            @Config.Name("Render whole kawoosh bounding box")
            public boolean renderWholeKawooshBoundingBox = false;

            @Config.Name("Render invisible blocks")
            @Config.Comment({
                    "Should render iris/stargate merged/rings barricade blocks?"
            })
            public boolean renderInvisibleBlocks = false;

            @Config.Name("Show loading textures in log")
            @Config.Comment({
                    "Should log every single texture while loading the mod?"
            })
            public boolean logTexturesLoading = false;

            @Config.Name("Log debug messages as info")
            @Config.Comment({
                    "If debug console not working, should display debug as info?"
            })
            public boolean logDebugAsInfo = false;
        }

        public static class Integration {
            @Config.RequiresMcRestart
            @Config.Name("Enable Tinkers' Construct integration")
            @Config.Comment({
                    "WARNING! - Requires reloading!"
            })
            public boolean tConstructIntegration = true;

            @Config.RequiresMcRestart
            @Config.Name("Enable Open Computers integration")
            @Config.Comment({
                    "WARNING! - Requires reloading!"
            })
            public boolean ocIntegration = true;

            @Config.Name("OC wireless network range (in blocks)")
            @Config.RangeInt(min = 0, max = 100)
            @Config.SlidingOption
            public int ocIntegrationWirelessRange = 20;

            @Config.RequiresMcRestart
            @Config.Name("Enable Thermal Expansion integration")
            @Config.Comment({
                    "WARNING! - Requires reloading!"
            })
            public boolean tExpansionIntegration = true;
        }

        public static class AdvancementsConfig {
            @Config.Name("Ranged Advancements radius")
            @Config.Comment({
                    "Players in this radius around triggered pos will get Advancement."
            })
            @Config.RangeInt(min = 0, max = 100)
            @Config.SlidingOption
            public int radius = 25;
        }

        public static class CountDownConfig {
            @Config.Name("Delay after zero-time (seconds)")
            @Config.RangeInt(min = 0, max = 60)
            @Config.SlidingOption
            public int zeroDelay = 5;

            @Config.Name("Delay to start dialing after countdown start (seconds)")
            @Config.RangeInt(min = 0, max = 60)
            @Config.SlidingOption
            public int dialStartDelay = 5;
        }

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
                    "WARNING! - Requires reloading!"
            })
            public boolean disableJSGMainMenu = false;

            @Config.Name("Enable debug mode")
            public boolean debugMode = false;

            @Config.Name("Play music in main menu")
            public boolean playMusic = true;

            @Config.Name("Enable sync with minecraft ticks")
            public boolean syncEnabled = false;

            @Config.Name("Enable changing gate type")
            public boolean enableGateChanging = true;

            @Config.Name("Enable loading music")
            public boolean loadingMusic = true;
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
            public double zpmCapacity = 4_398_046_511_104D;

            @Config.Name("ZPMHub's max power throughput")
            @Config.RangeInt(min = 1, max = 1043600)
            @Config.SlidingOption
            public int zpmHubMaxEnergyTransfer = 104360;
        }

    }

    @Config(modid = JSG.MOD_ID, name = CONFIG_FILE_NAME + "stargates", category = "stargates")
    @Config.LangKey("config.jsg.stargates")
    public static class Stargate {

        @Config.Name("Stargate size")
        @Config.Comment({
                "Defines size of stargate's model"
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
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI"
            })
            public boolean enableBurriedState = true;

            @Config.Name("Orlin's gate max open count")
            @Config.RangeInt(min = 0, max = 15000)
            @Config.SlidingOption
            public int stargateOrlinMaxOpenCount = 2;

            @Config.Name("Universe dialer nearby radius")
            @Config.RangeInt(min = 5)
            @Config.SlidingOption
            public int universeGateNearbyReach = 1024;

            @Config.Name("Enable gate overheat with explosion")
            @Config.Comment({
                    "Should gate explode when its overheated?",
                    "This method is not implemented yet!"
            })
            public boolean enableGateOverHeatExplosion = false;

            @Config.RequiresMcRestart
            @Config.Name("Max stargate heat")
            @Config.RangeDouble(min = 0)
            @Config.SlidingOption
            public double gateMaxHeat = 83400;

            @Config.Name("Chance of lighting strike that charge a gate")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public float lightingBoldChance = 0.0005f;

            @Config.Name("Connect to dialing gate")
            @Config.Comment({
                    "If target gate is dialing and this option is set to true,",
                    "the target gate will stop dialing and open incoming wormhole.",
                    "If this is set to false and the dialed gate dialing address,",
                    "the connection will not established.",
                    "If it cause issues, set it to false.",
            })
            public boolean allowConnectToDialing = true;

            @Config.Name("Use 8 chevrons between MW and PG gates")
            @Config.Comment({
                    "Change this to true, if you want to use 8 chevrons between pegasus and milkyway gates"
            })
            public boolean pegAndMilkUseEightChevrons = true;

            @Config.Name("Need only 7 symbols between Uni gates")
            @Config.Comment({
                    "If you want to dial UNI-UNI only with seven symbols (interdimensional for example), set this to true"
            })
            public boolean useStrictSevenSymbolsUniGate = false;
        }

        public static class Iris {
            @Config.Name("Iris kills at destination")
            @Config.Comment("If set to 'false' player get killed by iris on entering event horizon")
            public boolean killAtDestination = true;

            @Config.Name("Titanium iris durability")
            @Config.Comment({
                    "Durability of Titanium iris",
                    "set it to 0, if u want to make it unbreakable"
            })
            @Config.RangeInt(min = 0, max = 50000)
            @Config.SlidingOption
            public int titaniumIrisDurability = 500;

            @Config.Name("Trinium iris durability")
            @Config.Comment({
                    "Durability of Trinium iris",
                    "set it to 0, if u want to make it unbreakable"
            })
            @Config.RangeInt(min = 0, max = 50000)
            @Config.SlidingOption
            public int triniumIrisDurability = 1000;

            @Config.Name("Shield power draw")
            @Config.Comment({
                    "Energy/tick used for make shield closed"
            })
            @Config.RangeInt(min = 0, max = 500000)
            @Config.SlidingOption
            public int shieldPowerDraw = 500;

            @Config.Name("Allow creative bypass")
            @Config.Comment({
                    "Set it to true, if u want to bypass",
                    "shield/iris damage by creative gamemode"
            })
            public boolean allowCreative = false;

            @Config.Name("Maximum iris code length")
            @Config.RangeInt(min = 0, max = 32)
            @Config.SlidingOption
            public int irisCodeLength = 9;

            @Config.Name("Can iris destroy blocks")
            public boolean irisDestroysBlocks = false;

            @Config.Name("Unbreaking chance per level")
            @Config.Comment({"0 - disables unbreaking on iris", "100 - unbreaking makes iris unbreakable"})
            @Config.RangeInt(min = 0, max = 100)
            @Config.SlidingOption
            public int unbreakingChance = 10;

            @Config.Name("Enable iris overheat collapse")
            @Config.Comment({
                    "Should iris break when its overheated?"
            })
            public boolean enableIrisOverHeatCollapse = true;

            @Config.RequiresMcRestart
            @Config.Name("Max titanium iris heat")
            @Config.RangeDouble(min = 0)
            @Config.SlidingOption
            public double irisTitaniumMaxHeat = 1668;

            @Config.RequiresMcRestart
            @Config.Name("Max trinium iris heat")
            @Config.RangeDouble(min = 0)
            @Config.SlidingOption
            public double irisTriniumMaxHeat = 3336;
        }

        public static class Power {
            @Config.Name("Stargate's internal buffer size")
            @Config.RangeInt(min = 4608)
            @Config.SlidingOption
            public int stargateEnergyStorage = 71280000;

            @Config.Name("Stargate's max power throughput")
            @Config.RangeInt(min = 1, max = 500000)
            @Config.SlidingOption
            public int stargateMaxEnergyTransfer = 26360;

            @Config.Name("Stargate wormhole open power draw")
            @Config.RangeInt(min = 0, max = 500000)
            @Config.SlidingOption
            public int openingBlockToEnergyRatio = 4608;

            @Config.Name("Stargate wormhole sustain power draw")
            @Config.RangeInt(min = 0, max = 50)
            @Config.SlidingOption
            public int keepAliveBlockToEnergyRatioPerTick = 2;

            @Config.Name("Stargate instability threshold")
            @Config.Comment({
                    "Seconds of energy left before gate becomes unstable",
            })
            @Config.RangeInt(min = 1, max = 120)
            @Config.SlidingOption
            public int instabilitySeconds = 20;

            @Config.Name("Orlin's gate energy multiplier")
            @Config.RangeDouble(min = 0, max = 100)
            @Config.SlidingOption
            public double stargateOrlinEnergyMul = 2.0;

            @Config.Name("Universe gate energy multiplier")
            @Config.RangeDouble(min = 0, max = 100)
            @Config.SlidingOption
            public double stargateUniverseEnergyMul = 1.5;

            @Config.Name("Capacitors supported by Universe gates")
            @Config.Comment({
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI"
            })
            @Config.RangeInt(min = 0, max = 3)
            @Config.SlidingOption
            public int universeCapacitors = 0;

            @Config.Name("Stargate eight symbols address power mul")
            @Config.Comment({
                    "Specifies the multiplier of power needed to keep the gate alive",
                    "when 8-symbols address is dialed"
            })
            @Config.RangeDouble(min = 0, max = 100)
            @Config.SlidingOption
            public float eightSymbolAddressMul = 1.3f;

            @Config.Name("Stargate nine symbols address power mul")
            @Config.Comment({
                    "Specifies the multiplier of power needed to keep the gate alive",
                    "when 9-symbols address is dialed"
            })
            @Config.RangeDouble(min = 0, max = 100)
            @Config.SlidingOption
            public float nineSymbolAddressMul = 1.7f;
        }

        public static class Visual {
            @Config.Name("Temperature threshold for frosty overlay")
            @Config.Comment({
                    "Below this biome temperature the gate will receive frosty texture.",
                    "Set to negative value to disable."
            })
            @Config.RangeDouble(min = 0, max = 5)
            @Config.SlidingOption
            public float frostyTemperatureThreshold = 0.1f;

            @Config.Name("Biome overlay biome matches")
            @Config.Comment({
                    "This check comes last (after block is directly under sky (except Nether) and temperature is high enough).",
                    "You can disable the temperature check by setting it to a negative value.",
                    "Format: \"modid:biomename\", for example: ",
                    "\"minecraft:dark_forest\"",
                    "\"minecraft:forest\""
            })
            public Map<String, String[]> biomeMatches = new HashMap<String, String[]>() {
                {
                    put(BiomeOverlayEnum.NORMAL.toString(), new String[]{});
                    put(BiomeOverlayEnum.FROST.toString(), new String[]{});
                    put(BiomeOverlayEnum.MOSSY.toString(), new String[]{"minecraft:jungle", "minecraft:jungle_hills", "minecraft:jungle_edge", "minecraft:mutated_jungle", "minecraft:mutated_jungle_edge"});
                    put(BiomeOverlayEnum.AGED.toString(), new String[]{});
                    put(BiomeOverlayEnum.SOOTY.toString(), new String[]{"minecraft:hell"});
                }
            };

            @Config.Name("Biome overlay override blocks")
            @Config.Comment({
                    "Format: \"modid:blockid[:meta]\", for example: ",
                    "\"minecraft:wool:7\"",
                    "\"minecraft:stone\""
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
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI"
            })
            public boolean allowIncomingAnimations = true;

            @Config.Name("Faster MilkyWay and Universe gates computer dial")
            @Config.Comment({
                    "Speed up dialing with computer on MW and UNI gates"
            })
            public boolean fasterMWGateDial = false;

            @Config.Name("Enable fast dialing of gates")
            @Config.Comment({
                    "Enable fast dialing on gates by default",
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI"
            })
            public boolean enableFastDialing = false;

            @Config.Name("Render not placed blocks of s stargate")
            public boolean renderStargateNotPlaced = true;
        }

        public static class PointOfOrigins {
            @Config.Name("Enable different Point Of Origins for MW gate")
            public boolean enableDiffOrigins = true;

            @Config.RequiresMcRestart
            @Config.Name("Custom added points of origin")
            @Config.Comment({
                    "Specifies Point Of Origins that were added by any resource pack.",
                    "This options is required to load all models of added origins!",
                    "Format: \"id:name\", for example: ",
                    "\"6:Tollan\"",
                    "\"7:P4X-256\"",
                    "!DO NOT CHANGE ANYTHING IF YOU DON'T KNOW WHAT ARE YOU DOING!"
            })
            public String[] additionalOrigins = {};
        }

        public static class EventHorizon {
            @Config.Name("Disable animated Event Horizon")
            @Config.RequiresMcRestart
            @Config.Comment({
                    "Changing this option will require you to reload resources manually.",
                    "Just restart your game"
            })
            public boolean disableAnimatedEventHorizon = false;

            @Config.Name("Enable wrong side killing")
            public boolean wrongSideKilling = true;

            @Config.Name("Unstable Event Horizon chance of death")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public float ehDeathChance = 0.07f;

            @Config.Name("Disable new kawoosh model (from 4.11.0.0)")
            @Config.RequiresMcRestart
            public boolean disableNewKawoosh = false;

            @Config.Name("Kawoosh invincible blocks")
            @Config.Comment({
                    "Format: \"modid:blockid[:meta/*]\", for example: ",
                    "\"minecraft:wool:7\"",
                    "\"minecraft:stone\"",
                    "\"minecraft:concrete:*\""
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
            public boolean renderEHifTheyNot = true;
        }

        public static class AutoCloseConfig {
            @Config.Name("Autoclose enabled")
            public boolean autocloseEnabled = true;

            @Config.Name("Seconds to autoclose with no players nearby")
            @Config.RangeInt(min = 1, max = 300)
            @Config.SlidingOption
            public int secondsToAutoclose = 5;
        }

        public static class OpenLimitConfig {
            @Config.Name("Maximum seconds of gate should be open")
            @Config.Comment({
                    "(in seconds (2280 = 38 minutes))"
            })
            @Config.RangeInt(min = 5, max = 3000)
            @Config.SlidingOption
            public int maxOpenedSeconds = 240;

            @Config.Name("Gate open time limit mode")
            @Config.Comment({
                    "What happens after gate's open time reaches limit?"
            })
            public StargateTimeLimitModeEnum maxOpenedWhat = StargateTimeLimitModeEnum.DRAW_MORE_POWER;

            @Config.Name("Power draw after opened time limit")
            @Config.RangeInt(min = 0, max = 50000)
            @Config.SlidingOption
            public int maxOpenedPowerDrawAfterLimit = 10000;
        }

        public static class RIG {
            @Config.Name("Enable random incoming wormholes")
            @Config.Comment({
                    "Enable random incoming wormholes generator",
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI"
            })
            public boolean enableRandomIncoming = true;

            @Config.Name("Chance of spawning")
            @Config.Comment({
                    "10 = 1%"
            })
            @Config.RangeInt(min = 1, max = 100)
            @Config.SlidingOption
            public int chance = 1;

            @Config.Name("Entities to spawn")
            @Config.Comment({
                    "Format: \"modid:entityid\", for example: ",
                    "\"minecraft:zombie\"",
                    "\"minecraft:creeper\""
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
            public int rangeFlat = 25;

            @Config.Name("Rings vertical reach")
            @Config.RangeInt(min = 1, max = 256)
            @Config.SlidingOption
            public int rangeVertical = 256;

            @Config.Name("Ignore rings check for blocks to replace")
            public boolean ignoreObstructionCheck = false;
        }

        public static class Power {
            @Config.Name("Transport Rings active power draw")
            @Config.Comment({
                    "Energy extracted from rings every tick when they are active (calculated by distance from these rings)"
            })
            @Config.RangeInt(min = 0, max = 100)
            @Config.SlidingOption
            public int ringsKeepAliveBlockToEnergyRatioPerTick = 2;

            @Config.Name("Transport Rings teleport power draw")
            @Config.Comment({
                    "Energy extracted from rings when they teleport LIVING entity (not drop)"
            })
            @Config.RangeInt(min = 0, max = 25600)
            @Config.SlidingOption
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
            public int rangeFlat = 25;

            @Config.Name("DHD range's radius vertical")
            @Config.RangeInt(min = 1, max = 64)
            @Config.SlidingOption
            public int rangeVertical = 15;

            @Config.Name("Universe dialer max horizontal reach radius")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            public int universeDialerReach = 10;

            @Config.Name("DHD's max fluid capacity")
            @Config.RangeInt(min = 1, max = 128000)
            @Config.SlidingOption
            public int fluidCapacity = 16000;
        }

        public static class Power {
            @Config.Name("Capacity upgrade multiplier")
            @Config.RangeDouble(min = 1.0, max = 5.0)
            @Config.Comment({
                    "When capacity upgrade is placed in the DHD,",
                    "then multiply internal capacity by this number"
            })
            @Config.SlidingOption
            public float capacityUpgradeMultiplier = 2f;

            @Config.Name("Energy per 1mB Naquadah")
            @Config.RangeInt(min = 1, max = 50000)
            @Config.SlidingOption
            public int energyPerNaquadah = 10240;

            @Config.Name("Generation multiplier")
            @Config.RangeInt(min = 1, max = 5)
            @Config.Comment({
                    "Energy per 1mB is multiplied by this",
                    "Consumed mB/t is equal to this"
            })
            @Config.SlidingOption
            public int powerGenerationMultiplier = 1;

            @Config.Name("Efficiency upgrade multiplier")
            @Config.RangeDouble(min = 1.0, max = 5.0)
            @Config.Comment({
                    "Energy per 1mB is multiplied by this",
                    "when efficiency upgrade is placed in the DHD"
            })
            @Config.SlidingOption
            public float efficiencyUpgradeMultiplier = 1.4f;

            @Config.Name("Cold fusion reactor activation energy level")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public double activationLevel = 0.9;

            @Config.Name("Cold fusion reactor deactivation energy level")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public double deactivationLevel = 0.98;
        }

        public static class Audio {
            @Config.Name("Enable press sound when dialing with computer")
            @Config.Comment({
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI"
            })
            public boolean computerDialSound = false;
        }

        public static class Visual {
            @Config.Name("Enable hint when dialing on DHDs with notebook page")
            public boolean enablePageHint = true;
            @Config.Name("Enable opening last chevron while dialing with dhd")
            @Config.Comment({
                    "Enable opening last chevron while dialing milkyway gate with dhd",
                    "THIS OPTION CAN BE OVERRIDE BY SETTING IT IN STARGATE GUI"
            })
            public boolean dhdLastOpen = true;
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
            public int reach = 10;

            @Config.Name("Interval of signals being send to OC about transfers (in ticks)")
            @Config.RangeInt(min = 10, max = 400)
            @Config.SlidingOption
            public int signalIntervalTicks = 20;

            @Config.Name("Damage entities in a beam")
            public boolean damageEntities = true;

            @Config.Name("Destroy blocks in a beam")
            public boolean destroyBlocks = true;
        }

        public static class Container {
            @Config.Name("Fluid buffer capacity")
            @Config.RangeInt(min = 3000, max = 600000)
            @Config.SlidingOption
            public int fluidCapacity = 60000;

            @Config.Name("Energy buffer capacity")
            @Config.RangeInt(min = 3000)
            @Config.SlidingOption
            public int energyCapacity = 17820000;

            @Config.Name("Energy buffer max transfer")
            @Config.RangeInt(min = 1)
            @Config.SlidingOption
            public int energyTransfer = 26360;

            @Config.Name("Fluid max transfer")
            @Config.RangeInt(min = 1, max = 5000)
            @Config.SlidingOption
            public int fluidTransfer = 100;

            @Config.Name("Item max transfer")
            @Config.RangeInt(min = 1, max = 64)
            @Config.SlidingOption
            public int itemTransfer = 4;
        }

        public static class Visual {
            @Config.Name("Should the beam be responsive to fluid color")
            public boolean enableFluidBeamColorization = true;
        }

        public static class Power {
            @Config.Name("Energy/tick needed to keep laser alive")
            @Config.RangeInt(min = 10000, max = 500000)
            @Config.SlidingOption
            public int laserEnergy = 25360;
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

        public static class MystPage {
            @Config.Name("Max XZ-coords generation")
            @Config.RangeInt(min = 1000, max = 30000)
            @Config.SlidingOption
            public int maxOverworldCoords = 15000;

            @Config.Name("Min XZ-coords generation")
            @Config.RangeInt(min = 1000, max = 30000)
            @Config.SlidingOption
            public int minOverworldCoords = 5000;

            @Config.Name("Chance of despawning DHD")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.SlidingOption
            public double despawnDhdChance = 0.05;

            @Config.Name("Mysterious page cooldown")
            @Config.RangeInt(min = 0, max = 400)
            @Config.SlidingOption
            public int pageCooldown = 40;
        }

        public static class Ores {
            @Config.Name("Enable Naquadah ore generation")
            @Config.Comment({
                    "Do you want to spawn naquadah ores in the Nether?",
            })
            public boolean naquadahEnable = true;

            @Config.Name("Naquadah vein size")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            public int naquadahVeinSize = 8;

            @Config.Name("Naquadah max veins in chunk")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            public int naquadahMaxVeinInChunk = 16;

            @Config.Name("Enable Trinium ore generation")
            @Config.Comment({
                    "Do you want to spawn trinium ores in the End?",
            })
            public boolean triniumEnabled = true;

            @Config.Name("Trinium vein size")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            public int triniumVeinSize = 4;

            @Config.Name("Trinium max veins in chunk")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            public int triniumMaxVeinInChunk = 16;

            @Config.Name("Enable Titanium ore generation")
            @Config.Comment({
                    "Do you want to spawn titanium ores in the Overworld?",
            })
            public boolean titaniumEnable = true;

            @Config.Name("Titanium vein size")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            public int titaniumVeinSize = 4;

            @Config.Name("Titanium max veins in chunk")
            @Config.RangeInt(min = 0, max = 64)
            @Config.SlidingOption
            public int titaniumMaxVeinInChunk = 16;
        }

        public static class Structures {
            @Config.Name("Enable random stargate generator")
            @Config.RequiresMcRestart
            @Config.Comment({
                    "Generate stargate in world random.",
                    "CAN BE OVERRIDE IN STRUCTURES CONFIG FILE"
            })
            public boolean stargateRandomGeneratorEnabled = true;

            @Config.Name("Enable random structures generator")
            @Config.RequiresMcRestart
            @Config.Comment({
                    "Enable generation of structures in the world.",
                    "This will not disable the stargate generation!",
                    "CAN BE OVERRIDE IN STRUCTURES CONFIG FILE"
            })
            public boolean structuresRandomGeneratorEnabled = true;


            @Config.Name("Chance of generating stargates in Overworld")
            @Config.RequiresMcRestart
            @Config.RangeDouble(min = 0, max = 1f)
            @Config.SlidingOption
            public float stargateRGChanceOverworld = 0.0001f;

            @Config.Name("Chance of generating stargates in End")
            @Config.RequiresMcRestart
            @Config.RangeDouble(min = 0, max = 1f)
            @Config.SlidingOption
            public float stargateRGChanceTheEnd = 0.00007f;
        }
    }
}
