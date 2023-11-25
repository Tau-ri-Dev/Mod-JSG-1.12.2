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
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;
import tauri.dev.jsg.util.RayTraceHelper;

import javax.annotation.Nonnull;

import static net.minecraft.command.CommandBase.parseCoordinate;

public class CommandStargateGetFakePos extends AbstractJSGCommand {

    public CommandStargateGetFakePos() {
        super(JSGCommand.JSG_BASE_COMMAND);
    }

    @Nonnull
    @Override
    public String getName() {
        return "sggetfakepos";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Gets fake position of universe stargate";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "sggetfakepos [tileX] [tileY] [tileZ]";
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
        try {
            if (args.length > 2) {
                int x1 = (int) parseCoordinate(pos.getX(), args[0], false).getResult();
                int y1 = (int) parseCoordinate(pos.getY(), args[1], 0, 255, false).getResult();
                int z1 = (int) parseCoordinate(pos.getZ(), args[2], false).getResult();
                BlockPos foundPos = new BlockPos(x1, y1, z1);
                tileEntity = world.getTileEntity(foundPos);
            }
            if (tileEntity == null)
                tileEntity = RayTraceHelper.rayTraceTileEntity((EntityPlayerMP) sender);

            if (tileEntity instanceof StargateUniverseBaseTile) {
                baseCommand.sendSuccessMess(sender, "Fake position of this gate is:");
                baseCommand.sendInfoMess(sender, "DIM: " + ((StargateUniverseBaseTile) tileEntity).getFakeWorld().provider.getDimension());
                baseCommand.sendInfoMess(sender, "Pos: " + ((StargateUniverseBaseTile) tileEntity).getFakePos());
            } else
                baseCommand.sendErrorMess(sender, "TileEntity is not a StargateUniverseBaseTile!");
        } catch (NumberFormatException e) {
            baseCommand.sendUsageMess(sender, this);
        }
    }
}
