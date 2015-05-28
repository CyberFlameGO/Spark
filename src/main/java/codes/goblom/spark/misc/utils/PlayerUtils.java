/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.utils;

import codes.goblom.spark.reflection.safe.SafeClass;
import codes.goblom.spark.reflection.safe.SafeField;
import codes.goblom.spark.reflection.safe.SafeObject;
import codes.goblom.spark.reflection.Reflection;
import codes.goblom.spark.reflection.safe.SafeConstructor;
import codes.goblom.spark.reflection.safe.SafeMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

/**
 *
 * @author Goblom
 */
public class PlayerUtils {
    
    private PlayerUtils() { }
    
    public static boolean hasPermission(Permissible sender, String permission) {
        return sender.isOp() || sender.hasPermission(permission);
    }
    
    public static Player matchPlayer(String toMatch) {
        Player player = Bukkit.getPlayer(toMatch);
        
        if (player == null) {
            String lower = toMatch.toLowerCase();
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getName().toLowerCase().startsWith(lower)) {
                    player = online;
                    break;
                }
            }
        }
        
        return player;
    }
    
    public static int getPing(Player player) {
        SafeClass CraftPlayer = Reflection.getCraftClass("entity.CraftPlayer");
        SafeObject handle = CraftPlayer.getMethod("getHandle").invoke(player);
        SafeClass EntityPlayer = handle.getSafeClass();
        SafeField<Integer> ping = EntityPlayer.getField("ping");
        
        return ping.get(handle);
    }
    
    public static void forceRespawn(Player player) {
        SafeClass CraftPlayer = Reflection.getCraftClass("entity.CraftPlayer");
        SafeClass PacketPlayInClientCommand = Reflection.getNMSClass("PacketPlayInClientCommand");
        SafeClass EnumClientCommand = Reflection.getNMSClass("EnumClientCommand");
        
        SafeMethod handle = CraftPlayer.getMethod("getHandle");
        
        SafeObject entityPlayer = handle.invoke(player);
        SafeConstructor construct = PacketPlayInClientCommand.getConstructor(EnumClientCommand);
        Enum<?> PERFORM_RESPAWN = Enum.valueOf(EnumClientCommand.unsafe2(), "PERFORM_RESPAWN");
        
        SafeObject packet = construct.newInstance(PERFORM_RESPAWN);
        SafeObject playerConnection = entityPlayer.getSafeClass().getField("playerConnection").getSafe(entityPlayer);
        
        SafeMethod send = playerConnection.getSafeClass().getMethod("a", PacketPlayInClientCommand.unsafe());
                   send.invoke(playerConnection.unsafe(), packet.unsafe());
    }
    
    public static void setMaxPlayers(int max) {
        SafeClass CraftServer = Reflection.getCraftClass("CraftServer");
        SafeMethod getHandle = CraftServer.getMethod("getHandle");
        SafeObject handle = getHandle.invoke(Bukkit.getServer());
        
        SafeField maxPlayers = handle.getSafeClass().getField("maxPlayers");
                  maxPlayers.setAccessible(true);
                  maxPlayers.set(handle, max);
    }
}
