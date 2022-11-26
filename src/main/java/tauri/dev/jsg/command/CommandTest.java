package tauri.dev.jsg.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import tauri.dev.jsg.JSG;
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
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        JSGStructuresGenerator.generateStructure(EnumStructures.NAQUADAH_MINE, sender.getEntityWorld(), new Random(), sender.getPosition().getX()/16, sender.getPosition().getZ()/16, true);
        notifyCommandListener(sender, this, JSG.AGS_PATH);
    }
}
