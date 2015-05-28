/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.commands;

import codes.goblom.spark.Log;
import codes.goblom.spark.internals.Callback;
import codes.goblom.spark.internals.ExecutorArgs;
import codes.goblom.spark.internals.Spark;
import codes.goblom.spark.internals.task.AsyncTask;
import codes.goblom.spark.misc.utils.Utils;
import codes.goblom.spark.reflection.safe.SafeField;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

/**
 *
 * @author Goblom
 */
public final class DefaultSparkCommand extends Command {

    protected final static Map<String, SparkCommand> REGISTERED_COMMANDS = Maps.newConcurrentMap();
    
    protected static boolean registerMain(SparkCommand command) {
        System.out.println(String.format("Attempting to register %s as main", command.getAliases()[0]));
        if (!command.isMainCommand() || !Utils.isValid(command.getAliases())) {
            return false;
        }
        
        for (String alias : command.getAliases()) {
            SparkCommandWrapper wrapper = new SparkCommandWrapper(alias, command);
                                wrapper.register();
                 
//            REGISTERED_COMMANDS.put(alias, command);
        }
        
        return true;
    }
    
    public DefaultSparkCommand(String name) {
        super(name);
        
        this.description = "Spark Commands";
        this.usageMessage = String.format("/%s [command] {usage}", name);
        
        register();
    }
    
    protected void register() {
        SimpleCommandMap commandMap = Spark.getCommandMap();
        SafeField<Map<String, Command>> field = new SafeField(commandMap.getClass(), "knownCommands");
                                        field.setAccessible(true);
                                        field.setReadOnly(false);
        Map<String, Command> knownCommands = field.get(commandMap);
            
        knownCommands.put(getName(), this);
        knownCommands.put("spark:" + getName(), this);
        
        SafeField f = new SafeField(getClass(), "commandMap");
                  f.setAccessible(true);
                  f.set(this, commandMap);
    }
    
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        final SparkCommand cmd;
        
        if (args.length >= 1 && (cmd = SparkCommand.getCommand(args[0])) != null) {
            if (!cmd.canExecute(sender)) {
                Log.sendErrorMessage(sender, "Command not found");
                    
                return true;
            }
            
            if (args.length == 2) {
                switch (args[1].toLowerCase()) {
                    case "help":
                    case "usage":
                        if (Utils.isValid(cmd.getUsage())) {
                            sender.sendMessage(String.format("Usage: %s", cmd.getUsage()));
                        } else {
                            Log.sendErrorMessage(sender, "There is no usage found for this command.");
                        }
                    
                        return true;
                    case "desc":
                    case "description":
                        if (Utils.isValid(cmd.getDescription())) {
                            sender.sendMessage(String.format("Description: %s", cmd.getDescription()));
                        } else {
                            Log.sendErrorMessage(sender, "There is no description found for this command.");
                        }
                    
                        return true;
                }
            }
            
            if (!cmd.runAsync()) {
                try {
                    return executeAndReturn(cmd, ExecutorArgs.wrap(sender, prepare(1, args)));
                } catch (Throwable t) {
                    if (cmd.canSendError()) {
                        Log.sendErrorMessage(sender, t.getMessage());
                        t.printStackTrace();
                    }
                }
            } else {
                new AsyncTask<Boolean>(new Callback<Boolean>() {

                    @Override
                    public void onFinish(Boolean object, Throwable error) {
                        if (error != null && cmd.canSendError()) {
                            Log.sendErrorMessage(sender, error.getMessage());
                            error.printStackTrace();
                        }
                    }
                }) {

                    @Override
                    public Boolean execute() throws Throwable {
                        return executeAndReturn(cmd, ExecutorArgs.wrap(sender, prepare(1, args)));
                    }
                    
                }.run();
            }
        } else {
            //TODO: Finish help pages
            Log.sendErrorMessage(sender, "No Help pages yet.");
        }
        
        return true;
    }
    
    public boolean executeAndReturn(SparkCommand cmd, ExecutorArgs args) throws Throwable {
        return cmd.execute(args);
    }
    
    String[] prepare(int startAt, String[] args) {
        if (args.length == 1) {
            return new String[0];
        }
        
        if (args.length >= startAt) {
            List<String> list = Lists.newArrayList();
            
            for (int i = startAt; i < args.length; i++) {
                list.add(args[i]);
            }
            
            return list.toArray(new String[list.size()]);
        }
        
        return args;
    }
}
