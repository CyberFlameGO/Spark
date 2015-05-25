/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.tasks;

import codes.goblom.spark.internals.Spark;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @deprecated Use AsyncTask & SyncTask
 * @author Goblom
 */

// TODO: Add scheduled tasks
@Deprecated
public class Tasks {
    
    private Tasks() { }
    
    private static final boolean isBukkitRunnable(Runnable r) {
        return r instanceof BukkitRunnable;
    }
        
    public static final BukkitTask runLater(boolean async, Runnable run, long later) {
        if (isBukkitRunnable(run)) {
            if (async) {
                return ((BukkitRunnable) run).runTaskLaterAsynchronously(Spark.getInstance(), later);
            }

            return ((BukkitRunnable) run).runTaskLater(Spark.getInstance(), later);
        }

        if (async) {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(Spark.getInstance(), run, later);
        }

        return Bukkit.getScheduler().runTaskLater(Spark.getInstance(), run, later);
    }

    public static final BukkitTask runTimer(boolean async, Runnable run, long delay, long timer) {
        if (isBukkitRunnable(run)) {
            if (async) {
                return ((BukkitRunnable) run).runTaskTimerAsynchronously(Spark.getInstance(), delay, timer);
            }

            return ((BukkitRunnable) run).runTaskTimer(Spark.getInstance(), delay, timer);
        }

        if (async) {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(Spark.getInstance(), run, delay, timer);
        }

        return Bukkit.getScheduler().runTaskTimer(Spark.getInstance(), run, delay, timer);
    }

    public static final BukkitTask run(boolean async, Runnable run) {
        if (isBukkitRunnable(run)) {
            if (async) {
                return ((BukkitRunnable) run).runTaskAsynchronously(Spark.getInstance());
            }

            return ((BukkitRunnable) run).runTask(Spark.getInstance());
        }

        if (async) {
            return Bukkit.getScheduler().runTaskAsynchronously(Spark.getInstance(), run);
        }

        return Bukkit.getScheduler().runTask(Spark.getInstance(), run);
    }
}
