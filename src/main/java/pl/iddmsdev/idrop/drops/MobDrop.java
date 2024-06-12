package pl.iddmsdev.idrop.drops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.iddmsdev.idrop.iDrop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobDrop implements Listener {

    FileConfiguration config = iDrop.mobsYML;

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        if(e.getEntity().getKiller()!=null) {
            if(!e.getEntityType().equals(EntityType.PLAYER)) {
                try {
                    for (String path : config.getConfigurationSection("drops").getKeys(false)) {
                        // draw
                        Random rnd = new Random();
                        double choice = 100 * rnd.nextDouble();
                        String drop = "drops." + path + ".";
                        if (choice <= config.getDouble(drop + "chance")) {
                            // checking if used valid tool
                            boolean isValidTool = true;
                            if (config.getBoolean(drop + "tools.enabled")) {
                                if (!config.getStringList(drop + "tools.items").isEmpty()) {
                                    for (String toolName : config.getStringList(drop + "tools.items")) {
                                        if (!e.getEntity().getKiller().getInventory().getItemInMainHand().getType().equals(
                                                Material.valueOf(toolName))) {
                                            isValidTool = false;
                                        } else isValidTool = true;
                                        break;
                                    }
                                } else {
                                    String toolName = config.getString(drop + "tools.items");
                                    if (!e.getEntity().getKiller().getInventory().getItemInMainHand().getType().equals(
                                            Material.valueOf(toolName))) isValidTool = false;
                                }
                            }
                            if (isValidTool) {
                                // checking mob validity
                                List<EntityType> mobs = new ArrayList<>();
                                if(!config.getStringList(drop+"mobs").isEmpty()) {
                                    config.getStringList(drop+"mobs").forEach(mob -> mobs.add(EntityType.valueOf(mob.toUpperCase())));
                                }
                                else mobs.add(EntityType.valueOf(config.getString(drop + "mobs").toUpperCase()));
                                if(mobs.contains(e.getEntityType())) {
                                    // creating item
                                    Material type = Material.valueOf(config.getString(drop + "item").toUpperCase());
                                    // amount
                                    int amount = 1;
                                    if (config.getInt(drop + "count-min") == config.getInt(drop + "count-max")) {
                                        amount = config.getInt(drop + "count-min");
                                    } else if (config.getInt(drop + "count-min") > 0) {
                                        int countMin = config.getInt(drop + "count-min");
                                        int countMax;
                                        if (config.getInt(drop + "count-max") > 0)
                                            countMax = config.getInt(drop + "count-max");
                                        else countMax = 64;
                                        amount = rnd.nextInt(countMax + 1 - countMin) * countMin + 1;
                                    } else {
                                        int countMin = 1;
                                        int countMax;
                                        if (config.getInt(drop + "count-max") > 0)
                                            countMax = config.getInt(drop + "count-max");
                                        else countMax = 64;
                                        amount = rnd.nextInt(countMax + 1 - countMin) * countMin + 1;
                                    }
                                    // combine
                                    ItemStack item = new ItemStack(type, amount);
                                    // dropping
                                    List<ItemStack> modifiedDrops = e.getDrops();
                                    if(!config.getBoolean(drop + "drop-default-items")) {
                                        e.getDrops().clear();
                                    } else if(iDrop.blockDroppingDirectlyToInv) {
                                        e.getDrops().clear();
                                        modifiedDrops.add(item);
                                        for(ItemStack d : modifiedDrops) {
                                            if(hasAvailableSlot(e.getEntity().getKiller(), d)) {
                                                e.getEntity().getKiller().getInventory().addItem(d);
                                            } else {
                                                e.getEntity().getWorld().dropItemNaturally(e.getEntity().getKiller().getLocation(), d);
                                            }
                                        }
                                    } else {
                                        e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), item);
                                    }
                                    // message
                                    if (config.getString(drop + "message") != null) {
                                        e.getEntity().getKiller().sendMessage(
                                                ChatColor.translateAlternateColorCodes('&', config.getString(drop + "message")));
                                    }
                                    // experience
                                    e.setDroppedExp(0);
                                    if (config.getInt(drop + "experience-min") == config.getInt(drop + "experience-max")) {
                                        e.getEntity().getKiller().giveExp(config.getInt(drop + "experience-min"));
                                    } else if (config.getInt(drop + "experience-min") > 0) {
                                        int min = config.getInt(drop + "experience-min");
                                        int max;
                                        if (config.getInt(drop + "experience-max") > 0)
                                            max = config.getInt(drop + "experience-max");
                                        else max = 100;
                                        int finalAmount = rnd.nextInt(max + 1 - min) + min;
                                        e.getEntity().getKiller().giveExp(finalAmount);
                                    } else {
                                        int min = 1;
                                        int max;
                                        if (config.getInt(drop + "experience-max") > 0)
                                            max = config.getInt(drop + ".experience-max");
                                        else max = 100;
                                        int finalAmount = rnd.nextInt(max + 1 - min) + min;
                                        e.getEntity().getKiller().giveExp(finalAmount);
                                    }
                                }
                            }
                        }
                    }
                } catch(NullPointerException ex) {
                    Bukkit.getLogger().severe("[iDrop] Something is wrong with your mobs.yml. Without any fix, the mob drops will not work.");
                }
            }
        }
    }
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
