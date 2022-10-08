package tauri.dev.jsg.command;

import net.minecraft.command.CommandBase;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.Arrays;
import java.util.List;

public final class JSGCommands {

  private static final List<CommandBase> commands = Arrays.asList(
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
          new CommandAgs()
  );

  public static void registerCommands(FMLServerStartingEvent event) {
    for (CommandBase command : commands) {
      event.registerServerCommand(command);
    }
  }
}
