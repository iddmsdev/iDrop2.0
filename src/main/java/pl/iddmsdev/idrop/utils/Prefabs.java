package pl.iddmsdev.idrop.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Prefabs {

    public static ItemStack getErrorItemPrefab(String fileName, String path) {
        ItemStack is = new ItemStack(Material.DIRT);
        ItemMeta im = is.getItemMeta();
        //noinspection DataFlowIssue
        im.setDisplayName("§cInvalid configuration.");
        im.setLore(Arrays.asList("§7Check for any errors with this item. Here's info:",
                "§aFile: §b" + fileName,
                "§aPath: §b" + path.replaceAll("\\.", " -> ")));
        is.setItemMeta(im);
        return is;
    }

}
