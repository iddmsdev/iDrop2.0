package pl.iddmsdev.idrop.essentials;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StraightDrop {

    // in future updates

    public boolean hasAvailableSlot(Player player, ItemStack itemLookingFor) {
        Inventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();

        int slot = 0;
        for (ItemStack item : contents) {
            if (slot >= 36) break;
            else if (item == null) {
                return true;
            } else if (item.hasItemMeta()) {
                if (item.getItemMeta().equals(itemLookingFor.getItemMeta()) && item.getType().equals(itemLookingFor.getType()) && item.getAmount() < item.getMaxStackSize()) {
                    return true;
                }
            }
            slot++;
        }
        return false;
    }

}
