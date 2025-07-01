package pl.iddmsdev.idrop.GUIs.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.iddmsdev.idrop.GUIs.GUIAction;
import pl.iddmsdev.idrop.utils.ConfigFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ToggleChatMessages implements GUIAction {

    public static List<UUID> disablesDropMessages = new ArrayList<>();

    @Override
    public void handler(InventoryClickEvent e, String actionDataPath, ConfigFile cfg) {
        UUID uuid = e.getWhoClicked().getUniqueId();
        if(disablesDropMessages.contains(uuid)) {
            disablesDropMessages.add(uuid);
            changeName(e, true, actionDataPath, cfg);
        } else {
            disablesDropMessages.remove(uuid);
            changeName(e, false, actionDataPath, cfg);
        }
    }
    private void changeName(InventoryClickEvent e, boolean toEnable, String actionData, ConfigFile cfg) {
        ItemStack item = e.getCurrentItem();
        ItemMeta im = item.getItemMeta();
        if(toEnable) {
            im.setDisplayName(cfg.getString(actionData + ".enable-text"));
            item.setItemMeta(im);
        } else {
            im.setDisplayName(cfg.getString(actionData + ".disable-text"));
            item.setItemMeta(im);
        }
    }

    @Override
    public String getLabel() {
        return "toggle-chat-messages";
    }
}
