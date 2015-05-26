/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals;

import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.configuration.Config;
import codes.goblom.spark.configuration.ConfigType;
import codes.goblom.spark.internals.monitor.Monitor;
import codes.goblom.spark.internals.monitor.Monitors;
import codes.goblom.spark.reflection.safe.SafeField;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 * If you are implementing this class yourself you are doing it wrong
 * 
 * @author Goblom
 */
public interface Spark {
    
    public static <T extends SparkPlugin> T getInstance() {
        return (T) SparkPlugin.getInstance();
    }
    
    void reloadConfig();
    
    void saveConfig();
    
    FileConfiguration getConfig();
    
    Config getConfig(String file);
    
    Config getConfig(ConfigType type, String file);
    
    Config getExternalConfig(ConfigType type, File external, String file);
    
//    ClassLoader getClassLoader();
    
    public static <M extends Monitor> M getMonitor(Class<M> monitorClass) {
        return Monitors.load(monitorClass);
    }
    
    public static <E extends Event> E callEvent(E event) {
        Bukkit.getPluginManager().callEvent(event);
        
        return event;
    }
    
    public static void register(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, Spark.getInstance());
    }
    
    public static SimpleCommandMap getCommandMap() {
        SafeField<SimpleCommandMap> f = new SafeField(Bukkit.getServer().getClass(), "commandMap");
                  f.setAccessible(true);
        
        return f.get(Bukkit.getServer());
    }
}
