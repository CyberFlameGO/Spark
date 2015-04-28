/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core;

import codes.goblom.core.configuration.Config;
import codes.goblom.core.configuration.ConfigType;
import codes.goblom.core.internals.task.AsyncTask;
import codes.goblom.core.internals.Callback;
import codes.goblom.core.internals.GoLib;
import codes.goblom.core.internals.task.SyncTask;
import codes.goblom.core.internals.monitor.Monitors;
import codes.goblom.core.misc.tools.BukkitDevUpdater;
import codes.goblom.core.misc.tools.Metrics;
import codes.goblom.core.misc.tools.Metrics.Plotter;
import codes.goblom.core.misc.tools.SpigotUpdater;
import codes.goblom.core.misc.utils.PlayerUtils;
import codes.goblom.core.reflection.Reflection;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Goblom
 */
public abstract class GoPlugin extends JavaPlugin implements GoLib {
    private final Map<String, Config> configs = Maps.newConcurrentMap();
    
    @Getter
    private static GoPlugin instance;
    
    @Getter
    private final Reflection serverVersion;
    
    public GoPlugin() {
        super();
        
        GoPlugin.instance = this;
        this.serverVersion = new Reflection();
    }
    
    public void load() { }
    
    // BEFORE MONITORS ARE LOADED
    // ADD MONITOR POLICIES & CUSTOM MONITORS HERE
    public abstract void enable();
    
    // MONITORS LOADED
    public void enabled() { }
    
    public void disable() { }
    
    @Override
    public final void onLoad() { load(); }
    
    @Override
    public final void onEnable() {
        Config core = Configs.CORE;

        // Load core config information
        if (!core.contains("enable-metrics")) core.set("enable-metrics", true, true);
        if (!core.contains("auto-update")) {
            core.set("auto-update.check", true, true);
            core.set("auto-update.download", true, true);
            core.set("auto-update.notify", true, true);
        }
        
        // Finished core config loading
        
        if (core.get("enable-metrics", true)) {
            new AsyncTask<Metrics>((Metrics object, Throwable error) -> {
                if (error == null) {
                    Log.info("Metrics started successfully.");
                } else {
                    Log.warning("Metrics was unable to start. Error: %s", error.getMessage());
                }
            }) {
                @Override
                public Metrics execute() throws Throwable {
                    Metrics metrics = new Metrics(GoPlugin.this);
                            metrics.start();
                            
                    return metrics;
                }
                
            }.run();
        }
        
//        try {
            enable(); 
//        } catch (Throwable t) {
//            t.printStackTrace();
//            Log.severe("%s was unable to start. There was an error. (located above)", getName());
//        }
        
        Monitors.loadAll();
        enabled();
    }
    
    @Override
    public final void onDisable() { 
//        try {
            disable();
//        } catch (Throwable t) {
//            t.printStackTrace();
//            Log.severe("There was an error while disabling %s. (located above)", getName());
//        } 
    }
    
    @Override
    public final void reloadConfig() {
        Configs.DEFAULT.reload();
    }
    
    @Override
    public final void saveConfig() {
        Configs.DEFAULT.save();
    }
    
    @Override
    public final FileConfiguration getConfig() {
        return Configs.DEFAULT.getConfiguration();
    }
    
    @Override
    public Config getConfig(String file) {
        return getConfig(ConfigType.YAML, file);
    }
    
    @Override
    public final Config getConfig(ConfigType type, String file) {
        if (configs.containsKey(file)) {
            return configs.get(file);
        }
        
        Config config = type.load(this, file);
        configs.put(file, config);
        
        return config;
    }
    
    @Override
    public final Config getExternalConfig(ConfigType type, File external, String file) {
        String fRep = external.getName() + "/" + file;
        
        if (configs.containsKey(fRep)) {
            return configs.get(fRep);
        }
        
        Config config = type.load(this, external, file);
        configs.put(fRep, config);
        
        return config;
    }
    
    /**
     * @deprecated Very silly. Please instantiate SpigotUpdater yourself
     */
    @Deprecated
    protected final void runSpigotUpdater(int projectId, Callback<String> callback) {
        if (Configs.CORE.get("auto-update.check", true)) {
            SpigotUpdater.check(projectId, callback);
        }
    }
    
    protected final void runBukkitDevUpdater(int projectId) {
        if (Configs.CORE.get("auto-update.check", true)) {
            new AsyncTask<BukkitDevUpdater>((final BukkitDevUpdater updater, Throwable error) -> {
                if (error != null) {
                    final Listener listener = new Listener() {
                        @EventHandler
                        public void onPlayerJoin(PlayerJoinEvent event) {
                            Player player = event.getPlayer();
                            
                            if (PlayerUtils.hasPermission(player, getInstance().getName() + ".updater.notify")) {
                                player.sendMessage(String.format("%s[%s%s%s] " + ChatColor.GREEN + "%s", ChatColor.DARK_GRAY, ChatColor.AQUA, getInstance().getName(), ChatColor.DARK_GRAY, "An update is available to download. Download @ " + updater.getLatestFileLink()));
                            }
                        }
                    };
                    
                    new SyncTask<Void>() {
                        @Override
                        public Void execute() throws Throwable {
                            if (Configs.CORE.get("auto-update.notify", true)) {
                                register(listener);
                            }
                            
                            return null;
                        }
                    }.run();
                } else {
                    Log.warning("Unable to load bukkit plugin updater. :(");
                }
            }) {
                @Override
                public BukkitDevUpdater execute() throws Throwable {
                    boolean download = Configs.CORE.get("auto-update.download", true);
                    BukkitDevUpdater.UpdateType type = BukkitDevUpdater.UpdateType.NO_DOWNLOAD;
                    
                    if (download) {
                        type = BukkitDevUpdater.UpdateType.DEFAULT;
                    }
                    
                    return new BukkitDevUpdater(getInstance(), projectId, getInstance().getFile(), type, true);
                }
            }.run();
        }
    }
}
