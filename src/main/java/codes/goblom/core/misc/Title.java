/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.misc;

import codes.goblom.core.reflection.Reflection;
import codes.goblom.core.reflection.safe.SafeClass;
import codes.goblom.core.reflection.safe.SafeConstructor;
import codes.goblom.core.reflection.safe.SafeField;
import codes.goblom.core.reflection.safe.SafeMethod;
import codes.goblom.core.reflection.safe.SafeObject;
import lombok.experimental.Builder;
import lombok.Data;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
@Builder
@Data
public class Title {
    private static final SafeClass PacketPlayOutTitle = Reflection.getNMSClass("PacketPlayOutTitle");
    private static final SafeClass CraftPlayer = Reflection.getCraftClass("entity.CraftPlayer");
    private static final SafeClass CraftChatMessage = Reflection.getCraftClass("util.CraftChatMessage");
    private static final SafeClass EnumTitleAction = Reflection.getNMSClass("EnumTitleAction");
    private static final SafeClass IChatBaseComponent = Reflection.getNMSClass("IChatBaseComponent");
    private static final SafeConstructor PacketConstructor = PacketPlayOutTitle.getConstructor(EnumTitleAction, IChatBaseComponent);
    private static final SafeMethod fromString = CraftChatMessage.getMethod("fromString", String.class);
    
    private String title;
    private String subTitle;
    private int fadeIn = 20;
    private int fadeOut = 20;
    private int stay = 60;
    
    public void send(Player... players) {
        SafeObject packet = PacketConstructor.newInstance(Enum.valueOf(EnumTitleAction.unsafe2(), "TITLE"), fromString.invoke(null, title).as(String[].class)[0]);
        SafeObject packet2 = PacketConstructor.newInstance(Enum.valueOf(EnumTitleAction.unsafe2(), "SUBTITLE"), fromString.invoke(null, subTitle).as(String[].class)[0]);
        SafeObject packet3 = PacketPlayOutTitle.getConstructor(int.class, int.class, int.class).newInstance(fadeIn, stay, fadeOut);
        
        for (Player player : players) {
            SafeObject handle = CraftPlayer.getMethod("getHandle").invoke(player);
            SafeField playerConnection = handle.getSafeClass().getField("playerConnection");
            SafeObject connection = playerConnection.getSafe(handle);
            SafeMethod sendPacket = connection.getSafeClass().getMethod("sendPacket", Reflection.getNMSClass("Packet").unsafe2());
            
            for (SafeObject p : new SafeObject[] { packet3, packet, packet2}) {
                sendPacket.invoke(connection.unsafe(), p.unsafe());
            }
        }
    }
}
