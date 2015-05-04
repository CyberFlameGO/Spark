/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals.task;

import codes.goblom.core.GoPlugin;
import codes.goblom.core.Log;
import codes.goblom.core.internals.Callback;
import codes.goblom.core.internals.ExecutorNoArgs;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Goblom
 */
public abstract class SyncTask<T> implements ExecutorNoArgs<T, Throwable> {    
    private Callback<T> callback = null;
    private final Caller<T> caller;
    private BukkitTask task;
    
    public SyncTask() {
        this(null);
    }
    
    public SyncTask(Callback<T> callback) {
        this.callback = callback;
        this.caller = new Caller<>(this);
    }
        
    public final void cancel() {
        if (task != null) {
            task.cancel();
        } else {
            Log.warning("Attempted to cancel a task that has not yet started");
        }
    }
        
    public final BukkitTask run() {
        this.task = Bukkit.getScheduler().runTask(GoPlugin.getInstance(), caller);
        return task;
    }
    
    public final BukkitTask runLater(long delay) {
        this.task = Bukkit.getScheduler().runTaskLater(GoPlugin.getInstance(), caller, delay);
        return task;
    }
    
    public final BukkitTask runTimer(long delay, long period) {
        this.task = Bukkit.getScheduler().runTaskTimer(GoPlugin.getInstance(), caller, delay, period);
        return task;
    }

    @RequiredArgsConstructor
    private static final class Caller<T> implements Runnable {
        private final SyncTask<T> runnable;
        
        @Override
        public void run() {
            Throwable thrown = null;
            T obj = null;

            try {
                obj = runnable.execute();
            } catch (Throwable t) {
                thrown = t;
            }

            if (runnable.callback != null) {
                runnable.callback.onFinish(obj, thrown);
            }

            if (thrown != null) {
                thrown.printStackTrace();
            }
        }
        
    }
}
