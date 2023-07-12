package tauri.dev.jsg.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import tauri.dev.jsg.tileentity.util.PreparableInterface;
import tauri.dev.jsg.util.RayTraceHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class CommandPrepare extends AbstractJSGCommand {

    public CommandPrepare() {
        super(JSGCommand.JSG_BASE_COMMAND);
    }

    @Override
    @Nonnull
    public String getName() {
        return "sgprepare";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Prepares TileEntity for saving as structure";
    }

    @Override
    @Nonnull
    public String getGeneralUsage() {
        return "sgprepare";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException {
        TileEntity tileEntity = RayTraceHelper.rayTraceTileEntity((EntityPlayerMP) sender);

        if (tileEntity instanceof PreparableInterface) {
            if (((PreparableInterface) tileEntity).prepare(sender, this)) {
                baseCommand.sendSuccessMess(sender, "Preparing " + tileEntity.getClass().getSimpleName());
            } else {
                baseCommand.sendErrorMess(sender, "Failed to prepare " + tileEntity.getClass().getSimpleName() + ".");
            }
        } else
            baseCommand.sendErrorMess(sender, "Can't prepare this block. TileEntity not a instance of PreparableInterface.");
    }
}
