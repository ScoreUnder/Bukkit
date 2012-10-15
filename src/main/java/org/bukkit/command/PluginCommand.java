package org.bukkit.command;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Represents a {@link Command} belonging to a plugin
 */
public final class PluginCommand extends Command implements PluginIdentifiableCommand {
    private final Plugin owningPlugin;
    private CommandExecutor executor;
    private TabCompleter completer;

    protected PluginCommand(String name, Plugin owner) {
        super(name);
        this.executor = owner;
        this.owningPlugin = owner;
        this.usageMessage = "";
    }

    /**
     * Executes the command, returning its success
     *
     * @param sender Source object which is executing this command
     * @param commandLabel The alias of the command used
     * @param args All arguments passed to the command, split via ' '
     * @return true if the command was successful, otherwise false
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        boolean success = false;

        if (!owningPlugin.isEnabled()) {
            return false;
        }

        if (!testPermission(sender)) {
            return true;
        }

        try {
            success = executor.onCommand(sender, this, commandLabel, args);
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + owningPlugin.getDescription().getFullName(), ex);
        }

        if (!success && usageMessage.length() > 0) {
            for (String line : usageMessage.replace("<command>", commandLabel).split("\n")) {
                sender.sendMessage(line);
            }
        }

        return success;
    }

    /**
     * Sets the {@link CommandExecutor} to run when parsing this command
     *
     * @param executor New executor to run
     */
    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    /**
     * Gets the {@link CommandExecutor} associated with this command
     *
     * @return CommandExecutor object linked to this command
     */
    public CommandExecutor getExecutor() {
        return executor;
    }

    /**
     * Sets the {@link TabCompleter} to run when tab-completing this command.
     * If no TabCompleter is specified, and the command's executor implements
     * TabCompleter, then the executor will be used for tab completion.
     *
     * @param completer New tab completer
     */
    public void setTabCompleter(TabCompleter completer) {
        this.completer = completer;
    }

    /**
     * Gets the {@link TabCompleter} associated with this command.
     *
     * @return TabCompleter object linked to this command
     */
    public TabCompleter getTabCompleter() {
        return completer;
    }

    /**
     * Gets the owner of this PluginCommand
     *
     * @return Plugin that owns this command
     */
    public Plugin getPlugin() {
        return owningPlugin;
    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * Delegates to the tab completer if present.<br>
     * If it is not present or returns null, will delegate to the current command
     * executor if it implements {@link TabCompleter}. If a non-null list has not
     * been found, will default to standard player name completion in
     * {@link Command#tabComplete(CommandSender, String[])}.<br>
     * <br>
     * This method does not consider permissions.
     * @throws CommandException if the completer or executor throw an exception during the process of tab-completing.
     * @throws IllegalArgumentException if sender is null
     * @throws IllegalArgumentException if args is null
     */
    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) throws CommandException, IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");

        List<String> completions = null;
        try {
            if (completer != null) {
                completions = completer.onTabComplete(sender, this, args);
            }
            if (completions == null && executor instanceof TabCompleter) {
                completions = ((TabCompleter) executor).onTabComplete(sender, this, args);
            }
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception during tab completion for command '" + args[0] + "' in plugin " + owningPlugin.getDescription().getFullName(), ex);
        }

        if (completions == null) {
            return super.tabComplete(sender, args);
        }
        return completions;
    }
}
