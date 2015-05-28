/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package testing;

import codes.goblom.spark.Configs;
import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.Log;
import codes.goblom.spark.internals.commands.SparkCommand;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author Goblom
 */
public class TestPlugin extends SparkPlugin {

    @Override
    public void enable() {
        Log.info("We are Alive!");
        
        Configs.TEST_JSON.set("list", Arrays.asList("entry 1", "entry 2", "entry 3"));
        Configs.TEST_JSON.set("integer", 1);
        Configs.TEST_JSON.set("string", "test string");
        Configs.TEST_JSON.deeper("inner").set("test", true);
        Configs.TEST_JSON.save();
    }
    
    @Override
    public void enabled() {
        if (!SparkCommand.registerCommand(new TestCommand())) {
            Log.info("Unable to register TestCommand");
        } else {
            Log.info("Registered TestCommand");
        }
    }
    
    public static void message(String str) {
        str = ChatColor.translateAlternateColorCodes('&', str);
        
        Bukkit.broadcastMessage(str);
    }
}
