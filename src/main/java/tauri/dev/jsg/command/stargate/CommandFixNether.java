package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import tauri.dev.jsg.command.IJSGCommand;
import tauri.dev.jsg.command.JSGCommand;
import tauri.dev.jsg.stargate.network.StargateNetwork;

import javax.annotation.Nonnull;

public class CommandFixNether extends IJSGCommand {

  @Nonnull
  @Override
  public String getName() {
    return "fixnether";
  }

  @Nonnull
  @Override
  public String getDescription() {
    return "Delete nether gate to fix orlin gate";
  }

  @Nonnull
  @Override
  public String getUsage() {
    return "fixnether";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    StargateNetwork.get(world).deleteNetherGate();
    JSGCommand.sendSuccessMess(sender, "Nether gate deleted from network!");
  }

}
