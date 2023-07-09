package tauri.dev.jsg.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import tauri.dev.jsg.config.origins.OriginsLoader;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class CommandImportOrigins extends AbstractJSGCommand {
    public CommandImportOrigins() {
        super(JSGCommand.JSG_BASE_COMMAND);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Imports additional origins into config of the server (on singleplayer to config of the client) from file located in \"config/jsg/assets/loader/origins.json\"";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "originsimport";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Nonnull
    @Override
    public String getName() {
        return "originsimport";
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(OriginsLoader.loadOriginsToConfig())
            baseCommand.sendSuccessMess(sender, "Origins imported!");
        else {
            baseCommand.sendErrorMess(sender, "Error occurred while attempting to import origins!");
            baseCommand.sendErrorMess(sender, "Check the importing file");
        }
    }
}
