package mrjake.aunis.command;

import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandFix extends CommandBase {

  @Override
  public String getName() {
    return "sgfix";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/sgfix";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    World world = sender.getEntityWorld();

    if (args.length < 3) {
      throw new WrongUsageException("commands.sgsetaddress.usage");
    }

    BlockPos pos = sender.getPosition();
    int x1 = (int) parseCoordinate(pos.getX(), args[0], false).getResult();
    int y1 = (int) parseCoordinate(pos.getY(), args[1], 0, 255, false).getResult();
    int z1 = (int) parseCoordinate(pos.getZ(), args[2], false).getResult();

    BlockPos gatePos = new BlockPos(x1, y1, z1);
    TileEntity tileEntity = world.getTileEntity(gatePos);

    if (tileEntity instanceof StargateAbstractBaseTile) {
      StargateAbstractBaseTile gateTile = (StargateAbstractBaseTile) tileEntity;
      gateTile.refresh();
    } else throw new WrongUsageException("commands.sgsetaddress.notstargate");
  }

}
