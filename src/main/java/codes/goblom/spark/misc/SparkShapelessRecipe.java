/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc;

import codes.goblom.spark.reflection.safe.SafeClass;
import codes.goblom.spark.reflection.safe.SafeField;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor
public class SparkShapelessRecipe {
    
    public static SparkShapelessRecipe forItem(ItemStack item) {
        return new SparkShapelessRecipe(item);
    }
    
    private final ItemStack stack;
    private final List<ItemStack> ingredients = Lists.newArrayList();
    
    public SparkShapelessRecipe reset() {
        ingredients.clear();
        
        return this;
    }
    
    private void validate(int amount) {
        Validate.isTrue(ingredients.size() + amount <= 9, "Shapeless recipes cannot have more than 9 ingredients");
    }
    
    public SparkShapelessRecipe add(int amount, ItemStack i) {
        validate(amount);
        i.setAmount(1);
        
        while (amount-- > 0) {
            ingredients.add(i.clone());
        }
        
        return this;
    }
    
    public SparkShapelessRecipe add(ItemStack i) {
        return add(1, i);
    }
    
    public SparkShapelessRecipe add(int amount, Material mat) {
        return add(amount, new ItemStack(mat));
    }
    
    public SparkShapelessRecipe add(Material mat) {
        return add(1, mat);
    }
    
    public SparkShapelessRecipe add(int amount, Material mat, int data) {
        return add(amount, new ItemStack(mat, 1, (short) data));
    }
    
    public SparkShapelessRecipe add(Material mat, int data) {
        return add(1, new ItemStack(mat, 1, (short) data));
    }
    
    public Recipe build() {
        ShapelessRecipe recipe = new ShapelessRecipe(stack);
                      
        SafeClass c = new SafeClass(recipe.getClass());
        SafeField f = c.getField("ingredients");
                  f.setAccessible(true);
                  
                  f.set(recipe, ingredients);
                  
        return recipe;
    }
    
    public boolean registerRecipe() {
        return Bukkit.addRecipe(build());
    }
}
