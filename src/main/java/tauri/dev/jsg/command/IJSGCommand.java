package tauri.dev.jsg.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface IJSGCommand extends ICommand {
    @Nonnull
    default List<String> getAliases() {
        return Collections.<String>emptyList();
    }

    default boolean checkPermission(@Nonnull MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
    }

    default boolean isUsernameIndex(@Nonnull String[] args, int index)
    {
        return false;
    }

    default int compareTo(ICommand p_compareTo_1_)
    {
        return this.getName().compareTo(p_compareTo_1_.getName());
    }

    @Nonnull
    String getName();

    @Nonnull
    String getDescription();

    @Nonnull
    default String getUsage(@Nonnull ICommandSender sender){
        return getUsage();
    }

    @Nonnull
    String getUsage();

    int getRequiredPermissionLevel();

    void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException;

    @Nonnull
    default List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList<>();
    }
}
