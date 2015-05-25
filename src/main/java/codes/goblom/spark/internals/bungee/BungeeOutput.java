/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.bungee;

import codes.goblom.spark.Log;
import codes.goblom.spark.internals.Serializable;
import codes.goblom.spark.internals.Spark;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
public class BungeeOutput {
    
    private final ByteArrayOutputStream b = new ByteArrayOutputStream();
    private final DataOutputStream out = new DataOutputStream(b);
    
    public BungeeOutput write(Object obj) {
        try {
            if (obj instanceof String) {
                out.writeUTF((String) obj);
            } else if (obj instanceof Integer) {
                out.writeInt((Integer) obj);
            } else if (obj instanceof Boolean) {
                out.writeBoolean((Boolean) obj);
            } else if (obj instanceof Double) {
                out.writeDouble((Double) obj);
            } else if (obj instanceof Serializable) {
                out.write(((Serializable) obj).serialize());
            } else if (obj instanceof Map) {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(byteOut);
                                   oos.writeObject(obj);
                                   
                out.write(byteOut.toByteArray());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        return this;
    }
    
    public void send(String channel, Player player) {
        try {
            player.sendPluginMessage(Spark.getInstance(), channel, b.toByteArray());
        } catch (Throwable t) {
            Log.warning("Unable to send plugin message on channel %s. Error: %s", channel, t.getMessage());
        }
    }
    
    public void send(String channel, Collection<Player> players) {
        players.stream().forEach((player) -> {
            send(channel, player);
        });
    }
    
    public void send(String channel, World world) {
        try {
            world.sendPluginMessage(Spark.getInstance(), channel, b.toByteArray());
        } catch (Throwable t) { 
            Log.warning("Unable to send plugin message on channel %s. Error: %s", channel, t.getMessage());
        }
    }
}
