package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.command.IJSGCommand;
import tauri.dev.jsg.command.JSGCommand;
import tauri.dev.jsg.command.JSGCommands;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;

import static java.lang.Integer.parseInt;
import static net.minecraft.command.CommandBase.parseCoordinate;

public class CommandGenerateIncoming extends IJSGCommand {
    @Nonnull
    @Override
    public String getName() {
        return "sgincoming";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Generates incoming wormhole on stargate";
    }

    @Nonnull
    @Override
    public String getUsage() {
        return "sgincoming <x> <y> <z> <entities> <addressLength>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        BlockPos pos = sender.getPosition();
        int x1;
        int y1;
        int z1;
        int entities;
        int addressLength;
        TileEntity tileEntity;
        if (args.length >= 5) {
            x1 = (int) parseCoordinate(pos.getX(), args[0], false).getResult();
            y1 = (int) parseCoordinate(pos.getY(), args[1], 0, 255, false).getResult();
            z1 = (int) parseCoordinate(pos.getZ(), args[2], false).getResult();
            entities = ((args[3] != null) ? parseInt(args[3]) : 5);
            addressLength = ((args[4] != null) ? parseInt(args[4]) : 7);
            BlockPos gatePos = new BlockPos(x1, y1, z1);
            tileEntity = world.getTileEntity(gatePos);
        } else if (args.length >= 2 && sender instanceof EntityPlayerMP) {
            entities = ((args[0] != null) ? parseInt(args[0]) : 5);
            addressLength = ((args[1] != null) ? parseInt(args[1]) : 7);

            tileEntity = JSGCommands.rayTraceTileEntity((EntityPlayerMP) sender);
            if (tileEntity == null) {
                //throw new WrongUsageException("commands.sggenincoming.usage");
                JSGCommand.sendUsageMess(sender, this);
                return;
            }
        } else {
            //throw new WrongUsageException("commands.sggenincoming.usage");
            JSGCommand.sendUsageMess(sender, this);
            return;
        }

        if (entities < 0) entities = 0;
        if (entities > 30) entities = 30;

        if (addressLength < 7) addressLength = 7;
        if (addressLength > 9) addressLength = 9;

        if (tileEntity instanceof StargateClassicBaseTile) {
            // is classic gate tile
            StargateClassicBaseTile gateTile = (StargateClassicBaseTile) tileEntity;
            gateTile.generateIncoming(entities, addressLength);
            JSGCommand.sendSuccessMess(sender, "Successfully executed!");
        } else
            //throw new WrongUsageException("commands.sggenincoming.notstargate");
            JSGCommand.sendErrorMess(sender, "Target block is not a stargate base block!");
    }
}
