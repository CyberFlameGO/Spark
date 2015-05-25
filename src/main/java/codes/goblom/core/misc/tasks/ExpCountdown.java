/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.misc.tasks;

import codes.goblom.core.internals.task.SyncTask;
import codes.goblom.core.internals.Callback;
import codes.goblom.core.misc.utils.Utils;
import com.google.common.collect.Collections2;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
public abstract class ExpCountdown extends SyncTask<Void> implements Callback<Void>, Iterable<Player> {
    
    private final double add;
    protected final Collection<UUID> players;
    protected float exp = 0.0F;
    private int countdown;
    
    public ExpCountdown(float time, List<Player> players) {
        super();
        
        this.players = Collections2.transform(players, (player) -> player.getUniqueId());
        
        this.add = 1.000000d / time / Utils.PERFECT_TPS;
        this.countdown = (int) (time * Utils.PERFECT_TPS);
        
        players.stream().forEach((player) -> { player.setExp(0); });
        
        runTimer(20L / (long) Utils.PERFECT_TPS, 20L / (long) Utils.PERFECT_TPS);
    }
    
    public ExpCountdown(float time, Player... players) {
        this(time, Arrays.asList(players));
    }
    
    @Override
    public final Void execute() {
        if (--countdown <= 0) {
//            Collection<UUID> online = Collections2.filter(players, (UUID input) -> Bukkit.getPlayer(input) != null);
            
            players.stream().map((uuid) -> Bukkit.getPlayer(uuid)).filter((player) -> (player != null)).forEach((player) -> {
                player.setExp(1.0F);
            });
            
            onFinish(null, null);
            cancel();
        } else {
            players.stream().map((uuid) -> Bukkit.getPlayer(uuid)).filter((player) -> (player != null)).map((player) -> {
                this.exp += add;
                return player;
            }).forEach((player) -> {
                player.setExp(player.getExp() + (float) add);
            });
        }
        
        return null;
    }
    
    /**
     * Do not call extensively it builds a new iterator each time;
     */
    @Override
    public Iterator<Player> iterator() {
        return Collections2.transform(players, (UUID id) -> Bukkit.getPlayer(id)).stream().filter((player) -> (player != null)).iterator();
    }
}
