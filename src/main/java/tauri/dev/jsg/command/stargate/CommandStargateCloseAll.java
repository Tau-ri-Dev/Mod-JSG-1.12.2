package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.command.IJSGCommand;
import tauri.dev.jsg.command.JSGCommand;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.command.CommandBase.parseCoordinate;

public class CommandStargateCloseAll extends IJSGCommand {

    @Nonnull
    @Override
    public String getName() {
        return "sgcloseall";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Closes all stargates in box/dim/global";
    }

    @Nonnull
    @Override
    public String getUsage() {
        return "sgcloseall [x1 y1 z1 x2 y2 z2] [dim=id|current] [force=false|true]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }


    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        AxisAlignedBB queryBox = null;

        if (args.length >= 1 && args[0].equals("help")) {
            //throw new WrongUsageException("commands.sgcloseall.usage");
            JSGCommand.sendUsageMess(sender, this);
            return;
        }

        boolean force = false;
        boolean checkDim = false;
        int dimId = 0;

        try {
            if (args.length >= 6) {
                BlockPos pos = sender.getPosition();
                int x1 = (int) parseCoordinate(pos.getX(), args[0], false).getResult();
                int y1 = (int) parseCoordinate(pos.getY(), args[1], 0, 255, false).getResult();
                int z1 = (int) parseCoordinate(pos.getZ(), args[2], false).getResult();
                int x2 = (int) parseCoordinate(pos.getX(), args[3], false).getResult();
                int y2 = (int) parseCoordinate(pos.getY(), args[4], 0, 255, false).getResult();
                int z2 = (int) parseCoordinate(pos.getZ(), args[5], false).getResult();

                BlockPos sPos = new BlockPos(x1, y1, z1);
                BlockPos tPos = new BlockPos(x2, y2, z2);

                queryBox = new AxisAlignedBB(sPos, tPos);

            }

            for (String arg : args) {
                if (arg.startsWith("dim=")) {
                    checkDim = true;
                    String sub = arg.substring(4);

                    if (sub.equals("current"))
                        dimId = sender.getEntityWorld().provider.getDimension();
                    else
                        dimId = Integer.parseInt(sub);

                    break;
                } else if (arg.startsWith("force=")) {
                    if (Boolean.parseBoolean(arg.substring(6))) {
                        force = true;
                    }
                }
            }

        } catch (NumberFormatException e) {
            //throw new WrongUsageException("commands.sgquery.number_expected");
            JSGCommand.sendErrorMess(sender, "Number expected!");
            return;
        }

        JSGCommand.sendRunningMess(sender, "Closing gates with parameters [dimId=" + (checkDim ? dimId : "any") + ", box=" + (queryBox != null ? queryBox.toString() : "infinite") + "]...");
        //notifyCommandListener(sender, this, "commands.sgcloseall.closing", checkDim ? dimId : "any", (queryBox != null ? queryBox.toString() : "box=infinite"));

        StargateNetwork network = StargateNetwork.get(sender.getEntityWorld());
        Map<StargateAddress, StargatePos> map = network.getMap().get(SymbolTypeEnum.MILKYWAY);
        List<StargateAddress> toBeRemoved = new ArrayList<>();

        int closed = 0;

        for (StargateAddress address : map.keySet()) {
            StargatePos stargatePos = network.getStargate(address);

            if (stargatePos != null && checkDim && stargatePos.dimensionID != dimId) continue;

            if (stargatePos != null && queryBox != null && !queryBox.contains(new Vec3d(stargatePos.gatePos))) continue;

            StargateAbstractBaseTile gateTile = null;
            if (stargatePos != null) {
                gateTile = stargatePos.getTileEntity();
            }

            if (gateTile != null) {
                if (gateTile.getStargateState().initiating() || (force && gateTile.getStargateState().engaged()) || gateTile.randomIncomingIsActive) {
                    gateTile.attemptClose(StargateClosedReasonEnum.COMMAND);
                    closed++;
                }
            } else {
                toBeRemoved.add(address);
            }
        }

        for (StargateAddress address : toBeRemoved) {
            network.removeStargate(address);
            JSG.warn("Removing address " + address);
        }

        JSGCommand.sendSuccessMess(sender, "Closed " + closed + " gates!");

        //notifyCommandListener(sender, this, "commands.sgcloseall.closed", closed);
    }

}
