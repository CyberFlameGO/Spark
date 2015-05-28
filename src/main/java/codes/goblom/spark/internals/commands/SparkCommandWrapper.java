/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.commands;

import codes.goblom.spark.Log;
import codes.goblom.spark.internals.ExecutorArgs;
import codes.goblom.spark.internals.Spark;
import codes.goblom.spark.misc.utils.Utils;
import codes.goblom.spark.reflection.safe.SafeField;
import java.util.Arrays;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

/**
 *
 * @author Goblom
 */
final class SparkCommandWrapper extends Command {

    private final SparkCommand command;
    
    SparkCommandWrapper(String name, SparkCommand command) {
        super(name);
        this.command = command;
        
        
        if (Utils.isValid(command.getDescription())) {
            updateField("description", command.getDescription());
        }
        
        if (Utils.isValid(command.getUsage())) {
            updateField("usage", command.getUsage());
        }
        
//        if (command.getAliases().length > 1) {
//            updateField("aliases", Arrays.asList(command.getAliases()));
//            updateField("activeAliases", Arrays.asList(command.getAliases()));
//        }
        
        if (Utils.isValid(command.getPermission())) {
            setPermission(command.getPermission());
        }
    }
    
    void register() {
        SimpleCommandMap commandMap = Spark.getCommandMap();
        SafeField<Map<String, Command>> field = new SafeField(commandMap.getClass(), "knownCommands");
                                        field.setAccessible(true);
                                        field.setReadOnly(false);
        Map<String, Command> knownCommands = field.get(commandMap);
            
        knownCommands.put("bukkit:" + getName(), this);
        knownCommands.put(getName(), this);
        knownCommands.put(Spark.getInstance().getDescription().getName() + ":" + getName(), this);
        
        SafeField f = new SafeField(getClass(), "commandMap");
                  f.setAccessible(true);
                  f.set(this, commandMap);
    }
    
    protected void updateField(String name, Object value) {
        SafeField field = new SafeField(getClass(), name);
                  field.setAccessible(true);
                  field.setReadOnly(false);
                  
        field.set(this, value);
    }
    
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        try {
            return command.execute(ExecutorArgs.wrap(sender, args));
        } catch (Throwable t) {
            t.printStackTrace();
            
            if (command.canSendError()) {
                Log.sendErrorMessage(sender, t.getMessage());
            }
        }
        
        return true;
    }
    
    
}
