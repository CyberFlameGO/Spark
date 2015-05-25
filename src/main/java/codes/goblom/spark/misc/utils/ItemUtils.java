/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.utils;

import codes.goblom.spark.Log;
import codes.goblom.spark.reflection.Reflection;
import codes.goblom.spark.reflection.safe.SafeClass;
import codes.goblom.spark.reflection.safe.SafeMethod;
import codes.goblom.spark.reflection.safe.SafeObject;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Goblom
 */

//TODO: Read / Write hidden data in lore
public class ItemUtils {
    
    private ItemUtils() { }
    
    private static final ItemUtils itemUtils = new ItemUtils();
    private static final Base64 BASE64 = new Base64();
    
    public static void lowerHandItem(Player player, int amount) {
        if (amount == 0) return;
        ItemStack item = player.getItemInHand();
        
        if ((item.getAmount() - amount) <= 0) {
            player.setItemInHand(null);
            return;
        }
        
        item.setAmount(item.getAmount() - amount);

        player.setItemInHand(item);
    }
    
    public static String getItemCode(ItemStack item) {
        SafeClass CraftItemStack = Reflection.getCraftClass("inventory.CraftItemStack");
        SafeMethod asNMSCopy = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
        SafeObject nmsCopy = asNMSCopy.invoke(null, item);
        
        SafeMethod a = nmsCopy.getSafeClass().getMethod("a");
        return a.invoke(nmsCopy).as(String.class);
    }
    
    public static ItemStack getCustomSkull(String url) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        
        if (propertyMap == null) {
            Log.severe("Profile doesn't contain a property map, using default player head skull");
            return new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        }
        
        byte[] data = BASE64.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        propertyMap.put("textures", new Property("textures", new String(data)));
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        ItemMeta meta = item.getItemMeta();
        
        SafeClass SkullMeta = new SafeClass(meta.getClass());
                  SkullMeta.getField("profile", GameProfile.class).set(meta, profile);
                  
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack removeAttributes(ItemStack item) {
        if (item == null) {
            return item;
        }
        
        SafeClass NMSItemStack = Reflection.getNMSClass("ItemStack");
        SafeClass NBTTagCompound = Reflection.getNMSClass("NBTTagCompund");
        SafeClass NBTBase = Reflection.getNMSClass("NBTBase");
        SafeClass NBTTagList = Reflection.getNMSClass("NBTTagList");
        SafeClass CraftItemStack = Reflection.getCraftClass("CraftItemStack");

        SafeMethod asNMSCopy = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
        SafeMethod<ItemStack> asCraftMirror = CraftItemStack.getMethod("asCraftMirror", NMSItemStack.unsafe());
        SafeMethod<Boolean> hasTag = NMSItemStack.getMethod("hasTag");
        SafeMethod<Void> setTag = NMSItemStack.getMethod("setTag", NBTTagCompound.unsafe());
        SafeMethod getTag = NMSItemStack.getMethod("getTag");
        SafeMethod<Void> set = NBTTagCompound.getMethod("set", String.class, NBTBase.unsafe());
        
        SafeObject nmsCopy = asNMSCopy.invoke(null, item);
        
        if (nmsCopy == null) {
            return item;
        }
        
        SafeObject tag;
        if (!hasTag.invokeGeneric(nmsCopy)) {
            tag = NBTTagCompound.newInstance();
            setTag.invoke(nmsCopy, tag);
        } else {
            tag = getTag.invoke(nmsCopy);
        }
        
        SafeObject tagList = NBTTagList.newInstance();
        set.invokeGeneric(tag, "AttributeModifiers", tagList);
        setTag.invokeGeneric(nmsCopy, tag);
        
        return asCraftMirror.invokeGeneric(null, nmsCopy);
    }
    
    public static Builder build(Material mat) {
        return itemUtils.new Builder(mat);
    }
    
    public static Builder build(ItemStack stack) { 
        return itemUtils.new Builder(stack);
    }
    
    public static Builder build(Material mat, int amount) {
        return itemUtils.new Builder(mat, amount);
    }
    
    public static Builder build(Material mat, int amount, short damage) {
        return itemUtils.new Builder(mat, amount, damage);
    }
    
    // **************************************
    // ItemStack chainable builder
    // **************************************
    
    // TODO: BookMeta
    // TODO: BannerMeta
    // TODO: MapMeta
    public class Builder {
        private final ItemStack stack;
        
        protected Builder(ItemStack stack) {
            this.stack = stack;
        }
        
        protected Builder(Material mat) {
            this.stack = new ItemStack(mat);
        }
        
        protected Builder(Material mat, int amount) {
            this.stack = new ItemStack(mat, amount);
        }
        
        protected Builder(Material mat, int amount, short damage) {
            this.stack = new ItemStack(mat, amount, damage);
        }
        
        public ItemStack build() {
            return this.stack;
        }
        
//        private <M extends ItemMeta> M meta() {
//            return (M) stack.getItemMeta();
//        }
        
        private ItemMeta meta() {
            return stack.getItemMeta();
        }
        
        public Builder mainEffect(PotionEffectType type) {
            if (meta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) meta();
                           meta.setMainEffect(type);
                
                stack.setItemMeta(meta);
            }
            
            return this;
        }
        
        public Builder customEffect(PotionEffect effect, boolean override) {
            if (meta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) meta();
                           meta.addCustomEffect(effect, override);
                           
                stack.setItemMeta(meta);
            }
            
            return this;
        }
        
        public Builder skullOwner(String name) {
            if (meta() instanceof SkullMeta) {
                SkullMeta meta = (SkullMeta) meta();
                          meta.setOwner(name);

                stack.setItemMeta(meta);
            }
            
            return this;
        }
        
        public Builder skullOwner(OfflinePlayer player) {
            return skullOwner(player.getName());
        }
        
        public Builder name(String name) {
            ItemMeta meta = meta();
                     meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            stack.setItemMeta(meta);
            
            return this;
        }
        
        public Builder lore(String line) {
            ItemMeta meta = meta();
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = Lists.newArrayList();
            }
            
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
            meta.setLore(lore);
            stack.setItemMeta(meta);
            
            return this;
        }
        
        public Builder lore(List<String> lines) {
            return lore(lines, true);
        }
        
        public Builder lore(List<String> lines, boolean replace) {
            ItemMeta meta = meta();
            List<String> lore = meta.getLore();
            
            if (lore == null) {
                lore = replace ? lines : Lists.newArrayList();
            }
            
            if (!replace) {
                lore.addAll(lines);
            }
            
            meta.setLore(lore);
            stack.setItemMeta(meta);
            
            return this;
        }
        
        public Builder data(int data) {
            stack.setData(new MaterialData(stack.getType(), (byte) data));
            
            return this;
        }
        
        public Builder amount(int amount) {
            stack.setAmount(amount);
            
            return this;
        }
        
        public Builder durability(int durability) {
            stack.setDurability((short) durability);
            
            return this;
        }
        
        public Builder enchant(Enchantment ench, int level) {
            stack.addUnsafeEnchantment(ench, level);
            
            return this;
        }
        
        public Builder enchant(Enchantment ench) {
            return enchant(ench, 1);
        }
        
        public Builder type(Material mat) {
            stack.setType(mat);
            
            return this;
        }
        
        public Builder clearLore() {
            ItemMeta meta = meta();
                     meta.setLore(Lists.newArrayList());
                     
            stack.setItemMeta(meta);
            
            return this;
        }
        
        public Builder clearEnchantments() {
            stack.getEnchantments().keySet().forEach((Enchantment ench) -> {
                stack.removeEnchantment(ench);
            });
            
            return this;
        }
        
        public Builder color(Color color) {
            if (meta() instanceof LeatherArmorMeta) {
                LeatherArmorMeta meta = (LeatherArmorMeta) meta();
                meta.setColor(color);
                stack.setItemMeta(meta);
            }
            
            return this;
        }
        
        public Builder reset() {
            stack.setItemMeta(Bukkit.getItemFactory().getItemMeta(stack.getType()));
            
            return this;
        }
    }
}
