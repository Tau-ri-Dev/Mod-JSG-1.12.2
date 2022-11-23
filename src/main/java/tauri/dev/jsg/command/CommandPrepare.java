package tauri.dev.jsg.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import tauri.dev.jsg.tileentity.util.PreparableInterface;

import javax.annotation.Nonnull;

public class CommandPrepare extends CommandBase {

	@Override
	public String getName() {
		return "sgprepare";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/sgprepare";
	}

	@Override
	public void execute(MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException {
		TileEntity tileEntity = JSGCommands.rayTraceTileEntity((EntityPlayerMP) sender);

		if (tileEntity instanceof PreparableInterface) {
			if (((PreparableInterface) tileEntity).prepare(sender, this)) {
				notifyCommandListener(sender, this, "Preparing " + tileEntity.getClass().getSimpleName());
			} else {
				notifyCommandListener(sender, this, TextFormatting.RED + "Failed to prepare " + tileEntity.getClass().getSimpleName() + ".");
			}
		} else
			notifyCommandListener(sender, this, "Can't prepare this block. TileEntity not a instance of PreparableInterface.");
	}
}
