/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals;

import codes.goblom.spark.MainSparkConfigs;
import codes.goblom.spark.SparkInstance;
import codes.goblom.spark.configuration.Config;
import codes.goblom.spark.configuration.ConfigType;
import codes.goblom.spark.internals.monitor.Monitor;
import codes.goblom.spark.internals.monitor.Monitors;
import codes.goblom.spark.reflection.safe.SafeClass;
import codes.goblom.spark.reflection.safe.SafeField;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * If you are implementing this class yourself you are doing it wrong
 * 
 * @author Goblom
 */
public interface Spark extends Plugin {
    
    public static final double VERSION = 1.0;
    
    public static boolean isDebug() {
        return MainSparkConfigs.CORE.get("log.debug", false);
    }
    
    public static Spark getMainInstance() {
        StackTraceElement[] element = Thread.currentThread().getStackTrace();
//        for (int i = 0; i < element.length; i++) {
//            System.out.println(String.format("[%s] --> %s", i, element[i].getClassName()));
//        }
        
        Class clazz = new SafeClass(element[1].getClassName()).unsafe2();
        try {
            if (clazz.equals(SparkInstance.class)) {
                return (Spark) JavaPlugin.getPlugin(SparkInstance.class);
            }
        } catch (Exception e) { }
        
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(clazz);
        
        if (plugin == null) {
            throw new UnsupportedOperationException("Calling plugin cannot be null");
        }
        
        if (!(plugin instanceof Spark)) {
            throw new UnsupportedOperationException("Can only get caller plugin if plugin is a SparkPlugin");
        }
        
        Spark spark = (Spark) plugin;
        
        if (spark == null) {
            throw new Error("If you get this then something is definitly screwed up. Please message @DevGoblom on twitter");
        }
        
        return spark;
    }
    
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
        Bukkit.getServer().getPluginManager().registerEvents(listener, JavaPlugin.getProvidingPlugin(listener.getClass()));
    }
    
    public static SimpleCommandMap getCommandMap() {
        SafeField<SimpleCommandMap> f = new SafeField(Bukkit.getServer().getClass(), "commandMap");
                  f.setAccessible(true);
        
        return f.get(Bukkit.getServer());
    }
}
