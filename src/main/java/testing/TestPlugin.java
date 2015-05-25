/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package testing;

import codes.goblom.spark.Configs;
import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.Log;
import codes.goblom.spark.internals.monitor.Monitors;
import codes.goblom.spark.internals.policy.Policies;
import codes.goblom.spark.internals.task.SyncTask;
import codes.goblom.spark.misc.Colorize;
import codes.goblom.spark.scoreboard.FastScoreboard;
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
//        new SyncTask<Void>() {
//
//            @Override
//            public Void execute() throws Throwable {
//                LagMonitor lm = getMonitor(LagMonitor.class);
//                final Runtime r = Runtime.getRuntime();
//                
//                // 1048576L == 1MiB
//                message("&7Current TPS: &a" + lm.getAverage());
//                message("&7Memory usage: &a" + Utils.addCommas((r.totalMemory() - r.freeMemory()) / 1048576L) + "&8/&a" + Utils.addCommas(r.totalMemory() / 1048576L) + " &7of maximum &b" + Utils.addCommas(r.maxMemory() / 1048576L));
//                return null;
//            }
//            
//        }.runTimer(0, 20 * 3);
        
        final FastScoreboard fs = Monitors.loadNoStore(FastScoreboard.class, Policies.newLoadPolicy().constructor(String.class).values("Test Scoreboard"));
                             fs.add("Test Score", 1);
        
        new SyncTask<Void>() {
            @Override
            public Void execute() throws Throwable {
                fs.sendAll();
                
                return null;
            }
        }.runLater(20 * 60);
        
        new SyncTask<Void>() {
            int i = 0;
            
            @Override
            public Void execute() throws Throwable {
                fs.setTitle(String.valueOf(i++));
                fs.add(Colorize.style("Test Score").toGarbage(), 1);
                
                return null;
            }
        }.runTimer(0, 20);
    }
    
    public static void message(String str) {
        str = ChatColor.translateAlternateColorCodes('&', str);
        
        Bukkit.broadcastMessage(str);
    }
}
