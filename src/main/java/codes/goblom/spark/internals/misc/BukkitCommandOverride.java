/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.misc;

import codes.goblom.spark.internals.Spark;
import codes.goblom.spark.reflection.safe.SafeField;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;

/**
 *
 * @author Goblom
 */
abstract class BukkitCommandOverride extends BukkitCommand {
    
    public BukkitCommandOverride(String name) {
        super(name);
    }
    
    public BukkitCommandOverride(String name, String description, String usage, List<String> alias) {
        super(name, description, usage, alias);
    }
    
    protected final void register() {
        SimpleCommandMap commandMap = Spark.getCommandMap();
        SafeField<Map<String, Command>> field = new SafeField(commandMap.getClass(), "knownCommands");
                                        field.setAccessible(true);
                                        field.setReadOnly(false);
        Map<String, Command> knownCommands = field.get(commandMap);
            
        knownCommands.put("bukkit:" + getName(), this);
        knownCommands.put(getName(), this);
        knownCommands.put("spark:" + getName(), this);
        
        SafeField f = new SafeField(getClass(), "commandMap");
                  f.setAccessible(true);
                  f.set(this, commandMap);
    }
}
