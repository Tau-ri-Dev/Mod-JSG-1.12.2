package tauri.dev.jsg.command;

import net.minecraft.command.CommandBase;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.Arrays;
import java.util.List;

public final class JSGCommands {

  private static final List<CommandBase> COMMANDS = Arrays.asList(
          new CommandStargateQuery(),
          new CommandPrepare(),
          new CommandStargateCloseAll(),
          new CommandStargateSetAddress(),
          new CommandPageGive(),
          new CommandStargateLinkDHD(),
          new CommandDebug(),
          new CommandFix(),
          new CommandFixNether(),
          new CommandGenerateIncoming(),
          new CommandAgs(),
          new CommandActiveAll()
  );

  public static void registerCommands(FMLServerStartingEvent event) {
    for (CommandBase command : COMMANDS) {
      event.registerServerCommand(command);
    }
  }
}
