package mrjake.aunis.command;

import mrjake.aunis.stargate.network.StargateNetwork;
import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
