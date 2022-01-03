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

public class CommandTestIncomingEntity extends CommandBase {
    @Override
    public String getName() {
        return "sgtest";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/sgtest <x> <y> <z>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();

        BlockPos pos = sender.getPosition();
        int x1 = (int) parseCoordinate(pos.getX(), args[0], false).getResult();
        int y1 = (int) parseCoordinate(pos.getY(), args[1], 0, 255, false).getResult();
        int z1 = (int) parseCoordinate(pos.getZ(), args[2], false).getResult();

        BlockPos gatePos = new BlockPos(x1, y1, z1);
        TileEntity tileEntity = world.getTileEntity(gatePos);

        if (tileEntity instanceof StargateClassicBaseTile) {
            // is classic gate tile
            StargateClassicBaseTile gateTile = (StargateClassicBaseTile) tileEntity;
            gateTile.generateIncoming(0, 5, 7);
        }
        else
            throw new WrongUsageException("commands.sgsetaddress.notstargate");
    }
}
