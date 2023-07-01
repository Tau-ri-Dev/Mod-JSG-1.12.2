package tauri.dev.jsg.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.tileentity.props.DestinyCountDownTile;

import javax.annotation.Nonnull;

import static net.minecraft.command.CommandBase.parseCoordinate;

public class CommandCountdownSet extends AbstractJSGCommand {

    public CommandCountdownSet() {
        super(JSGCommand.JSG_BASE_COMMAND);
    }

    @Nonnull
    @Override
    public String getName() {
        return "countdown";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Sets Destiny timer to the value";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "countdown [set|reset] <ticks> [x y z]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        BlockPos pos = sender.getPosition();
        World world = sender.getEntityWorld();
        TileEntity tileEntity = null;

        if (args.length < 1) {
            baseCommand.sendUsageMess(sender, this);
            return;
        }

        if(args[0].equalsIgnoreCase("set")){
            if (args.length > 4) {
                int x1 = (int) parseCoordinate(pos.getX(), args[2], false).getResult();
                int y1 = (int) parseCoordinate(pos.getY(), args[3], 0, 255, false).getResult();
                int z1 = (int) parseCoordinate(pos.getZ(), args[4], false).getResult();
                BlockPos foundPos = new BlockPos(x1, y1, z1);
                tileEntity = world.getTileEntity(foundPos);
            }
            if (tileEntity == null)
                tileEntity = JSGCommands.rayTraceTileEntity((EntityPlayerMP) sender);

            try {
                long ticks = Long.parseLong(args[1]);

                if (tileEntity instanceof DestinyCountDownTile) {
                    ((DestinyCountDownTile) tileEntity).setCountDown(sender.getEntityWorld().getTotalWorldTime() + ticks);
                    baseCommand.sendSuccessMess(sender, "Countdown set to " + ticks + " ticks!");
                } else
                    baseCommand.sendErrorMess(sender, "Target block is not a countdown!");
            } catch (Exception e) {
                baseCommand.sendUsageMess(sender, this);
            }
        }
        else if(args[0].equalsIgnoreCase("reset")){
            if (args.length > 3) {
                int x1 = (int) parseCoordinate(pos.getX(), args[1], false).getResult();
                int y1 = (int) parseCoordinate(pos.getY(), args[2], 0, 255, false).getResult();
                int z1 = (int) parseCoordinate(pos.getZ(), args[3], false).getResult();
                BlockPos foundPos = new BlockPos(x1, y1, z1);
                tileEntity = world.getTileEntity(foundPos);
            }
            if (tileEntity == null)
                tileEntity = JSGCommands.rayTraceTileEntity((EntityPlayerMP) sender);

            try {
                if (tileEntity instanceof DestinyCountDownTile) {
                    ((DestinyCountDownTile) tileEntity).setCountDown(sender.getEntityWorld().getTotalWorldTime() + 5);
                    baseCommand.sendSuccessMess(sender, "Countdown reset!");
                } else
                    baseCommand.sendErrorMess(sender, "Target block is not a countdown!");
            } catch (Exception e) {
                baseCommand.sendUsageMess(sender, this);
            }
        }
    }
}
