package pl.iddmsdev.idrop.wtyczkamc.cores;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.iddmsdev.idrop.iDrop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneratorCore {

    private static FileConfiguration cfg = iDrop.coresYML;

    public static ItemStack getCore(int level) {
        ItemStack core = new ItemStack(Material.valueOf(cfg.getString("material").toUpperCase()));
        ItemMeta meta = core.getItemMeta();
        NamespacedKey levelKey = new NamespacedKey(iDrop.getPlugin(iDrop.class), "wtyczkamc-corelvl");
        NamespacedKey dataKey = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-data");
        meta.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
        meta.getPersistentDataContainer().set(dataKey, PersistentDataType.STRING, "wmc-gen-core");
        meta.setDisplayName(c(cfg.getString("name").replaceAll("%level%", String.valueOf(level))));
        meta.setLore(cfg.getStringList("lore").stream()
                .map(s -> c(s.replaceAll("%level%", String.valueOf(level))))
                .collect(Collectors.toList()));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        core.setItemMeta(meta);
        if(cfg.getBoolean("glowing")) core.addUnsafeEnchantment(Enchantment.LUCK, 1);
        return core;
    }
    private static String c(String m) {
        return ChatColor.translateAlternateColorCodes('&', m);
    }

}
