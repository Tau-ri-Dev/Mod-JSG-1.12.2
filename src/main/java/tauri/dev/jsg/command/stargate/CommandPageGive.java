package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import tauri.dev.jsg.command.AbstractJSGCommand;
import tauri.dev.jsg.command.JSGCommand;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.notebook.PageNotebookItem;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.command.CommandBase.getPlayer;
import static tauri.dev.jsg.loader.OriginsLoader.DEFAULT_ORIGIN_ID;
import static tauri.dev.jsg.loader.OriginsLoader.getAllOrigins;

public class CommandPageGive extends AbstractJSGCommand {

    public CommandPageGive() {
        super(JSGCommand.JSG_BASE_COMMAND);
    }

    @Nonnull
    @Override
    public String getName() {
        return "sggivepage";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Gives page with specified address";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "sggivepage <player> <map=MILKYWAY|PEGASUS|UNIVERSE> <biome=biome_name> [origin=origin_id] <6x symbol>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        boolean hasUpgrade = true;

        if (args.length < 8) {
            baseCommand.sendUsageMess(sender, this);
            return;
        }

        if (args.length < 10)
            hasUpgrade = false;

        SymbolTypeEnum symbolType = null;
        String biome = "plains";
        int originId = DEFAULT_ORIGIN_ID;

        for (int i = 1; i <= 3; i++) {
            if (args[i].startsWith("map=")) {
                try {
                    symbolType = SymbolTypeEnum.valueOf(args[i].substring(4).toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            } else if (args[i].startsWith("biome=")) {
                biome = args[i].substring(4);
            } else if (args[i].startsWith("origin=")) {
                try {
                    originId = Integer.parseInt(args[i].substring(4));
                } catch (Exception ignored) {
                }
            }
        }

        if (!getAllOrigins().contains(originId)) {
            originId = DEFAULT_ORIGIN_ID;
        }

        if (symbolType == null) {
            baseCommand.sendUsageMess(sender, this);
            return;
        }

        EntityPlayer player = getPlayer(server, sender, args[0]);

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(symbolType);
        int index = 1;

        for (int i = args.length - (hasUpgrade ? 8 : 6); i < args.length; i++) {
            SymbolInterface symbol = symbolType.fromEnglishName(args[i].replace("-", " "));

            if (symbol == null) {
                baseCommand.sendErrorMess(sender, "Wrong symbol name at position " + index + "!");
                return;
            }

            stargateAddress.addSymbol(symbol);
            index++;
        }

        NBTTagCompound compound = PageNotebookItem.getCompoundFromAddress(
                stargateAddress,
                hasUpgrade,
                biome,
                originId);

        ItemStack stack = new ItemStack(JSGItems.PAGE_NOTEBOOK_ITEM, 1, 1);
        stack.setTagCompound(compound);
        player.addItemStackToInventory(stack);
        baseCommand.sendSuccessMess(sender, "Successfully executed!");
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {

        SymbolTypeEnum symbolType = null;

        if (args.length > 1 && args[1].startsWith("map=")) {
            try {
                symbolType = SymbolTypeEnum.valueOf(args[1].substring(4).toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (args.length == 1) {
            return Arrays.asList(server.getOnlinePlayerNames());
        } else if (args.length == 2) {
            ArrayList<String> maps = new ArrayList<>();
            for (SymbolTypeEnum s : SymbolTypeEnum.values())
                maps.add("map=" + s.name().toUpperCase());
            return maps;
        } else if (args.length == 3) {
            ArrayList<String> biomesArray = new ArrayList<>();
            List<Biome> biomes = server.getWorld(0).getBiomeProvider().getBiomesToSpawnIn();
            for (Biome s : biomes) {
                if (s.getRegistryName() == null) continue;
                biomesArray.add("biome=" + s.getRegistryName().getResourcePath());
            }
            return biomesArray;
        } else if (args.length == 4 && symbolType == SymbolTypeEnum.MILKYWAY) {
            ArrayList<String> originsArray = new ArrayList<>();
            for (Integer o : getAllOrigins()) {
                originsArray.add("origin=" + o);
            }
            return originsArray;
        } else if (args.length <= (symbolType == SymbolTypeEnum.MILKYWAY ? 11 : 10)) {
            if (symbolType != null) {
                ArrayList<String> symbols = new ArrayList<>();
                for (SymbolInterface s : symbolType.getValues()) {
                    symbols.add(s.getEnglishName().replaceAll(" ", "-"));
                }
                return symbols;
            }
        }
        return new ArrayList<>();
    }
}
