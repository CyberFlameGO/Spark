/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.commands;

import codes.goblom.spark.Log;
import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.internals.Callback;
import codes.goblom.spark.internals.ExecutorArgs;
import codes.goblom.spark.internals.Spark;
import codes.goblom.spark.internals.task.AsyncTask;
import codes.goblom.spark.misc.utils.PlayerUtils;
import codes.goblom.spark.misc.utils.Utils;
import codes.goblom.spark.reflection.safe.SafeField;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

/**
 *
 * @author Goblom
 */
public abstract class AbstractSparkCommandWrapper<T extends SparkPlugin> extends Command {

    protected final T plugin;

    public AbstractSparkCommandWrapper(T plugin, String command) {
        super(command);
        this.plugin = plugin;
    }

    public AbstractSparkCommandWrapper(T plugin, String name, String description, String usageMessage) {
        super(name, description, usageMessage, Lists.newArrayList()); // TODO: Support Aliases
        this.plugin = plugin;
    }

    public AbstractSparkCommandWrapper register() {
        SimpleCommandMap commandMap = Spark.getCommandMap();
        SafeField<Map<String, Command>> field = new SafeField(commandMap.getClass(), "knownCommands");
        field.setAccessible(true);
        field.setReadOnly(false);
        Map<String, Command> knownCommands = field.get(commandMap);

        knownCommands.put("bukkit:" + getName(), this);
        knownCommands.put(getName(), this);
        knownCommands.put(plugin.getDescription().getName() + ":" + getName(), this);

        SafeField f = new SafeField(getClass(), "commandMap");
        f.setAccessible(true);
        f.set(this, commandMap);

        return this;
    }

    public abstract Map<String, SparkCommand> getAvailableCommands();

    public void sendHelpMessages(CommandSender sender) { }

    public String cannotExecuteMessage() {
        return "&cInvalid Permissions.";
    }

    @Override
    public final boolean execute(CommandSender sender, String commandLabel, String[] args) {
        final SparkCommand cmd;

        if (args.length >= 1 && ((cmd = getAvailableCommands().get(args[0])) != null)) {
            if (!cmd.canExecute(sender)) {
                String msg = cannotExecuteMessage();

                if (Utils.isValid(msg)) {
                    PlayerUtils.sendMessage(sender, msg);
                }

                return true;
            }

            return DefaultSparkCommand.execute(cmd, sender, args);
        } else {
            sendHelpMessages(sender);
        }

        return true;
    }
}
