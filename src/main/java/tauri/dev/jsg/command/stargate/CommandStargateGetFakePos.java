package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import tauri.dev.jsg.command.JSGCommands;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;

import javax.annotation.Nonnull;

public class CommandStargateGetFakePos extends CommandBase {

    @Nonnull
    @Override
    public String getName() {
        return "sggetfakepos";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/sggetfakepos [tileX] [tileY] [tileZ]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException {
        BlockPos pos = sender.getPosition();
        World world = sender.getEntityWorld();
        TileEntity tileEntity = null;
        try {
            if (args.length > 2) {
                int x1 = (int) parseCoordinate(pos.getX(), args[0], false).getResult();
                int y1 = (int) parseCoordinate(pos.getY(), args[1], 0, 255, false).getResult();
                int z1 = (int) parseCoordinate(pos.getZ(), args[2], false).getResult();
                BlockPos foundPos = new BlockPos(x1, y1, z1);
                tileEntity = world.getTileEntity(foundPos);
            }
            if (tileEntity == null)
                tileEntity = JSGCommands.rayTraceTileEntity((EntityPlayerMP) sender);

            if (tileEntity instanceof StargateUniverseBaseTile) {
                notifyCommandListener(sender, this, "StargateUniverseBaseTile's fake pos:");
                notifyCommandListener(sender, this, "Dim: " + ((StargateUniverseBaseTile) tileEntity).getFakeWorld().provider.getDimension());
                notifyCommandListener(sender, this, "Pos: " + ((StargateUniverseBaseTile) tileEntity).getFakePos());
            } else
                notifyCommandListener(sender, this, "TileEntity is not a StargateUniverseBaseTile.");
        } catch (NumberFormatException e) {
            notifyCommandListener(sender, this, "Wrong format!");
        }
    }
}
