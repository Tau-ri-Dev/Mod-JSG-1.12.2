package tauri.dev.jsg.command;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import tauri.dev.jsg.command.stargate.*;

import java.util.ArrayList;
import java.util.Arrays;

public final class JSGCommands {

    @SuppressWarnings("unused")
    public static final ArrayList<AbstractJSGCommand> COMMANDS = new ArrayList<>(Arrays.asList(
            new CommandStargateQuery(),
            new CommandPrepare(),
            new CommandStargateCloseAll(),
            new CommandStargateSetAddress(),
            new CommandPageGive(),
            new CommandStargateLinkDHD(),
            new CommandFix(),
            new CommandFixNether(),
            new CommandGenerateIncoming(),
            new CommandAgs(),
            new CommandActiveAll(),
            new CommandCountdownSet(),
            new CommandReloadConfigs(),
            new CommandDestinyFTL(),
            new CommandStargateSetFakePos(),
            new CommandStargateGetFakePos(),
            new CommandStargateResetFakePos(),
            new CommandImportOrigins(),
            new CommandStructureSpawn(),
            new CommandTest()
    ));

    /**
     * Used as API
     * <p>
     * Register your sub commands to /jsg command.
     * <p>
     * ! REGISTER YOUR COMMANDS WHEN SERVER IS STARTING !
     * Call this when FMLServerStartingEvent event is fired
     *
     * @param commandInstance - instance of IJSGCommand from your mod
     */
    @SuppressWarnings("unused")
    public static void registerSubCommand(JSGCommand baseCommand, AbstractJSGCommand commandInstance) {
        baseCommand.subCommands.add(commandInstance);
    }

    public static void load() {
    }

    public static void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(JSGCommand.JSG_BASE_COMMAND);
    }
}
