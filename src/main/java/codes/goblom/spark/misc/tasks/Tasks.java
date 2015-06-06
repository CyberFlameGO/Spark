/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.tasks;

import codes.goblom.spark.internals.Spark;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * AsyncTask & SyncTask is the preferred method to running tasks. This is here
 * for other purposes
 * 
 * @author Goblom
 */

// TODO: Add scheduled tasks
public class Tasks {
    
    private Tasks() { }
    
    private static final boolean isBukkitRunnable(Runnable r) {
        return r instanceof BukkitRunnable;
    }
    
    private static Plugin getOwning(Object o) {
        try {
            return JavaPlugin.getProvidingPlugin(o.getClass());
        } catch (Exception e) { }
        
        return Spark.getMainInstance();
    }
    
    public static final BukkitTask runLater(boolean async, Runnable run, long later) {
        if (isBukkitRunnable(run)) {
            if (async) {
                return ((BukkitRunnable) run).runTaskLaterAsynchronously(getOwning(run), later);
            }

            return ((BukkitRunnable) run).runTaskLater(getOwning(run), later);
        }

        if (async) {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(getOwning(run), run, later);
        }

        return Bukkit.getScheduler().runTaskLater(getOwning(run), run, later);
    }

    public static final BukkitTask runTimer(boolean async, Runnable run, long delay, long timer) {
        if (isBukkitRunnable(run)) {
            if (async) {
                return ((BukkitRunnable) run).runTaskTimerAsynchronously(getOwning(run), delay, timer);
            }

            return ((BukkitRunnable) run).runTaskTimer(getOwning(run), delay, timer);
        }

        if (async) {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(getOwning(run), run, delay, timer);
        }

        return Bukkit.getScheduler().runTaskTimer(getOwning(run), run, delay, timer);
    }

    public static final BukkitTask run(boolean async, Runnable run) {
        if (isBukkitRunnable(run)) {
            if (async) {
                return ((BukkitRunnable) run).runTaskAsynchronously(getOwning(run));
            }

            return ((BukkitRunnable) run).runTask(getOwning(run));
        }

        if (async) {
            return Bukkit.getScheduler().runTaskAsynchronously(getOwning(run), run);
        }

        return Bukkit.getScheduler().runTask(getOwning(run), run);
    }
}
