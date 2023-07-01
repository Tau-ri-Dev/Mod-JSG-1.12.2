package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.command.AbstractJSGCommand;
import tauri.dev.jsg.command.JSGCommand;
import tauri.dev.jsg.command.JSGCommands;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;

import javax.annotation.Nonnull;
import java.util.Objects;

import static net.minecraft.command.CommandBase.parseCoordinate;

public class CommandStargateSetFakePos extends AbstractJSGCommand {

    public CommandStargateSetFakePos() {
        super(JSGCommand.JSG_BASE_COMMAND);
    }

    @Nonnull
    @Override
    public String getName() {
        return "sgsetfakepos";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "sgsetfakepos <x> <y> <z> <dimId> [tileX] [tileY] [tileZ]";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Sets fake position of universe stargate";
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

        if (args.length < 4) {
            //notifyCommandListener(sender, this, "Please, insert x y z and dimId!");
            baseCommand.sendUsageMess(sender, this);
            return;
        }
        try {
            int newX = (int) parseCoordinate(pos.getX(), args[0], false).getResult();
            int newY = (int) parseCoordinate(pos.getY(), args[1], 0, 255, false).getResult();
            int newZ = (int) parseCoordinate(pos.getZ(), args[2], false).getResult();
            int newDim = Integer.parseInt(args[3]);

            if (args.length > 6) {
                int x1 = (int) parseCoordinate(pos.getX(), args[4], false).getResult();
                int y1 = (int) parseCoordinate(pos.getY(), args[5], 0, 255, false).getResult();
                int z1 = (int) parseCoordinate(pos.getZ(), args[6], false).getResult();
                BlockPos foundPos = new BlockPos(x1, y1, z1);
                tileEntity = world.getTileEntity(foundPos);
            }
            if (tileEntity == null)
                tileEntity = JSGCommands.rayTraceTileEntity((EntityPlayerMP) sender);

            if (tileEntity instanceof StargateUniverseBaseTile) {
                ((StargateUniverseBaseTile) tileEntity).setFakePos(new BlockPos(newX, newY, newZ));
                ((StargateUniverseBaseTile) tileEntity).setFakeWorld(Objects.requireNonNull(sender.getEntityWorld().getMinecraftServer()).getWorld(newDim));
                baseCommand.sendSuccessMess(sender, "Successfully set!");
            } else
                baseCommand.sendErrorMess(sender, "Target block is not a Universe gate base block!");
        } catch (NumberFormatException e) {
            baseCommand.sendUsageMess(sender, this);
        }
    }
}
