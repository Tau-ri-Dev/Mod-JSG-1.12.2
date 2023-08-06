package tauri.dev.jsg.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import tauri.dev.jsg.api.StargateGenerator;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.util.RayTraceHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class CommandTest extends AbstractJSGCommand {
    public CommandTest() {
        super(JSGCommand.JSG_BASE_COMMAND);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Testing command for devs";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "test";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Nonnull
    @Override
    public String getName() {
        return "test";
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        BlockPos target = RayTraceHelper.rayTracePos((EntityPlayer) sender, 20);
        if (target == null) return;
        if (args.length > 1) {
            StargateGenerator.PlacementConfig config = new StargateGenerator.PlacementConfig();
            config.world = sender.getEntityWorld();
            config.gateBasePos = target.down();
            config.gateType = SymbolTypeEnum.valueOf(args[0]);
            config.gateFacing = ((EntityPlayer) sender).getHorizontalFacing().getOpposite();

            config.dhdPos = target.offset(config.gateFacing, 6).up();
            StargateGenerator.generateStargate(config);
        } else if (args.length == 1) {
            StargateGenerator.switchGateType(sender.getEntityWorld(), target, SymbolTypeEnum.valueOf(args[0]), true);
        }
    }
}
