package tauri.dev.jsg.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.transportrings.StartPlayerFadeOutToClient;

public class CommandTest extends CommandBase {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/test";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayerMP) {
            StartPlayerFadeOutToClient.EnumFadeOutEffectType type = StartPlayerFadeOutToClient.EnumFadeOutEffectType.FTL_IN;
            if(args.length > 0) type = StartPlayerFadeOutToClient.EnumFadeOutEffectType.FTL_OUT;

            JSGPacketHandler.INSTANCE.sendTo(new StartPlayerFadeOutToClient(type), (EntityPlayerMP) sender);
        }
        notifyCommandListener(sender, this, JSG.AGS_PATH);
    }
}
