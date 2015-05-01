/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package testing;

import codes.goblom.core.Configs;
import codes.goblom.core.GoPlugin;
import codes.goblom.core.Log;
import codes.goblom.core.internals.monitor.types.LagMonitor;
import codes.goblom.core.internals.task.SyncTask;
import codes.goblom.core.misc.utils.Utils;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author Goblom
 */
public class TestPlugin extends GoPlugin {

    @Override
    public void enable() {
        Log.info("We are Alive!");
        
        Configs.TEST_JSON.set("list", Arrays.asList("entry 1", "entry 2", "entry 3"));
        Configs.TEST_JSON.set("integer", 1);
        Configs.TEST_JSON.set("string", "test string");
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
    }
    
    public static void message(String str) {
        str = ChatColor.translateAlternateColorCodes('&', str);
        
        Bukkit.broadcastMessage(str);
    }
}
