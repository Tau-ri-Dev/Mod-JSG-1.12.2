package tauri.dev.jsg.command;

import tauri.dev.jsg.JSG;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandAgs extends CommandBase {
    @Override
    public String getName() {
        return "ags";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ags";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        notifyCommandListener(sender, this, JSG.AGS_PATH);
    }
}
