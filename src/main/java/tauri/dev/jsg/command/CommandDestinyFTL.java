package tauri.dev.jsg.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.transportrings.StartPlayerFadeOutToClient;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.util.JSGAxisAlignedBB;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.command.CommandBase.parseCoordinate;

public class CommandDestinyFTL implements IJSGCommand {
    @Nonnull
    @Override
    public String getName() {
        return "destinyftl";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Executes destiny's FTL effect";
    }

    @Nonnull
    @Override
    public String getUsage() {
        return "destinyftl [<x> <y> <z>] <range> [from ftl?]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        BlockPos pos = sender.getPosition();
        World world = sender.getEntityWorld();
        int r;

        try {
            if (args.length > 3) {
                int x1 = (int) parseCoordinate(pos.getX(), args[0], false).getResult();
                int y1 = (int) parseCoordinate(pos.getY(), args[1], 0, 255, false).getResult();
                int z1 = (int) parseCoordinate(pos.getZ(), args[2], false).getResult();
                r = Integer.parseInt(args[3]);

                pos = new BlockPos(x1, y1, z1);
            } else {
                r = Integer.parseInt(args[0]);
            }
            BlockPos from = new BlockPos(pos).add(new BlockPos(r, r, r));
            BlockPos to = new BlockPos(pos).subtract(new BlockPos(r, r, r));

            StartPlayerFadeOutToClient.EnumFadeOutEffectType type = StartPlayerFadeOutToClient.EnumFadeOutEffectType.FTL_IN;
            SoundEventEnum sound = SoundEventEnum.DESTINY_FTL_JUMP_IN;
            if (args.length > 4 || args.length == 2) {
                type = StartPlayerFadeOutToClient.EnumFadeOutEffectType.FTL_OUT;
                sound = SoundEventEnum.DESTINY_FTL_JUMP_OUT;
            }

            List<EntityPlayerMP> entities = world.getEntitiesWithinAABB(EntityPlayerMP.class, new JSGAxisAlignedBB(from, to));
            for (EntityPlayerMP e : entities) {
                JSGPacketHandler.INSTANCE.sendTo(new StartPlayerFadeOutToClient(type), e);
                JSGSoundHelper.playSoundToPlayer(e, sound, e.getPosition());
            }
            JSGCommand.sendSuccessMess(sender, "Successfully executed!");
        } catch (Exception e) {
            JSGCommand.sendUsageMess(sender, this);
        }
    }
}
