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
import java.util.ArrayList;
import java.util.Arrays;

public final class JSGCommands {

    @SuppressWarnings("unused")
    public static final ArrayList<AbstractJSGCommand> COMMANDS = new ArrayList<>(Arrays.asList(
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
    ));

    /**
     * Used as API
     * <p>
     * Register your sub commands to /jsg command.
     * <p>
     * ! REGISTER YOUR COMMANDS WHEN SERVER IS STARTING !
     * Call this when FMLServerStartingEvent event is fired
     *
     * @param commandInstance - instance of IJSGCommand from your mod
     */
    @SuppressWarnings("unused")
    public static void registerSubCommand(JSGCommand baseCommand, AbstractJSGCommand commandInstance) {
        baseCommand.subCommands.add(commandInstance);
    }

    public static void load() {
    }

    public static void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(JSGCommand.JSG_BASE_COMMAND);
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
