/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.tasks;

import codes.goblom.spark.internals.Callback;
import codes.goblom.spark.internals.task.SyncTask;
import codes.goblom.spark.misc.utils.Utils;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
public abstract class LevelCountdown extends SyncTask<Void> implements Callback<Void>, Iterable<Player> {
    
    private final boolean retainLevel;
    private final Map<UUID, PlayerData> data = Maps.newConcurrentMap();
    private int countdown;
    
    public LevelCountdown(int time, List<Player> players, boolean retainLevelOnComplete) {
        this.retainLevel = retainLevelOnComplete;
        this.countdown = time;
        
        players.stream().forEach((player) -> { data.put(player.getUniqueId(), new PlayerData(player)); });
        
        runTimer(20L / (long) Utils.PERFECT_TPS, 20L / (long) Utils.PERFECT_TPS);
    }
    
    /**
     * Do not call extensively it builds a new iterator each time;
     */
    @Override
    public Iterator<Player> iterator() {
        return Collections2.transform(data.keySet(), (UUID id) -> Bukkit.getPlayer(id)).stream().filter((player) -> (player != null)).iterator();
    }

    @Override
    public final Void execute() throws Throwable {
        if (--countdown <= 0) {
            data.entrySet().stream().forEach((entry) -> {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    if (retainLevel) {
                        player.setLevel(entry.getValue().level);
                        player.setExp(entry.getValue().exp);
                    } else {
                        player.setLevel(0);
                        player.setExp(1.0F);
                    }
                }
            });
            
            onFinish(null, null);
            cancel();
        } else {
            data.entrySet().stream().forEach((entry) -> {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    player.setLevel(countdown);
                }
            });
        }
        
        return null;
    }
    
    private final class PlayerData {
        int level;
        float exp;
        
        PlayerData(Player player) {
            this.level = player.getLevel();
            this.exp = player.getExp();
            
            player.setLevel(0);
            player.setExp(0);
        }
    }
}
