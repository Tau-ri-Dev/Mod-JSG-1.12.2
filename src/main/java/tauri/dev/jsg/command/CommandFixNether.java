package tauri.dev.jsg.command;

import tauri.dev.jsg.stargate.network.StargateNetwork;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class CommandFixNether extends CommandBase {

  @Override
  public String getName() {
    return "sgfixnether";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/sgfixnether";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    StargateNetwork.get(world).deleteNetherGate();
  }

}
