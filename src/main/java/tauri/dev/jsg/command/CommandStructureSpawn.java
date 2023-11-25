package tauri.dev.jsg.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.worldgen.structures.EnumStructures;
import tauri.dev.jsg.worldgen.util.JSGWorldTopBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.minecraft.command.CommandBase.*;
import static tauri.dev.jsg.worldgen.util.JSGWorldTopBlock.getTopBlock;

public class CommandStructureSpawn extends AbstractJSGCommand {

    public CommandStructureSpawn() {
        super(JSGCommand.JSG_BASE_COMMAND);
    }
    @Nonnull
    @Override
    public String getName() {
        return "structurespawn";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Spawns a JSG structure";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "structurespawn <structure name> <x> <z> [dimId]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if (args.length < 3) {
            baseCommand.sendUsageMess(sender, this);
            return;
        }

        World world = sender.getEntityWorld();

        int dimId = world.provider.getDimension();
        BlockPos pos = sender.getPosition();
        Rotation rotation = null;
        if (sender instanceof EntityPlayer) {
            rotation = FacingHelper.getRotation(((EntityPlayer) sender).getHorizontalFacing());
        }
        try {
            EnumStructures structure = EnumStructures.getStructureByName(args[0]);
            if (structure == null) {
                baseCommand.sendErrorMess(sender, "Structure with that name not found!");
                return;
            }

            if (args.length >= 45)
                dimId = Integer.parseInt(args[3]);

            if (!DimensionManager.isDimensionRegistered(dimId)){
                baseCommand.sendErrorMess(sender, "Dimension not found");
                return;
            }

            double x = parseCoordinate(pos.getX(), args[1], false).getResult();
            double z = parseCoordinate(pos.getZ(), args[2], false).getResult();

            JSGWorldTopBlock topBlock = getTopBlock(server.getWorld(dimId), (int) Math.round(x), (int) Math.round(z), structure.getActualStructure(dimId).airUp, dimId);

            if (topBlock == null){
                baseCommand.sendErrorMess(sender, "Can not get top block!");
                return;
            }

            if(!structure.getActualStructure(dimId).findOptimalRotation) rotation = null;

            structure.getActualStructure(dimId).generateStructure(world, new BlockPos(x, topBlock.y, z), new Random(), server.getWorld(dimId), rotation);
            baseCommand.sendSuccessMess(sender, "Successfully spawned!");

        } catch (Exception e) {
            baseCommand.sendErrorMess(sender, "Can not place structure here!");
        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, EnumStructures.getAllStructureNames());
        }
        if (args.length == 4) {
            return getListOfStringsMatchingLastWord(args, Arrays.asList(DimensionManager.getStaticDimensionIDs()));
        } else {
            return args.length > 1 && args.length <= 3 ? getTabCompletionCoordinate(args, 1, targetPos) : Collections.emptyList();
        }
    }
}