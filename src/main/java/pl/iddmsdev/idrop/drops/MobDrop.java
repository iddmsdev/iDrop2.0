package pl.iddmsdev.idrop.drops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.iddmsdev.idrop.GUIs.actions.ToggleChatMessages;
import pl.iddmsdev.idrop.drops.megadrop.MegaDrop;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;
import pl.iddmsdev.idrop.utils.Miscellaneous;
import pl.iddmsdev.idrop.utils.Prefabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class MobDrop implements Listener {

    ConfigFile config = iDrop.mobsYML;

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        if(config.getBoolean("enabled")) {
            if (e.getEntity().getKiller() != null) {
                if (!e.getEntityType().equals(EntityType.PLAYER)) {
                    try {
                        for (String path : config.getConfigurationSection("drops").getKeys(false)) {
                            // draw
                            double chance = config.getDouble("drops." + path + ".chance");
                            ItemStack mainHand = e.getEntity().getKiller().getInventory().getItemInMainHand();
                            int amount = 1;
                            if (mainHand.hasItemMeta()) {
                                if (mainHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) > 0) {
                                    amount = Fortune.modifyAmount(path, "mobs", mainHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS), 0);
                                    chance = Fortune.modifyChance(path, "mobs", mainHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS), chance);
                                }
                            }
                            Random rnd = new Random();
                            if (MegaDrop.hasPlayerMegaDrop(e.getEntity().getKiller())) {
                                chance = MegaDrop.getModifiedChance(path, "mobs", chance);
                            }
                            double choice = 100 * rnd.nextDouble();
                            String drop = "drops." + path + ".";
                            if (choice <= chance) {
                                // checking if used valid tool
                                boolean isValidTool = true;
                                if (config.getBoolean(drop + "tools.enabled")) {
                                    if (!config.getStringList(drop + "tools.items").isEmpty()) {
                                        for (String toolName : config.getStringList(drop + "tools.items")) {
                                            Material tMat = Material.AIR;
                                            try {
                                                tMat = Miscellaneous.tryToGetMaterial(toolName);
                                            } catch (IllegalArgumentException ex) {
                                                String epath = drop + "tools.items";
                                                Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this item. Here's info: " +
                                                        "File: " + config.getFile().getName() +
                                                        "Path: " + epath.replaceAll("\\.", " -> "));
                                                e.getEntity().getKiller().sendMessage("§c[iDrop] Check console for errors.");
                                            }
                                            if (e.getEntity().getKiller().getInventory().getItemInMainHand().getType()!=tMat) {
                                                isValidTool = false;
                                            } else isValidTool = true;
                                            break;
                                        }
                                    } else {
                                        String toolName = config.getString(drop + "tools.items");
                                        Material tMat = Material.AIR;
                                        try {
                                            tMat = Miscellaneous.tryToGetMaterial(toolName);
                                        } catch (IllegalArgumentException ex) {
                                            String epath = drop + "tools.items";
                                            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this item. Here's info:" +
                                                    "File: " + config.getFile().getName() +
                                                    "Path: " + epath.replaceAll("\\.", " -> "));
                                            e.getEntity().getKiller().sendMessage("§c[iDrop] Check console for errors.");
                                        }
                                        if (e.getEntity().getKiller().getInventory().getItemInMainHand().getType()!=tMat)
                                            isValidTool = false;
                                    }
                                }
                                if (isValidTool) {
                                    // checking mob validity
                                    List<EntityType> mobs = new ArrayList<>();
                                    if (!config.getStringList(drop + "mobs").isEmpty()) {
                                        for (String mob : config.getStringList(drop + "mobs")) {
                                            try {
                                                EntityType entityType = EntityType.valueOf(mob.toUpperCase());
                                                mobs.add(entityType);
                                            } catch (IllegalArgumentException ex) {
                                                String epath = drop + "mobs";
                                                Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this mob. Here's info:\n" +
                                                        "File: " + config.getFile().getName() + "\n" +
                                                        "Path: " + epath.replaceAll("\\.", " -> "));
                                                e.getEntity().getKiller().sendMessage("§c[iDrop] Check console for errors.");
                                                return;
                                            }
                                        }
                                    } else {
                                        try {
                                            EntityType entityType = EntityType.valueOf(config.getString(drop + "mobs").toUpperCase());
                                            mobs.add(entityType);
                                        } catch (IllegalArgumentException ex) {
                                            String epath = drop + "mobs";
                                            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this mob. Here's info:\n" +
                                                    "File: " + config.getFile().getName() + "\n" +
                                                    "Path: " + epath.replaceAll("\\.", " -> "));
                                            e.getEntity().getKiller().sendMessage("§c[iDrop] Check console for errors.");
                                            return;
                                        }
                                    }
                                    if (mobs.contains(e.getEntityType())) {
                                        // creating item
                                        Material type;
                                        try {
                                            type = Miscellaneous.tryToGetMaterial(config.getString(drop + "item"));
                                        } catch(IllegalArgumentException ex) {
                                            type = Material.STONE;
                                            String epath = drop + ".item";
                                            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors w ith this item. Here's info:" +
                                                    "File: " + config.getFile().getName() +
                                                    "Path: " + epath.replaceAll("\\.", " -> "));
                                            e.getEntity().getKiller().sendMessage("§c[iDrop] Check console for errors.");
                                        }
                                        // amount
                                        if (config.getInt(drop + "count-min") == config.getInt(drop + "count-max")) {
                                            amount += config.getInt(drop + "count-min");
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
                                        if (!config.getBoolean(drop + "drop-default-items")) {
                                            e.getDrops().clear();
                                        }
                                        if (iDrop.blockDroppingDirectlyToInv) {
                                            e.getDrops().clear();
                                            modifiedDrops.add(item);
                                            for (ItemStack d : modifiedDrops) {
                                                if (hasAvailableSlot(e.getEntity().getKiller(), d)) {
                                                    e.getEntity().getKiller().getInventory().addItem(d);
                                                } else {
                                                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getKiller().getLocation(), d);
                                                }
                                            }
                                        } else {
                                            e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), item);
                                        }
                                        // message
                                        if(!ToggleChatMessages.disablesDropMessages.contains(e.getEntity().getKiller().getUniqueId())) {
                                            if (config.getString(drop + ".message") != null) {
                                                // NS
                                                e.getEntity().getKiller().sendMessage(
                                                        ChatColor.translateAlternateColorCodes('&', config.getString(drop + ".message")));
                                            }
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
                    } catch (NullPointerException ex) {
                        Bukkit.getLogger().severe("[iDrop] Something is wrong with your mobs.yml. Without any fix, the mob drops will not work.");
                    }
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
