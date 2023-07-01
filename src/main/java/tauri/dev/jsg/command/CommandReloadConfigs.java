package tauri.dev.jsg.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import tauri.dev.jsg.config.JSGConfigUtil;
import tauri.dev.jsg.config.craftings.CraftingConfig;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.config.structures.StructureConfig;

import javax.annotation.Nonnull;

public class CommandReloadConfigs extends IJSGCommand {
    @Nonnull
    @Override
    public String getName() {
        return "reloadconfig";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Reloads configs";
    }

    @Nonnull
    @Override
    public String getUsage() {
        return "reloadconfig";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        try {
            CraftingConfig.reload();
            StructureConfig.reload();
            JSGConfigUtil.reloadConfig();
            StargateDimensionConfig.reload();
        } catch (Exception e){
            e.printStackTrace();
        }
        JSGCommand.sendSuccessMess(sender, "Configs reloaded!");
    }

}
