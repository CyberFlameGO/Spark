/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc;

import codes.goblom.spark.reflection.safe.SafeClass;
import codes.goblom.spark.reflection.safe.SafeField;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor
public class SparkShapedRecipe {
    private static final char EMPTY_SLOT = '§';
    
    public static SparkShapedRecipe forItem(ItemStack item) {
        return new SparkShapedRecipe(item);
    }
    
    private final ItemStack stack;
    private final Map<Character, ItemStack> ingredients = Maps.newHashMap();
    private final RecipeBox box = new RecipeBox();

    public SparkShapedRecipe reset() {
        box.clear();
        ingredients.clear();
        
        return this;
    }
    
    public SparkShapedRecipe top(int slot, char c) {
        box.setTop(slot, c);
        
        return this;
    }
    
    public SparkShapedRecipe middle(int slot, char c) {
        box.setMiddle(slot, c);
        
        return this;
    }
    
    public SparkShapedRecipe bottom(int slot, char c) {
        box.setBottom(slot, c);
        
        return this;
    }
    
    public SparkShapedRecipe ingredient(char c, Material mat) {
        Validate.isTrue(!box.anyContains(c), "Can only set ingredient with char that exists in shape.");
        ingredients.put(c, new ItemStack(mat));
        
        return this;
    }
    
    public SparkShapedRecipe ingredient(char c, ItemStack stack) {
        Validate.isTrue(!box.anyContains(c), "Can only set ingredient with char that exists in shape.");
        ingredients.put(c, stack);
        
        return this;
    }

    public Recipe build() {
        ShapedRecipe recipe = new ShapedRecipe(stack);
                     recipe.shape(box.getShape());
                     
        SafeClass c = new SafeClass(recipe.getClass());
        SafeField f = c.getField("ingredients");
                  f.setAccessible(true);
                  
                  f.set(recipe, ingredients);
                  
        return recipe;
    }
    
    public boolean registerRecipe() {
        return Bukkit.addRecipe(build());
    }
    
    private static class RecipeBox {
        final char[] top, middle, bottom;
        
        private RecipeBox() {
            this.top = new char[3];
            this.middle = new char[3];
            this.bottom = new char[3];
            
            clear();
        }
        
        final void clear() {
            Arrays.asList(top, middle, bottom).stream().forEach((chars) -> {
                for (int i = 0; i < chars.length; i++) {
                    chars[i] = EMPTY_SLOT;
                }
            });
        }
        
        boolean anyContains(char c) {
            for (char[] chars : Arrays.asList(top, middle, bottom)) {
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == c) {
                        return true;
                    }
                }
            }
            
            return false;
        }
        
        private void validate(char c) {
            Validate.isTrue(c == EMPTY_SLOT, "Cannot use same symbol as empty slot... [" + EMPTY_SLOT + "]");
        }
        
        private void validate(int i) {
            Validate.isTrue(i >= 1 && i <= 3, "Slot cannot be less than 1 or greater than 3 not");
        }
        
        void setTop(int slot, char string) {
            validate(slot);
            validate(string);
            
            top[slot - 1] = string;
        }
        
        void setMiddle(int slot, char string) {
            validate(slot);
            validate(string);
            
            middle[slot - 1] = string;
        }
        
        void setBottom(int slot, char string) {
            validate(slot);
            validate(string);
            
            middle[slot - 1] = string;
        }
        
        public String getString(char[] chars) {
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < chars.length; i++) {
                sb.append(chars[i]);
            }
            
            return sb.toString();
        }
        
        public String[] getShape() {
            return new String[] { getString(top), getString(middle), getString(bottom) };
        }
    }
}
