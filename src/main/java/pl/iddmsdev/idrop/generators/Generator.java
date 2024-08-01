package pl.iddmsdev.idrop.generators;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;
import pl.iddmsdev.idrop.utils.Miscellaneous;
import pl.iddmsdev.idrop.utils.Prefabs;

import java.util.*;
import java.util.logging.Level;


public class Generator {

    public static Map<Location, Generator> tickingGenerators = new HashMap<>();

    private final String systemName;

    private final ItemStack item; // not sure with final

    public Generator(String sysName, String generatorYMLKey) {
        this.systemName = sysName;
        this.item = createItem(generatorYMLKey);
    }

    public String getSystemName() {
        return systemName;
    }

    public ItemStack getItem() {
        return item;
    }

    private ItemStack createItem(String b) {
        ConfigFile a = iDrop.generatorsYML;
        String path = "generators." + b + ".";
        Material c;
        try {
            c = Miscellaneous.tryToGetMaterial(a.getRawString(path + "item"));
        } catch (IllegalArgumentException ex) {
            return Prefabs.getErrorItemPrefab(a.getFile().getName(), path + "item");
        }
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
        if (a.getBoolean(path + "glowing")) {
            d.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        return d;
    }

    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
