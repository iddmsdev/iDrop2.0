package pl.iddmsdev.idrop.generators.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;
import pl.iddmsdev.idrop.utils.Miscellaneous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Recipe extends ShapedRecipe {
    final String recipeIdentifier;
    final ConfigFile recipes = iDrop.genRecipesYML;
    List<Material> crafting = new ArrayList<>();

    public String getRecipeIdentifier() {
        return recipeIdentifier;
    }

    public Recipe(NamespacedKey key, ItemStack result, String recipeIdentifier) {
        super(key, result);
        this.recipeIdentifier = recipeIdentifier;
        String[] shape = compileShape();
        try {
            this.shape(shape[0], shape[1], shape[2]);
            setIngredients();
            addCraftingSlot();
        }
        catch(IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check if your recipes."+recipeIdentifier+".crafting and ingredients have valid syntax.");
        }
    }
    private void setIngredients() {
        for(String str : recipes.getStringList("recipes."+recipeIdentifier+".ingredients")) {
            String[] spl = str.split(" : ");
            Material spl1 = Material.STONE;
            try {
                spl1 = Miscellaneous.tryToGetMaterial(spl[1]);
            } catch (IllegalArgumentException ex) {
                String epath = "recipes." + recipeIdentifier + ".ingredients";
                Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this item. Here's info: \n" +
                        "File: " + recipes.getFile().getName() + "\n" +
                        "Path: " + epath.replaceAll("\\.", " -> "));
            }
            this.setIngredient(spl[0].toUpperCase().charAt(0), spl1);
        }
    }
    private String[] compileShape() {
        return recipes.getStringList("recipes." + recipeIdentifier + ".crafting").toArray(new String[0]);
    }
    private void addCraftingSlot() {
        List<String> shapeStrings = recipes.getStringList("recipes."+recipeIdentifier+".crafting");
        char[] indexes = shapeStrings.stream()
                .flatMapToInt(String::chars)
                .mapToObj(c -> (char) c)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        lst -> {
                            char[] array = new char[lst.size()];
                            for(int i = 0; i < lst.size(); i++) {
                                array[i] = lst.get(i);
                            }
                            return array;
                        }));
        for(int i = 0; i<9; i++) {
            crafting.add(getIngredientMap().get(indexes[i]).getType());
        }
    }
    public void assignToPlugin() {
        Bukkit.addRecipe(this);
    }
    public Material getItemAtSlot(int slot) {
        return crafting.get(slot-1);
    }
}
