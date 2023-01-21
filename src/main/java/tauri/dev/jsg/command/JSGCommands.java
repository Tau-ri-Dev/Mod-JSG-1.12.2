package tauri.dev.jsg.command;

import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import tauri.dev.jsg.command.stargate.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public final class JSGCommands {

    private static final List<CommandBase> COMMANDS = Arrays.asList(
            new CommandStargateQuery(),
            new CommandPrepare(),
            new CommandStargateCloseAll(),
            new CommandStargateSetAddress(),
            new CommandPageGive(),
            new CommandStargateLinkDHD(),
            new CommandFix(),
            new CommandFixNether(),
            new CommandGenerateIncoming(),
            new CommandAgs(),
            new CommandActiveAll(),
            new CommandCountdownSet(),
            new CommandReloadConfigs(),
            new CommandDestinyFTL(),
            new CommandStargateSetFakePos(),
            new CommandStargateGetFakePos(),
            new CommandStructureSpawn()
    );

    public static void registerCommands(FMLServerStartingEvent event) {
        for (CommandBase command : COMMANDS) {
            event.registerServerCommand(command);
        }
    }

    @Nullable
    public static TileEntity rayTraceTileEntity(@Nonnull EntityPlayerMP player) {
        RayTraceResult rayTraceResult = player.rayTrace(8, 0);
        if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            return player.getEntityWorld().getTileEntity(rayTraceResult.getBlockPos());
        }
        return null;
    }
}
