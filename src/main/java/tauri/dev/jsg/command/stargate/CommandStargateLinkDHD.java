package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.command.AbstractJSGCommand;
import tauri.dev.jsg.command.JSGCommand;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.LinkingHelper;

import javax.annotation.Nonnull;

import static java.lang.Integer.parseInt;

public final class CommandStargateLinkDHD extends AbstractJSGCommand {
    public CommandStargateLinkDHD() {
        super(JSGCommand.JSG_BASE_COMMAND);
    }

    @Nonnull
    @Override
    public String getName() {
        return "sglinkdhd";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Links DHD to stargate";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "sglinkdhd [radius] [vertical radius]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        final int radius = args.length > 0 ? parseInt(args[0]) : JSGConfig.DialHomeDevice.mechanics.rangeFlat;
        final int verticalRadius = args.length > 1 ? parseInt(args[1]) : tauri.dev.jsg.config.JSGConfig.DialHomeDevice.mechanics.rangeVertical;

        final BlockPos radiusPos = new BlockPos(radius, verticalRadius, radius);

        final BlockPos gatePos = LinkingHelper.findClosestUnlinked(sender.getEntityWorld(), sender.getPosition(), radiusPos, JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK, -1);
        final BlockPos dhdPos = LinkingHelper.findClosestUnlinked(sender.getEntityWorld(), sender.getPosition(), radiusPos, JSGBlocks.DHD_BLOCK, -1);

        if (gatePos != null && dhdPos != null) {
            LinkingHelper.updateLinkedGate(sender.getEntityWorld(), gatePos, dhdPos);
            baseCommand.sendSuccessMess(sender, "Successfully executed!");
        } else {
            baseCommand.sendErrorMess(sender, String.format("Unable to link to nearest gate: No gates found in radius %s from pos %s", radiusPos, sender.getPosition()));
            //notifyCommandListener(sender, this, TextFormatting.RED + "Unable to link to nearest gate: no gates found in radius %s from pos %s", radiusPos.toString(), sender.getPosition().toString());
        }
    }
}
