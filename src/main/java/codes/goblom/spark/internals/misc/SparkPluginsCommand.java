/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.misc;

import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.internals.Spark;
import codes.goblom.spark.reflection.safe.SafeField;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Goblom
 */
public class SparkPluginsCommand extends BukkitCommand {

    public SparkPluginsCommand(String name) {
        super(name);
        this.description = "Gets a list of plugins running on the server";
        this.usageMessage = "/plugins";
        this.setPermission("bukkit.command.plugins");
        
        register(name);
    }
    
    void register(String name) {
        SimpleCommandMap commandMap = Spark.getCommandMap();
        SafeField<Map<String, Command>> field = new SafeField(commandMap.getClass(), "knownCommands");
                                        field.setAccessible(true);
                                        field.setReadOnly(false);
        Map<String, Command> knownCommands = field.get(commandMap);
            
        knownCommands.put("bukkit:" + name, this);
        knownCommands.put(name, this);
        knownCommands.put("spark:" + name, this);
        
        SafeField f = new SafeField(getClass(), "commandMap");
                  f.setAccessible(true);
                  f.set(this, commandMap);
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!testPermission(sender)) return true;
        
        PluginListData data = getPluginListData();
        String spark = data.genSparkList();
        
        sender.sendMessage(data.pluginList);
        
        if (!spark.isEmpty()) {
            sender.sendMessage(spark);
        }
        
        return true;
    }
    
    private PluginListData getPluginListData() {
        PluginListData data = new PluginListData();
        StringBuilder pluginList = new StringBuilder();
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

        for (Plugin plugin : plugins) {
            if (plugin instanceof SparkPlugin) {
                data.sparkPlugins.add((SparkPlugin) plugin);
                continue;
            }
            
            if (pluginList.length() > 0) {
                pluginList.append(ChatColor.WHITE);
                pluginList.append(", ");
            }

            pluginList.append(plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
            pluginList.append(plugin.getDescription().getName());
        }

        data.pluginList = "Plugins (" + plugins.length + "): " + pluginList.toString();
        
        return data;
    }
    
    private class PluginListData {
        String pluginList;
        List<SparkPlugin> sparkPlugins = Lists.newArrayList();
        
        String genSparkList() {
            if (sparkPlugins.isEmpty()) {
                return "";
            }
            
            StringBuilder sb = new StringBuilder();
            
            sparkPlugins.stream().map((plugin) -> {
                if (sb.length() > 0) {
                    sb.append(ChatColor.WHITE);
                    sb.append(", ");
                }
                
                sb.append(plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
                return plugin;
            }).forEach((plugin) -> {
                sb.append(plugin.getDescription().getName());
            });
            
            return String.format("Powered by Spark (%s): %s", sparkPlugins.size(), sb.toString());
        }
    }
}
