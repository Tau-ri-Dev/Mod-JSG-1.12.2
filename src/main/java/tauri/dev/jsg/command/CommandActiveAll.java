package tauri.dev.jsg.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import java.util.Map;

public class CommandActiveAll extends CommandBase {
    @Override
    public String getName() {
        return "sgactiveall";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/sgactiveall [entities] [addressLength]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        StargateNetwork network = StargateNetwork.get(sender.getEntityWorld());
        Map<StargateAddress, StargatePos> map = network.getMap().get(SymbolTypeEnum.MILKYWAY);

        int entities        = ((args.length >= 4 && args[3] != null) ? parseInt(args[3]) : 5);
        int addressLength   = ((args.length >= 5 && args[4] != null) ? parseInt(args[4]) : 7);

        if(entities < 0) entities = 0;
        if(entities > 30) entities = 30;

        if(addressLength < 7) addressLength = 7;
        if(addressLength > 9) addressLength = 9;

        int id = 0;

        for (StargateAddress address : map.keySet()) {
            StargatePos selectedStargatePos = map.get(address);
            BlockPos pos = selectedStargatePos.gatePos;
            World world = selectedStargatePos.getWorld();

            TileEntity tileEntity = world.getTileEntity(pos);

            if (tileEntity instanceof StargateClassicBaseTile) {
                // is classic gate tile
                StargateClassicBaseTile gateTile = (StargateClassicBaseTile) tileEntity;
                gateTile.generateIncoming(entities, addressLength);
                id++;
            }
        }

        notifyCommandListener(sender, this, "Executed at " + id + " gates");
    }
}
