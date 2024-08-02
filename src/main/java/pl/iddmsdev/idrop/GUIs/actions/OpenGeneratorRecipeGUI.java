package pl.iddmsdev.idrop.GUIs.actions;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.iddmsdev.idrop.GUIs.GUIAction;
import pl.iddmsdev.idrop.GUIs.iDropGuiInterpreter;
import pl.iddmsdev.idrop.generators.recipes.Recipe;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;

import java.util.Map;
import java.util.logging.Level;

public class OpenGeneratorRecipeGUI implements GUIAction {
    @Override
    public void handler(InventoryClickEvent e, String actionDataPath, ConfigFile cfg) {
        Player p = (Player) e.getWhoClicked();
        iDropGuiInterpreter interpreter = new iDropGuiInterpreter(cfg, "guis", "variables");
        String recipeGUI = cfg.getString(actionDataPath + ".gui");
        String[] slots = new String[9];
        for(int i = 1; i<=9; i++) {
            slots[i-1] = cfg.getString(actionDataPath + ".slot" + i);
        }
        String resultSlot = cfg.getString(actionDataPath + ".result-slot");
        String recipeID = cfg.getString(actionDataPath + ".recipe");
        Recipe rec = iDrop.getRecipeByID(recipeID);
        Inventory inv = interpreter.compile(recipeGUI);
        ConfigFile recs = iDrop.genRecipesYML;
        ConfigFile guis = iDrop.genGuiYML;
        if(recs.contains("recipes."+recipeID)) {
            if(guis.contains("guis."+recipeGUI)) {
                int slotIndex = 0;
                int craftingSlotIndex = 1;
                NamespacedKey nKey = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-gui-action");
                for (String key : cfg.getConfigurationSection("guis." + recipeGUI + ".items").getKeys(false)) {
                    for (String slot : slots) {
                        if (key.equals(slot)) {
                            ItemStack is = new ItemStack(rec.getItemAtSlot(craftingSlotIndex));
                            ItemMeta im = is.getItemMeta();
                            im.getPersistentDataContainer().set(nKey, PersistentDataType.STRING, "none");
                            is.setItemMeta(im);
                            inv.setItem(slotIndex, is);
                            craftingSlotIndex++;
                            break;
                        } else if (key.equals(resultSlot)) {
                            ItemStack is = rec.getResult();
                            ItemMeta im = is.getItemMeta();
                            im.getPersistentDataContainer().set(nKey, PersistentDataType.STRING, "none");
                            is.setItemMeta(im);
                            inv.setItem(slotIndex, is);
                            break;
                        }
                    }
                    slotIndex++;

                }
                p.openInventory(inv);
            } else {
                Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Cannot open gui of recipe '"+recipeID+"' because there's no gui "+recipeGUI+" in gen-gui.yml.");
                e.setCancelled(true);
            }
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Cannot open gui of recipe '" + recipeID + "' because it doesn't exist!");
            e.setCancelled(true);
        }

    }

    @Override
    public String getLabel() {
        return "open-generator-recipe-gui";
    }
}
