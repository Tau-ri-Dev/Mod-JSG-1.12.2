package tauri.dev.jsg.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import tauri.dev.jsg.command.stargate.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public final class JSGCommands {

    public static final List<IJSGCommand> COMMANDS = Arrays.asList(
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

            // Should be last
            // Is added when server is started
            //new JSGCommand.CommandHelp()
    );
    /**
     * Used as API
     *
     * Register your sub commands to /jsg command.
     *
     * ! REGISTER YOUR COMMANDS WHEN SERVER IS STARTING !
     * Call this when FMLServerStartingEvent event is fired
     *
     * @param commandInstance - instance of IJSGCommand from your mod
     */
    @SuppressWarnings("unused")
    public static void registerSubCommand(IJSGCommand commandInstance){
        COMMANDS.add(commandInstance);
    }

    public static void load(){}

    public static void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(JSGCommand.INSTANCE);
    }

    public static RayTraceResult rayTraceEntity(Entity e, double blockReachDistance, float partialTicks) {
        Vec3d vec3d = e.getPositionEyes(partialTicks);
        Vec3d vec3d1 = e.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return e.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }

    @Nullable
    public static TileEntity rayTraceTileEntity(@Nonnull EntityPlayerMP player) {
        try {
            RayTraceResult rayTraceResult = rayTraceEntity(player, 8, 0);
            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                return player.getEntityWorld().getTileEntity(rayTraceResult.getBlockPos());
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
