package tauri.dev.jsg.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import tauri.dev.jsg.tileentity.util.PreparableInterface;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class CommandPrepare extends IJSGCommand {

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
    public String getUsage() {
        return "sgprepare";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException {
        TileEntity tileEntity = JSGCommands.rayTraceTileEntity((EntityPlayerMP) sender);

        if (tileEntity instanceof PreparableInterface) {
            if (((PreparableInterface) tileEntity).prepare(sender, this)) {
                JSGCommand.sendSuccessMess(sender, "Preparing " + tileEntity.getClass().getSimpleName());
            } else {
                JSGCommand.sendErrorMess(sender, "Failed to prepare " + tileEntity.getClass().getSimpleName() + ".");
            }
        } else
            JSGCommand.sendErrorMess(sender, "Can't prepare this block. TileEntity not a instance of PreparableInterface.");
    }
}
