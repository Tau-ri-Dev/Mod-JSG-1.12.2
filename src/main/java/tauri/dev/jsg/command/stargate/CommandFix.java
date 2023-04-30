package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.command.IJSGCommand;
import tauri.dev.jsg.command.JSGCommand;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

import javax.annotation.Nonnull;

import static net.minecraft.command.CommandBase.parseCoordinate;

public class CommandFix implements IJSGCommand {

    @Nonnull
    @Override
    public String getName() {
        return "sgfix";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Refresh gate tile (pos and address)";
    }

    @Nonnull
    @Override
    public String getUsage() {
        return "sgfix <x y z>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();

        if (args.length < 3) {
            JSGCommand.sendUsageMess(sender, this);
            return;
            //throw new WrongUsageException("commands.sgsetaddress.usage");
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
            JSGCommand.sendSuccessMess(sender, "Successfully executed!");
        } else
            JSGCommand.sendErrorMess(sender, "Target block is not a stargate base block!");
        //throw new WrongUsageException("commands.sgsetaddress.notstargate");
    }

}
