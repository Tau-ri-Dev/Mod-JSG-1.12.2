package tauri.dev.jsg.command.stargate;

import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.LinkingHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public final class CommandStargateLinkDHD extends CommandBase {
  @Override
  public final String getName() {
    return "sglinkdhd";
  }

  @Override
  public final String getUsage(ICommandSender sender) {
    return "/sglinkdhd [radius] [vertical radius]";
  }

  @Override
  public final void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    final int radius = args.length > 0 ? parseInt(args[0]) : JSGConfig.DialHomeDevice.mechanics.rangeFlat;
    final int verticalRadius = args.length > 1 ? parseInt(args[1]) : tauri.dev.jsg.config.JSGConfig.DialHomeDevice.mechanics.rangeVertical;

    final BlockPos radiusPos = new BlockPos(radius, verticalRadius, radius);

    final BlockPos gatePos = LinkingHelper.findClosestUnlinked(sender.getEntityWorld(), sender.getPosition(), radiusPos, JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK, -1);
    final BlockPos dhdPos = LinkingHelper.findClosestUnlinked(sender.getEntityWorld(), sender.getPosition(), radiusPos, JSGBlocks.DHD_BLOCK, -1);

    if (gatePos != null && dhdPos != null) {
      LinkingHelper.updateLinkedGate(sender.getEntityWorld(), gatePos, dhdPos);
    } else {
      notifyCommandListener(sender, this, TextFormatting.RED + "Unable to link to nearest gate: no gates found in radius %s from pos %s", radiusPos.toString(), sender.getPosition().toString());
    }
  }
}
