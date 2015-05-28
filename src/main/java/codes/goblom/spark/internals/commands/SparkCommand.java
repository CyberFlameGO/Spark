/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.commands;

import codes.goblom.spark.Log;
import codes.goblom.spark.internals.Executor;
import codes.goblom.spark.internals.ExecutorArgs;
import static codes.goblom.spark.internals.commands.DefaultSparkCommand.REGISTERED_COMMANDS;
import codes.goblom.spark.misc.utils.PlayerUtils;
import codes.goblom.spark.misc.utils.Utils;
import java.util.Collections;
import java.util.Map;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Goblom
 */
public interface SparkCommand extends Executor<Boolean, Throwable> {

    public static Map<String, SparkCommand> getCommands() {
        return Collections.unmodifiableMap(REGISTERED_COMMANDS);
    }
    
    public static SparkCommand getCommand(String arg) {
        if (REGISTERED_COMMANDS.containsKey(arg)) {
            return REGISTERED_COMMANDS.get(arg);
        }
        
        for (String alias : REGISTERED_COMMANDS.keySet()) {
            if (alias.equalsIgnoreCase(arg)) {
                return REGISTERED_COMMANDS.get(alias);
            }
        }
        
        return null;
    }
    
    public static boolean registerCommand(SparkCommand command) {
        if (!Utils.isValid(command.getAliases())) {
            return false;
        }
        
        if (command.isMainCommand()) {
            return DefaultSparkCommand.registerMain(command);
        }
        
        boolean registered = false;
        for (String alias : command.getAliases()) {
            
            if (Utils.isValid(alias) && getCommand(alias) == null) {
                REGISTERED_COMMANDS.put(alias, command);
                Log.debug("Registered SparkCommand [%s]", alias);
                
                registered = true;
            }
        }
        
        return registered;
    }
    
    String[] getAliases();
    
    String getDescription();
    
    default boolean canExecute(CommandSender sender) {
        if (Utils.isValid(getPermission())) {
            return PlayerUtils.hasPermission(sender, getPermission());
        }
        
        return true;
    }
    
    default String getPermission() {
        return "";
    }
        
    default String getUsage() {
        return "";
    }
    
    default boolean runAsync() {
        return false;
    }
    
    default boolean isMainCommand() {
        return false;
    }
    
    default boolean canSendError() {
        return true;
    }
    
    default CommandSender getSender(ExecutorArgs args) {
        return args.getAs(0);
    }
    
    default String[] getArgs(ExecutorArgs args) {
        return args.getAs(1);
    }
    
    default String getArgAt(ExecutorArgs args, int i) {
        return getArgs(args)[i];
    }
}
