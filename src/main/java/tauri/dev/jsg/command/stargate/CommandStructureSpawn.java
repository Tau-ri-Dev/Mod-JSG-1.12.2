package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import tauri.dev.jsg.worldgen.structures.EnumStructures;
import tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator;
import tauri.dev.jsg.worldgen.util.JSGWorldTopBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static tauri.dev.jsg.worldgen.util.JSGWorldTopBlock.getTopBlock;

public class CommandStructureSpawn extends CommandBase {
    @Nonnull
    @Override
    public String getName() {
        return "jsggeneratestructure";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/jsggeneratestructure <structure name> <x> <z> [dimId]";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if (args.length < 3)
            throw new WrongUsageException("Use: " + getUsage(sender));

        World world = sender.getEntityWorld();

        int dimId = world.provider.getDimension();
        BlockPos pos = sender.getPosition();
        try {
            EnumStructures structure = EnumStructures.getStructureByName(args[0]);
            if (structure == null)
                throw new WrongUsageException("Structure whit that name not found!");

            if (args.length >= 45)
                dimId = Integer.parseInt(args[3]);

            if (!DimensionManager.isDimensionRegistered(dimId))
                throw new WrongUsageException("Dimension not found!");

            double x = parseCoordinate(pos.getX(), args[1], false).getResult();
            double z = parseCoordinate(pos.getZ(), args[2], false).getResult();

            JSGWorldTopBlock topBlock = getTopBlock(server.getWorld(dimId), (int) Math.round(x), (int) Math.round(z), structure.getActualStructure(dimId).airUp, dimId);

            if(topBlock == null)
                throw new CommandException("Can not get top block!");
            structure.getActualStructure(dimId).generateStructure(world, new BlockPos(x, topBlock.y, z), new Random(), server.getWorld(dimId));

        } catch (Exception e) {
            throw new WrongUsageException("Use: " + getUsage(sender));
        }
    }

    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
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