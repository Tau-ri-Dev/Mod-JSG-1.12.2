package tauri.dev.jsg.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSGCommand extends CommandBase {

    private final String name;
    protected final CommandHelp helpCommand;
    public final ArrayList<AbstractJSGCommand> subCommands = new ArrayList<>();

    public JSGCommand(String baseCommandName) {
        this.name = baseCommandName;
        helpCommand = new CommandHelp(this);
    }

    public void addSubCommand(AbstractJSGCommand commands) {
        this.subCommands.add(commands);
    }

    public static final JSGCommand JSG_BASE_COMMAND = new JSGCommand("jsg");

    public static class CommandHelp extends AbstractJSGCommand {

        public CommandHelp(JSGCommand baseCommand) {
            super(baseCommand);
        }

        @Nonnull
        @Override
        public String getName() {
            return "help";
        }

        @Nonnull
        @Override
        public String getDescription() {
            return "Shows this list";
        }

        @Nonnull
        @Override
        public String getGeneralUsage() {
            return "help";
        }

        @Override
        public int getRequiredPermissionLevel() {
            return -1;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
            int page = 1;
            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch (Exception ignored) {
                }
            }
            baseCommand.showHelp(sender, page);
        }
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    public String getTitle(){
        return "Just Stargate Mod";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @ParametersAreNonnullByDefault
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return "Use /" + getName() + " help for help";
    }

    public void sendNoPerms(ICommandSender sender) {
        sendErrorMess(sender, "You don't have permissions to do that!");
    }

    public void sendErrorMess(ICommandSender sender, String mess) {
        sender.sendMessage(new TextComponentString(" \u00a7c\u00a7lOps! \u00a77" + mess));
    }

    public void sendSuccessMess(ICommandSender sender, String mess) {
        sender.sendMessage(new TextComponentString(" \u00a7a\u00a7lDone! \u00a77" + mess));
    }

    public void sendInfoMess(ICommandSender sender, String mess) {
        sender.sendMessage(new TextComponentString(" \u00a73\u00a7l\u2502 \u00a77" + mess));
    }

    public void sendUsageMess(ICommandSender sender, AbstractJSGCommand cmd) {
        sender.sendMessage(new TextComponentString(" \u00a73\u00a7lUsage: \u00a77/" + getName() + " " + cmd.getUsage(sender)));
    }

    public void sendRunningMess(ICommandSender sender, String mess) {
        sender.sendMessage(new TextComponentString(" \u00a76\u00a7lRunning: \u00a77" + mess));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int argsLength = args.length;
        if (argsLength <= 0) {
            showHelp(sender, 1);
            return;
        }

        // Get subcommand
        AbstractJSGCommand command = null;
        for (AbstractJSGCommand c : subCommands) {
            if (c.getName().equalsIgnoreCase(args[0])) {
                command = c;
                break;
            }
        }
        if (command == null) {
            sendErrorMess(sender, "Unknown subcommand! Type /" + getName() + " help for help");
            return;
        }

        // Check permissions
        if (!canUseCommand(sender, command.getRequiredPermissionLevel())) {
            sendNoPerms(sender);
            return;
        }

        // Remove first argument
        List<String> a = Arrays.asList(args);
        if (a.size() >= 2)
            a = a.subList(1, a.size());
        else
            a = new ArrayList<>();

        // Execute
        command.execute(server, sender, a.toArray(new String[0]));
    }

    public boolean canUseCommand(ICommandSender sender, int requiredPerms) {
        if (requiredPerms <= 0) return true;
        return sender.canUseCommand(requiredPerms, getName());
    }

    public ITextComponent getCommandTextComponentForHelp(AbstractJSGCommand cmd) {
        TextComponentBase text = new TextComponentString(TextFormatting.DARK_AQUA + " /" + getName() + " " + cmd.getName());
        Style style = new Style();
        HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.WHITE + "" + TextFormatting.BOLD + "/" + getName() + " " + cmd.getGeneralUsage() + "\n" + TextFormatting.GRAY + cmd.getDescription()));
        style.setHoverEvent(he);
        ClickEvent ce = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + getName() + " " + cmd.getName());
        style.setClickEvent(ce);
        text.setStyle(style);
        return text;
    }

    public void showHelp(ICommandSender sender, int page) {
        sender.sendMessage(new TextComponentString(TextFormatting.STRIKETHROUGH + "------" + TextFormatting.RESET + " " + TextFormatting.AQUA + TextFormatting.BOLD + getTitle() + " " + TextFormatting.RESET + TextFormatting.STRIKETHROUGH + "------"));

        ArrayList<AbstractJSGCommand> commands = new ArrayList<>();
        for (AbstractJSGCommand c : subCommands) {
            if (canUseCommand(sender, c.getRequiredPermissionLevel())) {
                commands.add(c);
            }
        }

        int count = commands.size();
        final int perPage = 10;
        final int maxPage = (int) Math.ceil((double) count / perPage);
        page = Math.max(1, Math.min(maxPage, page));

        int start = perPage * (page - 1);
        int end = perPage * page;

        int i = 0;
        for (AbstractJSGCommand c : commands) {
            i++;
            if (i <= start) continue;
            if (i > end) break;
            sender.sendMessage(getCommandTextComponentForHelp(c));
        }

        TextComponentString back = (TextComponentString) new TextComponentString("\u00a73\u00a7l\u00a7m<--\u00a7r").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getName() + " help " + (page - 1))).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Previous page"))));
        TextComponentString next = (TextComponentString) new TextComponentString("\u00a73\u00a7l\u00a7m-->\u00a7r").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getName() + " help " + (page + 1))).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Next page"))));
        TextComponentString arrows = new TextComponentString("       ");
        if (page - 1 > 0)
            arrows.appendSibling(back);
        else
            arrows.appendSibling(new TextComponentString("\u00a78\u00a7l\u00a7m<--\u00a7r"));
        arrows.appendSibling(new TextComponentString(" \u00a77(" + page + ")\u00a7r "));
        if (page + 1 <= maxPage)
            arrows.appendSibling(next);
        else
            arrows.appendSibling(new TextComponentString("\u00a78\u00a7l\u00a7m-->\u00a7r"));
        sender.sendMessage(arrows);
        sender.sendMessage(new TextComponentString(TextFormatting.STRIKETHROUGH + "--------------------------------"));
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> list = new ArrayList<>();

        int argsLength = args.length;
        if (argsLength <= 0) return list;
        if (argsLength == 1) {
            List<String> names = new ArrayList<>();
            for (AbstractJSGCommand c : subCommands) {
                if (canUseCommand(sender, c.getRequiredPermissionLevel())) {
                    names.add(c.getName());
                }
            }
            return getListOfStringsMatchingLastWord(args, names);
        }

        // Get subcommand
        AbstractJSGCommand command = null;
        for (AbstractJSGCommand c : subCommands) {
            if (c.getName().equalsIgnoreCase(args[0])) {
                command = c;
                break;
            }
        }
        if (command == null) {
            return list;
        }

        // Check permissions
        if (!canUseCommand(sender, command.getRequiredPermissionLevel())) {
            return list;
        }

        // Remove first argument
        List<String> a = Arrays.asList(args);
        a = a.subList(1, a.size());

        return command.getTabCompletions(server, sender, a.toArray(new String[0]), targetPos);
    }
}
