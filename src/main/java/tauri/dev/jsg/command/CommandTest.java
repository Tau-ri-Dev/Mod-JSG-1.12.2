package tauri.dev.jsg.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.transportrings.StartPlayerFadeOutToClient;
import tauri.dev.jsg.worldgen.structures.EnumStructures;
import tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator;

import java.util.Random;

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
            JSGPacketHandler.INSTANCE.sendTo(new StartPlayerFadeOutToClient(StartPlayerFadeOutToClient.EnumFadeOutEffectType.FTL), (EntityPlayerMP) sender);
        }
        notifyCommandListener(sender, this, JSG.AGS_PATH);
    }
}
