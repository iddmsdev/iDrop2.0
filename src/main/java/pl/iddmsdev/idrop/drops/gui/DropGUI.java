package pl.iddmsdev.idrop.drops.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.iddmsdev.idrop.GUIs.iDropGuiInterpreter;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;
import pl.iddmsdev.idrop.utils.Miscellaneous;
import pl.iddmsdev.idrop.utils.Prefabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DropGUI {


    private final ConfigFile cfg = iDrop.dropGuiYML;
    private final ConfigFile msg = iDrop.messagesYML;
    private final Player player;

    public DropGUI(Player p) {
        this.player = p;
    }

    public void openGUI() {
        iDropGuiInterpreter interpreter = new iDropGuiInterpreter(cfg, "guis", "variables");
        String guiPath = cfg.getString("drops");
        Inventory inv = interpreter.compile(guiPath);
        int index = 0;
        for (String path : cfg.getConfigurationSection("guis." + guiPath + ".items").getKeys(false)) {
            String fpath = "guis." + guiPath + ".items." + path + ".";
            if (cfg.contains(fpath + "drop") && cfg.contains(fpath + "drop-type")) {
                ConfigFile dropConfig;
                String lfrom; // Localized from (it can be blocks or mobs)
                String lobtained; // Localized obtained (it can be pickaxes or tools)
                if (cfg.getString(fpath + "drop-type").equalsIgnoreCase("blocks")) {
                    dropConfig = iDrop.blocksYML;
                    lfrom = "blocks";
                    lobtained = "pickaxes";
                } else {
                    dropConfig = iDrop.mobsYML;
                    lfrom = "mobs";
                    lobtained = "tools";
                }
                String drop = cfg.getString(fpath + "drop");
                String dpath = "drops." + drop + "."; // dpath - droppath
                Material mat;
                ItemStack item;
                try {
                    mat = Miscellaneous.tryToGetMaterial(dropConfig.getRawString(dpath + "item"));
                } catch (IllegalArgumentException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this item. Here's info:" +
                            "File: " + dropConfig.getFile().getName() +
                            "Path: " + (dpath + "item").replaceAll("\\.", " -> "));
                    return;
                }
                item = new ItemStack(mat, 1);
                ItemMeta im = item.getItemMeta();
                String name = cfg.getString("drop-item-name").replaceAll("%name%", drop);
                if (!name.equals("none")) im.setDisplayName(name);
                List<String> lore = new ArrayList<>();
                for (String line : cfg.getStringList("drop-item-lore")) {
                    String from;
                    if (dropConfig.isList(dpath + lfrom)) {
                        from = processListToReadFriendly(dropConfig.getStringList(dpath + lfrom));
                    } else {
                        from = processListToReadFriendly(Collections.singletonList(dropConfig.getString(dpath + lfrom)));
                    }
                    String obtained;
                    if (dropConfig.getBoolean(dpath + lobtained + ".enabled")) {
                        if (dropConfig.isList(dpath + lobtained + ".items")) {
                            obtained = processListToReadFriendly(dropConfig.getStringList(dpath + lobtained + ".items"));
                        } else {
                            obtained = processListToReadFriendly(Collections.singletonList(dropConfig.getString(dpath + lobtained + ".items")));
                        }
                    } else {
                        obtained = msg.getString("all-tools");
                    }
                    int min = 1;
                    if (dropConfig.contains(dpath + "count-min")) min = dropConfig.getInt(dpath + "count-min");
                    int max = 64;
                    if (dropConfig.contains(dpath + "count-max")) max = dropConfig.getInt(dpath + "count-max");
                    int amount = 0;
                    if(min-max==0) {
                        line = line.replaceAll("%count%", String.valueOf(min));
                    } else {
                        line = line.replaceAll("%count%",
                                msg.getString("from-x-to-y")).replaceAll("%x%", String.valueOf(min)).replaceAll("%y%", String.valueOf(max));
                    }
                    lore.add(line.
                                    replaceAll("%chance%", dropConfig.getDouble(dpath + "chance") + "%").
                                    replaceAll("%from%", from).
                                    replaceAll("%count%", String.valueOf(amount)).
                                    replaceAll("%obtained%", obtained)
                            );
                }
                im.setLore(lore);
                NamespacedKey nKey = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-gui-action");
                im.getPersistentDataContainer().set(nKey, PersistentDataType.STRING, "none");
                item.setItemMeta(im);
                inv.setItem(index, item);


            }
            index++;
        }
        player.openInventory(inv);
    }

    private String processListToReadFriendly(List<String> stringList) {
        return stringList.stream()
                .map(str -> {
                    String[] parts = str.split("_");
                    String capitalized = "";
                    for (String part : parts) {
                        capitalized += part.substring(0, 1).toUpperCase() + part.substring(1) + " ";
                    }
                    return capitalized.trim();
                })
                .collect(Collectors.joining(msg.getString("friendly-list-separator")));
    }

}
