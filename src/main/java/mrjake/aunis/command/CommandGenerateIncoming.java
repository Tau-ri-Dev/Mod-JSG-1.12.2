package mrjake.aunis.command;

import mrjake.aunis.stargate.network.StargateAddressDynamic;
import mrjake.aunis.stargate.network.StargatePos;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class CommandGenerateIncoming extends CommandBase {
    @Override
    public String getName() {
        return "sggenincoming";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/sggenincoming <x> <y> <z> <entities> <addressLength>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 5) {
            World world = sender.getEntityWorld();
            BlockPos pos = sender.getPosition();
            int x1 = (int) parseCoordinate(pos.getX(), args[0], false).getResult();
            int y1 = (int) parseCoordinate(pos.getY(), args[1], 0, 255, false).getResult();
            int z1 = (int) parseCoordinate(pos.getZ(), args[2], false).getResult();

            int entities        = ((args[3] != null) ? parseInt(args[3]) : 5);
            int addressLength   = ((args[4] != null) ? parseInt(args[4]) : 7);

            if(entities < 0) entities = 0;
            if(entities > 30) entities = 30;

            if(addressLength < 7) addressLength = 7;
            if(addressLength > 9) addressLength = 9;

            BlockPos gatePos = new BlockPos(x1, y1, z1);
            TileEntity tileEntity = world.getTileEntity(gatePos);

            if (tileEntity instanceof StargateClassicBaseTile) {
                // is classic gate tile
                StargateClassicBaseTile gateTile = (StargateClassicBaseTile) tileEntity;
                gateTile.generateIncoming(entities, addressLength);
            } else
                throw new WrongUsageException("commands.sggenincoming.notstargate");
        }
        else throw new WrongUsageException("commands.sggenincoming.usage");
    }
}
