/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark;

import codes.goblom.spark.configuration.Config;
import codes.goblom.spark.configuration.ConfigType;
import codes.goblom.spark.conversation.ConversationApi;
import codes.goblom.spark.conversation.ConversationSequencer;
import codes.goblom.spark.internals.task.AsyncTask;
import codes.goblom.spark.internals.Callback;
import codes.goblom.spark.internals.Spark;
import codes.goblom.spark.internals.commands.DefaultSparkCommand;
import codes.goblom.spark.internals.misc.SparkPluginsCommand;
import codes.goblom.spark.internals.task.SyncTask;
import codes.goblom.spark.internals.monitor.Monitors;
import codes.goblom.spark.misc.tools.BukkitDevUpdater;
import codes.goblom.spark.misc.tools.Metrics;
import codes.goblom.spark.misc.tools.SpigotUpdater;
import codes.goblom.spark.misc.utils.PlayerUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Goblom
 */
public abstract class SparkPlugin extends JavaPlugin implements Spark {
    private final Map<String, Config> configs = Maps.newConcurrentMap();
    
    @Getter
    private static SparkPlugin instance;
    
    public SparkPlugin() {
        super();
        
        SparkPlugin.instance = this;
        
        ImmutableList.of("plugins", "pl").forEach((name) -> { new SparkPluginsCommand(name); });
        ImmutableList.of("spark", "spk", "sk").forEach((name) -> { new DefaultSparkCommand(name); });
    }
    
    public void load() { }
    
    // BEFORE MONITORS ARE LOADED
    // ADD MONITOR POLICIES & CUSTOM MONITORS HERE
    public abstract void enable();
    
    // MONITORS LOADED
    public void enabled() { }
    
    public void disable() { }
    
    @Override // If you override this make sure to call super.onLoad()
    public void onLoad() { load(); }
    
    @Override // If you override this make sure to call super.onEnable()
    public void onEnable() {
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
                    Metrics metrics = new Metrics(SparkPlugin.this);
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
    
    @Override // If you override this make sure to call super.onLoad()
    public void onDisable() { 
//        try {
            disable();
//        } catch (Throwable t) {
//            t.printStackTrace();
//            Log.severe("There was an error while disabling %s. (located above)", getName());
//        } 
            
        Iterator<Entry<Conversable, ConversationSequencer>> it = ConversationApi.iterator();
        
        while (it.hasNext()) {
            it.next().getValue().abort();
        }
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
                            
                            if (PlayerUtils.hasPermission(player, Spark.getInstance().getName() + ".updater.notify")) {
                                player.sendMessage(String.format("%s[%s%s%s] " + ChatColor.GREEN + "%s", ChatColor.DARK_GRAY, ChatColor.AQUA, Spark.getInstance().getName(), ChatColor.DARK_GRAY, "An update is available to download. Download @ " + updater.getLatestFileLink()));
                            }
                        }
                    };
                    
                    new SyncTask<Void>() {
                        @Override
                        public Void execute() throws Throwable {
                            if (Configs.CORE.get("auto-update.notify", true)) {
                                Spark.register(listener);
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
                    
                    return new BukkitDevUpdater(Spark.getInstance(), projectId, Spark.getInstance().getFile(), type, true);
                }
            }.run();
        }
    }
}
