package tauri.dev.jsg.command.stargate;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import tauri.dev.jsg.command.AbstractJSGCommand;
import tauri.dev.jsg.command.JSGCommand;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.notebook.PageNotebookItem;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

import javax.annotation.Nonnull;

import static net.minecraft.command.CommandBase.getPlayer;

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
        return "sggivepage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        boolean hasUpgrade = true;

        if (args.length < 8) {
            //throw new WrongUsageException("commands.sggivepage.usage");
            baseCommand.sendUsageMess(sender, this);
            return;
        }

        if (args.length < 10)
            hasUpgrade = false;

        SymbolTypeEnum symbolType = null;
        String biome = "plains";

        for (int i = 1; i < 3; i++) {
            if (args[i].startsWith("map=")) {
                try {
                    symbolType = SymbolTypeEnum.valueOf(args[i].substring(4).toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            } else if (args[i].startsWith("biome=")) {
                biome = args[i].substring(4);
            }
        }

        if (symbolType == null) {
            baseCommand.sendUsageMess(sender, this);
            return;
            //throw new WrongUsageException("commands.sggivepage.no_map");
        }

        EntityPlayer player = getPlayer(server, sender, args[0]);
        //notifyCommandListener(sender, this, "commands.sggivepage.give_page", player.getName());

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(symbolType);
        int index = 1;

        for (int i = args.length - (hasUpgrade ? 8 : 6); i < args.length; i++) {
            SymbolInterface symbol = symbolType.fromEnglishName(args[i].replace("-", " "));

            if (symbol == null) {
                baseCommand.sendErrorMess(sender, "Wrong symbol name at position " + index + "!");
                return;
                //throw new WrongUsageException("commands.sgsetaddress.wrongsymbol", index);
            }

            stargateAddress.addSymbol(symbol);
            index++;
        }

        NBTTagCompound compound = PageNotebookItem.getCompoundFromAddress(
                stargateAddress,
                hasUpgrade,
                biome,
                -1);

        ItemStack stack = new ItemStack(JSGItems.PAGE_NOTEBOOK_ITEM, 1, 1);
        stack.setTagCompound(compound);
        player.addItemStackToInventory(stack);
        baseCommand.sendSuccessMess(sender, "Successfully executed!");
    }

}
