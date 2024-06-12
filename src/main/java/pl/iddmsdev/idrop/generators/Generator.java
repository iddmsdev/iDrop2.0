package pl.iddmsdev.idrop.generators;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.iddmsdev.idrop.iDrop;

import java.util.Arrays;


public class Generator {
    private final String systemName;

    private final ItemStack item; // not sure with final

    public Generator(String sysName, FileConfiguration genYML, String generatorYMLKey) {
        this.systemName = sysName;
        this.item = createItem(genYML, generatorYMLKey);
    }
    public String getSystemName() {
        return systemName;
    }

    public ItemStack getItem() {
        return item;
    }

    private ItemStack createItem(FileConfiguration a, String b) {
        try {
            String path = "generators." + b + ".";
            Material c = Material.valueOf(a.getString(path + "item").toUpperCase());
            ItemStack d = new ItemStack(c);
            ItemMeta e = d.getItemMeta();
            NamespacedKey f = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-data");
            NamespacedKey g = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-gen");
            e.getPersistentDataContainer().set(f, PersistentDataType.STRING, "generator");
            e.getPersistentDataContainer().set(g, PersistentDataType.STRING, b);
            e.setLore(Arrays.asList("test1 : " + b));
            d.setItemMeta(e);
            return d;
        } catch(Exception ex) {
            Bukkit.getLogger().severe("[iDrop] Cannot create item of generator " + b + ". Check if 'item' value is real minecraft item");
            return null;
        }
    }

}
