package tauri.dev.jsg.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import tauri.dev.jsg.config.JSGConfig;

import javax.annotation.Nonnull;

public class CommandAgs implements IJSGCommand {
    @Nonnull
    @Override
    public String getName() {
        return "ags";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Sends you the AGS pastebin";
    }

    @Nonnull
    @Override
    public String getUsage() {
        return "ags";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, ICommandSender sender, @Nonnull String[] args) throws CommandException {
        sender.sendMessage(new TextComponentString("Pastebin of AGS: " + TextFormatting.ITALIC + TextFormatting.BOLD + JSGConfig.General.agsPath + TextFormatting.GRAY + " [CLICK ME]").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, JSGConfig.General.agsPath))));
    }
}
