/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.generation;

import codes.goblom.spark.internals.Spark;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Regeneration v1.3
 *
 * A simple block regeneration class that allows a person to request a location
 * to be regenerated after a certain amount of time.
 *
 * @author Goblom
 */
public class Regeneration {

    private static Regeneration instance = null;
    
    private static Regeneration getInstance() {
        if (instance == null) {
            instance = new Regeneration(Spark.getInstance());
        }
        
        return instance;
    }
    
    private final Plugin plugin;
    private final Map<Location, BlockState> toRegen = Maps.newHashMap();
    private final List<BlockRegen> tasks = Lists.newArrayList();

    /**
     * @see Regeneration
     * @param plugin 
     */
    protected Regeneration(final Plugin plugin) {
        this.plugin = plugin;
        
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPluginDisable(PluginDisableEvent event) {
                if (event.getPlugin().equals(plugin)) {
                    forceTasks();
                }
            }
        }, plugin);
    }

    /**
     * Checks to see if the task list is not empty and need to run tasks
     *
     * @return true if there are tasks that need to run
     */
    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    /**
     * Forces all tasks to run
     */
    public void forceTasks() {
        for (BlockRegen task : Lists.newArrayList(tasks)) {
            // i overwrote the cancel task
            task.cancel();
        }
    }

    /**
     * Checks if the location you want to regen already has a regen task running
     * on it
     *
     * @param location
     * @return true if that location already has a {@link BlockRegen} task
     */
    public boolean alreadyScheduled(Location location) {
        return toRegen.containsKey(location);
    }

    /**
     * Request the location for a {@link BlockRegen} task
     *
     * @param block the block to regen back to
     * @param ticksLater ticks later for regen task to run
     * @return true if task was started, false if a task is {@link Regeneration#alreadyScheduled(org.bukkit.Location)
     * }
     */
    public boolean request(Block block, long ticksLater) {
        if (alreadyScheduled(block.getLocation())) {
            return false;
        }

        this.toRegen.put(block.getLocation(), block.getState());

        BlockRegen regenTask = new BlockRegen(block.getType(), block.getState().getData(), block.getData(), block.getLocation());
        regenTask.runTaskLater(plugin, ticksLater);

        return tasks.add(regenTask);
    }

    /**
     * Does the event task for you, just pass the {@link BlockBreakEvent} to
     * this
     *
     * @param event
     * @param ticksLater
     */
    public void onBlockBreak(BlockBreakEvent event, long ticksLater) {
        request(event.getBlock(), ticksLater);
    }

    /**
     * Does the event task for you, just pass the {@link BlockPlaceEvent} to
     * this
     *
     * @param event
     * @param ticksLater
     */
    public void onBlockPlace(BlockPlaceEvent event, long ticksLater) {
        request(event.getBlockReplacedState().getBlock(), ticksLater);
    }

    /**
     * BlockRegen
     *
     * The Regeneration task that is scheduled whenever a regen is requested
     */
    @RequiredArgsConstructor
    public class BlockRegen extends BukkitRunnable {

        @Getter
        private final Material type;
        @Getter
        private final MaterialData data;
        @Getter
        private final byte moarData;
        
        @Getter
        private final Location location;
        private boolean hasRun = false;

        @Override
        public void cancel() {
            if (!hasRun) {
                run();
            }

            super.cancel();
        }

        @Override
        public void run() {
            location.getBlock().setType(type);
            location.getBlock().getState().setData(data);
            location.getBlock().setData(moarData);

            location.getBlock().getState().update();

            finish();
        }

        public void finish() {
            this.hasRun = true;
            if (toRegen.containsKey(getLocation())) {
                toRegen.remove(getLocation());
            }

            if (tasks.contains(this)) {
                tasks.remove(this);
            }
        }
    }
}
