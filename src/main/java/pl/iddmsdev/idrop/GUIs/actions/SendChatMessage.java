package pl.iddmsdev.idrop.GUIs.actions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.iddmsdev.idrop.GUIs.GUIAction;

public class SendChatMessage implements GUIAction {
    @Override
    public void handler(InventoryClickEvent e, String actionDataPath, FileConfiguration cfg) {
        for(String msg : cfg.getStringList(actionDataPath)) {
            e.getWhoClicked().sendMessage(colorize(msg));
        }
        e.setCancelled(true);
    }
    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public String getLabel() {
        return "send-chat-message";
    }
}
