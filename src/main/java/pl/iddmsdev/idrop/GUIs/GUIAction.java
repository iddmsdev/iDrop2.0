package pl.iddmsdev.idrop.GUIs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface GUIAction {

    void handler(InventoryClickEvent e, String actionDataPath, FileConfiguration cfg);
    String getLabel();

}