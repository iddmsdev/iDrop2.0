package pl.iddmsdev.idrop.GUIs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.iddmsdev.idrop.utils.ConfigFile;

public interface GUIAction {

    void handler(InventoryClickEvent e, String actionDataPath, ConfigFile cfg);
    String getLabel();

}