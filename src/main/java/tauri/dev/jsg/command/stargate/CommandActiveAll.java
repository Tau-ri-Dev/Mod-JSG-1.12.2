package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.command.IJSGCommand;
import tauri.dev.jsg.command.JSGCommand;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class CommandActiveAll extends IJSGCommand {
    @Override
    @Nonnull
    public String getName() {
        return "sgactiveall";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Actives all stargates !CAN CAUSE LAGS!";
    }

    @Override
    @Nonnull
    public String getUsage() {
        return "sgactiveall [entities] [addressLength]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        StargateNetwork network = StargateNetwork.get(sender.getEntityWorld());
        Map<StargateAddress, StargatePos> map = network.getMap().get(SymbolTypeEnum.MILKYWAY);

        int entities = ((args.length >= 4 && args[3] != null) ? parseInt(args[3]) : 5);
        int addressLength = ((args.length >= 5 && args[4] != null) ? parseInt(args[4]) : 7);

        if (entities < 0) entities = 0;
        if (entities > 30) entities = 30;

        if (addressLength < 7) addressLength = 7;
        if (addressLength > 9) addressLength = 9;

        int i = 0;

        for (StargateAddress address : map.keySet()) {
            StargatePos selectedStargatePos = map.get(address);
            BlockPos pos = selectedStargatePos.gatePos;
            World world = selectedStargatePos.getWorld();

            TileEntity tileEntity = world.getTileEntity(pos);

            if (tileEntity instanceof StargateClassicBaseTile) {
                // is classic gate tile
                StargateClassicBaseTile gateTile = (StargateClassicBaseTile) tileEntity;
                gateTile.generateIncoming(entities, addressLength);
                i++;
            }
        }
        JSGCommand.sendSuccessMess(sender, "Successfully opened " + i + " gates!");
    }
}
