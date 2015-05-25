/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals;

import codes.goblom.core.GoPlugin;
import codes.goblom.core.configuration.Config;
import codes.goblom.core.configuration.ConfigType;
import codes.goblom.core.internals.monitor.Monitor;
import codes.goblom.core.internals.monitor.Monitors;
import codes.goblom.core.reflection.Reflection;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 *
 * @author Goblom
 */
public interface GoLib {
    
    void reloadConfig();
    
    void saveConfig();
    
    FileConfiguration getConfig();
    
    Config getConfig(String file);
    
    Config getConfig(ConfigType type, String file);
    
    Config getExternalConfig(ConfigType type, File external, String file);
    
//    ClassLoader getClassLoader();
    
    default <M extends Monitor> M getMonitor(Class<M> monitorClass) {
        return Monitors.load(monitorClass);
    }
    
    default <E extends Event> E callEvent(E event) {
        Bukkit.getPluginManager().callEvent(event);
        
        return event;
    }
    
    default void register(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, GoPlugin.getInstance());
    }
}
