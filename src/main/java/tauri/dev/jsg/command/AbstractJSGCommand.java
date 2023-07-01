package tauri.dev.jsg.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractJSGCommand implements ICommand {

    public final JSGCommand baseCommand;
    public AbstractJSGCommand(JSGCommand baseCommand){
        this.baseCommand = baseCommand;
        baseCommand.addSubCommand(this);
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand p_compareTo_1_) {
        return this.getName().compareTo(p_compareTo_1_.getName());
    }

    @Nonnull
    public abstract String getDescription();

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return getGeneralUsage();
    }

    @Nonnull
    public abstract String getGeneralUsage();

    public abstract int getRequiredPermissionLevel();

    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList<>();
    }
}
