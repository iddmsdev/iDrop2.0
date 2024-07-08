package pl.iddmsdev.idrop.generators;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.iddmsdev.idrop.iDrop;

import java.util.*;


public class Generator {

    public static Map<Location, Generator> tickingGenerators = new HashMap<>();

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
            List<String> lore = new ArrayList<>();
            for (String str : a.getStringList(path + "lore")) {
               lore.add(colorize(str));
            }
            e.setLore(lore);
            e.setDisplayName(colorize(a.getString(path + "name")));
            e.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            d.setItemMeta(e);
            if(a.getBoolean(path + "glowing")) {
                d.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }
            return d;
        } catch(Exception ex) {
            Bukkit.getLogger().severe("[iDrop] Cannot create item of generator " + b + ". Check if 'item' value is real minecraft item");
            return null;
        }
    }
    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
