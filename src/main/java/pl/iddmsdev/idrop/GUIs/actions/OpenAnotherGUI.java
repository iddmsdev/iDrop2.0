package pl.iddmsdev.idrop.GUIs.actions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.iddmsdev.idrop.GUIs.GUIAction;
import pl.iddmsdev.idrop.GUIs.iDropGuiInterpreter;
import pl.iddmsdev.idrop.utils.ConfigFile;

import java.util.List;

public class OpenAnotherGUI implements GUIAction {
    @Override
    public void handler(InventoryClickEvent e, String actionDataPath, ConfigFile cfg) {
        List<String> data = cfg.getStringList(actionDataPath);
        String target = data.get(0);
        String guis = data.get(1);
        String vars = data.get(2);
        Player p = (Player) e.getWhoClicked();
        iDropGuiInterpreter interpreter = new iDropGuiInterpreter(cfg, guis, vars);
        p.openInventory(interpreter.compile(target));
    }

    @Override
    public String getLabel() {
        return "open-internal-gui";
    }
}
