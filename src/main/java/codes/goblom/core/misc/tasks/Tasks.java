/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.misc.tasks;

import codes.goblom.core.GoPlugin;
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
    
    private static final boolean isBukkitRunnable(Runnable r) {
        return r instanceof BukkitRunnable;
    }
        
    public static final BukkitTask runLater(boolean async, Runnable run, long later) {
        if (isBukkitRunnable(run)) {
            if (async) {
                return ((BukkitRunnable) run).runTaskLaterAsynchronously(GoPlugin.getInstance(), later);
            }

            return ((BukkitRunnable) run).runTaskLater(GoPlugin.getInstance(), later);
        }

        if (async) {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(GoPlugin.getInstance(), run, later);
        }

        return Bukkit.getScheduler().runTaskLater(GoPlugin.getInstance(), run, later);
    }

    public static final BukkitTask runTimer(boolean async, Runnable run, long delay, long timer) {
        if (isBukkitRunnable(run)) {
            if (async) {
                return ((BukkitRunnable) run).runTaskTimerAsynchronously(GoPlugin.getInstance(), delay, timer);
            }

            return ((BukkitRunnable) run).runTaskTimer(GoPlugin.getInstance(), delay, timer);
        }

        if (async) {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(GoPlugin.getInstance(), run, delay, timer);
        }

        return Bukkit.getScheduler().runTaskTimer(GoPlugin.getInstance(), run, delay, timer);
    }

    public static final BukkitTask run(boolean async, Runnable run) {
        if (isBukkitRunnable(run)) {
            if (async) {
                return ((BukkitRunnable) run).runTaskAsynchronously(GoPlugin.getInstance());
            }

            return ((BukkitRunnable) run).runTask(GoPlugin.getInstance());
        }

        if (async) {
            return Bukkit.getScheduler().runTaskAsynchronously(GoPlugin.getInstance(), run);
        }

        return Bukkit.getScheduler().runTask(GoPlugin.getInstance(), run);
    }
}
