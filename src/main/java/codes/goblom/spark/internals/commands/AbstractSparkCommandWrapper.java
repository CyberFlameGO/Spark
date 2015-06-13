/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.commands;

import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.internals.Spark;
import codes.goblom.spark.misc.utils.PlayerUtils;
import codes.goblom.spark.misc.utils.Utils;
import codes.goblom.spark.reflection.safe.SafeField;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

/**
 *
 * @author Goblom
 */
public abstract class AbstractSparkCommandWrapper extends Command {
    
    @Getter
    private final Map<String, SparkCommand> availableCommands = Maps.newConcurrentMap();
    
    public AbstractSparkCommandWrapper(String command) {
        super(command);
    }

    public AbstractSparkCommandWrapper(String name, String description, String usageMessage) {
        super(name, description, usageMessage, Lists.newArrayList()); // TODO: Support Aliases
    }

    public final AbstractSparkCommandWrapper registerCommand(SparkCommand cmd) {
        if (Utils.isValid(cmd.getAliases())) {
            for (String alias : cmd.getAliases()) {
                if (!availableCommands.containsKey(alias)) {
                    availableCommands.put(alias, cmd);
                }
            }
        }
        
        return this;
    }
    
    public final AbstractSparkCommandWrapper registerWithBukkit(SparkPlugin plugin) {
        SimpleCommandMap commandMap = Spark.getCommandMap();
        SafeField<Map<String, Command>> field = new SafeField(commandMap.getClass(), "knownCommands");
                                        field.setAccessible(true);
                                        field.setReadOnly(false);
        
        Map<String, Command> knownCommands = field.get(commandMap);
                             knownCommands.put(getName(), this);
                             knownCommands.put(plugin.getDescription().getName() + ":" + getName(), this);

        SafeField f = new SafeField(getClass(), "commandMap");
                  f.setAccessible(true);
                  f.set(this, commandMap);

        return this;
    }

    public void sendHelpMessages(CommandSender sender) { }

    public String[] cannotExecuteMessage(SparkCommand cmd) {
        return new String[] { "&cInvalid Permissions." };
    }

    @Override
    public final boolean execute(CommandSender sender, String commandLabel, String[] args) {
        final SparkCommand cmd;

        if (args.length >= 1 && ((cmd = getAvailableCommands().get(args[0])) != null)) {
            if (!cmd.canExecute(sender)) {
                String[] msg = cannotExecuteMessage(cmd);

                if (Utils.isValid(msg)) {
                    for (String str : msg) {
                        PlayerUtils.sendMessage(sender, str);
                    }
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
