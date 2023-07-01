package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.command.AbstractJSGCommand;
import tauri.dev.jsg.command.JSGCommand;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

import javax.annotation.Nonnull;

import static net.minecraft.command.CommandBase.parseCoordinate;

public class CommandStargateSetAddress extends AbstractJSGCommand {

    public CommandStargateSetAddress() {
        super(JSGCommand.JSG_BASE_COMMAND);
    }

    @Nonnull
    @Override
    public String getName() {
        return "sgsetaddress";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Sets address of target gate";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "sgsetaddress <x y z> <map=UNIVERSE|MILKYWAY|PEGASUS> <8x symbol>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        World world = sender.getEntityWorld();

        if (args.length < 12) {
            //throw new WrongUsageException("commands.sgsetaddress.usage");
            baseCommand.sendUsageMess(sender, this);
            return;
        }

        SymbolTypeEnum symbolType = null;

        if (args[3].startsWith("map=")) {
            try {
                symbolType = SymbolTypeEnum.valueOf(args[3].substring(4).toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (symbolType == null) {
            //throw new WrongUsageException("commands.sgsetaddress.noaddressspace");
            baseCommand.sendErrorMess(sender, "Wrong map!");
            return;
        }

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(symbolType);
        int index = 1;

        for (int i = args.length - 8; i < args.length; i++) {
            SymbolInterface symbol = symbolType.fromEnglishName(args[i].replace("-", " "));

            if (symbol == null) {
                //throw new WrongUsageException("commands.sgsetaddress.wrongsymbol", index);
                baseCommand.sendErrorMess(sender, "Wrong symbol at position " + index + "!");
                return;
            }

            if (stargateAddress.contains(symbol)) {
                //throw new WrongUsageException("commands.sgsetaddress.duplicatesymbol", index);
                baseCommand.sendErrorMess(sender, "Duplicated symbol at position " + index + "!");
                return;
            }

            stargateAddress.addSymbol(symbol);
            index++;
        }

        if (StargateNetwork.get(world).isStargateInNetwork(stargateAddress)) {
            //throw new WrongUsageException("commands.sgsetaddress.exists");
            baseCommand.sendErrorMess(sender, "Gate with that address already exists!");
            return;
        }

        BlockPos pos = sender.getPosition();
        int x1 = (int) parseCoordinate(pos.getX(), args[0], false).getResult();
        int y1 = (int) parseCoordinate(pos.getY(), args[1], 0, 255, false).getResult();
        int z1 = (int) parseCoordinate(pos.getZ(), args[2], false).getResult();

        BlockPos gatePos = new BlockPos(x1, y1, z1);
        TileEntity tileEntity = world.getTileEntity(gatePos);

        if (tileEntity instanceof StargateAbstractBaseTile) {
            StargateAbstractBaseTile gateTile = (StargateAbstractBaseTile) tileEntity;

            gateTile.setGateAddress(symbolType, stargateAddress.toImmutable());
            //notifyCommandListener(sender, this, "commands.sgsetaddress.success", gateTile.getPos().toString(), stargateAddress.toString());
            baseCommand.sendSuccessMess(sender, "Address successfully changed!");
        } else
            //throw new WrongUsageException("commands.sgsetaddress.notstargate");
            baseCommand.sendErrorMess(sender, "Target block is not a stargate base block!");
    }

}
