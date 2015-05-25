/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.reflection;

import codes.goblom.core.GoPlugin;
import codes.goblom.core.Log;
import codes.goblom.core.reflection.exceptions.InvalidPacketException;
import codes.goblom.core.reflection.exceptions.PacketNotFoundException;
import java.lang.reflect.Field;
import java.util.Collection;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */

// TODO: Update to use ServerVersion
// TODO: Support constructors
public class PacketWriter {    
    
    private final Class<?> packetClass;
    private final Object packetObject;
    
    public PacketWriter(String packet) throws PacketNotFoundException, InvalidPacketException {        
        this.packetClass = getNMSClass(packet);
        
        if (this.packetClass == null) {
            throw new PacketNotFoundException(packet + " was unable to be found.");
        }
        
        Object obj = null;
        try {
            obj = packetClass.newInstance();
        } catch (Exception e) { }
        
        if (obj == null) {
            throw new InvalidPacketException("Packet was unable to be initialized");
        }
        
        this.packetObject = obj;
    }
    
    private static Class<?> getNMSClass(String name) {
        return Reflection.getNMSClass(name).unsafe();
    }
    
    private static Field getField(Class<?> clazz, String name) {
        Field field = null;
        
        try {
            field = clazz.getField(name);
        } catch (Exception e) { }
        
        if (field == null) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (Exception e) { }
        }
        
        if (field != null) {
            field.setAccessible(true);
        }
        
        return field;
    }
    
    public PacketWriter write(String field, Object value) {
        Field f = getField(packetClass, field);

        if (f == null) {
            Log.warning("Field %s was not found for %s", field, packetClass.getSimpleName());
            return this;
        }

        if (!f.getType().equals(value.getClass())) {
            Log.warning("Field %s uses a different object value", field);
            return this;
        }
        
        try {
            f.set(packetObject, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field " + field + " is not accessible.", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        
        return this;
    }
    
    public void send(Collection<Player> players) {
        players.stream().forEach((player) -> {
            send(player);
        });
    }
    
    public void send(Player player) {
        Class<?> packetClass = getNMSClass("Packet");
        if (packetClass != null) {
            try {
                Object handle = player.getClass().getMethod("getHandle").invoke(player);
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

                playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, packetObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

