/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.monitor.types;

import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.internals.ExecutorArgs;
import codes.goblom.spark.internals.monitor.Monitor;
import codes.goblom.spark.internals.tools.Placeholders;
import codes.goblom.spark.internals.tools.Placeholders.AbstractPlaceholder;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
public class CooldownMonitor extends Monitor {
    @Monitor.TickInterval private static final long TICK_INTERVAL = 20L;
    
    private Map<UUID, Counter> cooldowns = Maps.newConcurrentMap();
    public boolean mustBeOnline = false;
    
    CooldownMonitor() {
        super();
        
        Placeholders.register(new AbstractPlaceholder("{cooldown:remaining}", false, true) {
            
            @Override
            public Object execute(ExecutorArgs args) throws Throwable {                
                return getRemaining(args.getAs(Player.class));
            }
        });
    }
    
    public void add(Player player, int seconds) {
        if (seconds <= 0) {
            return;
        }
        
        Counter counter = new Counter(seconds);
        
        if (cooldowns.containsKey(player.getUniqueId())) {
            cooldowns.get(player.getUniqueId()).add(counter);
        } else {
            cooldowns.put(player.getUniqueId(), counter);
        }
    }
    
    public boolean has(Player player) {
        return getRemaining(player) > 0;
    }
    
    public int getRemaining(Player player) {
        if (cooldowns.containsKey(player.getUniqueId())) {
            Counter c = cooldowns.get(player.getUniqueId());
            
            return c != null ? c.time : 0;
        }
        
//        cooldowns.put(player.getUniqueId(), new Counter());
        return 0;
    }
    
    @Override
    public void update() {
        cooldowns.entrySet().stream().filter((entry) -> !(this.mustBeOnline && Bukkit.getPlayer(entry.getKey()) == null)).forEach((entry) -> {
            entry.getValue().time--;
        });
    }
    
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Counter {
        int time = 0;
        
        public void add(Counter counter) {
            this.time += counter.time;
        }
    }
}
