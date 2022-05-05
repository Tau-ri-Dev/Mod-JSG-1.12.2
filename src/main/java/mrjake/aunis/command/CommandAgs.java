package mrjake.aunis.command;

import mrjake.aunis.Aunis;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandAgs extends CommandBase {
    @Override
    public String getName() {
        return "sggetags";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/sggetags";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        notifyCommandListener(sender, this, Aunis.AGS_PATH);
    }
}
