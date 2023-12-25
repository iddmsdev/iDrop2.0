package pl.iddmsdev.idrop.drops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.iddmsdev.idrop.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockDrop implements Listener {
    FileConfiguration config = Main.blocksYML;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (config.getBoolean("enabled")) {
            if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                for (String path : config.getConfigurationSection("drops").getKeys(false)) {
                    // DROP FROM SPECIFIED TOOLS
                    boolean pickaxesContinue;
                    if (config.getBoolean("drops." + path + ".pickaxes.enabled")) {
                        List<Material> pickaxes = new ArrayList<>();
                        for (String item : config.getStringList("drops." + path + ".pickaxes.items")) {
                            Material mat = Material.valueOf(item.toUpperCase());
                            pickaxes.add(mat);
                        }
                        pickaxesContinue = pickaxes.contains(e.getPlayer().getInventory().getItemInMainHand().getType());
                    } else {
                        pickaxesContinue = true;
                    }
                    // DROPPING MECHANIC
                    if (pickaxesContinue) {
                        List<Material> blocks = new ArrayList<>();
                        String fullpath = "drops." + path;
                        // SETUP BLOCK MATERIALS
                        if (!config.getStringList(fullpath + ".blocks").isEmpty()) {
                            for (String material : config.getStringList(fullpath + ".blocks")) {
                                blocks.add(Material.valueOf(material.toUpperCase()));
                            }
                        } else if (config.getString(fullpath + ".blocks") != null) {
                            blocks.add(Material.valueOf(config.getString(fullpath + ".blocks").toUpperCase()));
                        } else {
                            Bukkit.getLogger().severe("Cannot find block materials in drop '" + path + "'");
                            continue;
                        }

                        // SETUP ITEM AND CHECK IF BLOCK IS CORRECT
                        for (Material mat : blocks) {
                            if (mat == null) {
                                Bukkit.getLogger().warning("Invalid material in drop '" + path + "'");
                            } else {
                                // CHECKING BLOCK
                                if (e.getBlock().getType().equals(mat)) {
                                    Random random = new Random();
                                    if (config.getString(fullpath + ".item") == null) {
                                        Bukkit.getLogger().severe("Cannot find item material in drop '" + path + "'");
                                        continue;
                                    } else {
                                        // SETUP ITEM
                                        // Material
                                        Material itemMaterial = Material.valueOf(config.getString(fullpath + ".item"));
                                        // Amount
                                        int amount;
                                        if (config.getInt(fullpath + ".count-min") == config.getInt(fullpath + ".count-max")) {
                                            amount = config.getInt(fullpath + ".count-min");
                                        } else if (config.getInt(fullpath + ".count-min") > 0) {
                                            int countMin = config.getInt(fullpath + ".count-min");
                                            int countMax;
                                            if (config.getInt(fullpath + ".count-max") > 0)
                                                countMax = config.getInt(fullpath + ".count-max");
                                            else countMax = 64;
                                            amount = random.nextInt(countMax + 1 - countMin) * countMin + 1;
                                        } else {
                                            int countMin = 1;
                                            int countMax;
                                            if (config.getInt(fullpath + ".count-max") > 0)
                                                countMax = config.getInt(fullpath + ".count-max");
                                            else countMax = 64;
                                            amount = random.nextInt(countMax + 1 - countMin) * countMin + 1;
                                        }
                                        // Finish setup
                                        ItemStack item = new ItemStack(itemMaterial, amount);
                                        // SETUP DROP
                                        // Chance
                                        double chance = config.getDouble(fullpath + ".chance");
                                        double choice = 0 + 100 * random.nextDouble();
                                        if (choice <= chance) {
                                            // Add drop
                                            if(!config.getBoolean(fullpath + ".drop-default-block")) {
                                                    e.setDropItems(false);
                                            } else if(Main.blockDroppingDirectlyToInv) {
                                                e.setDropItems(false);
                                                List<ItemStack> modifiedDrops = (List<ItemStack>) e.getBlock().getDrops();
                                                modifiedDrops.add(item);
                                                for(ItemStack drop : modifiedDrops) {
                                                    if(hasAvailableSlot(e.getPlayer(), drop)) {
                                                        e.getPlayer().getInventory().addItem(drop);
                                                    } else {
                                                        e.getBlock().getWorld().dropItemNaturally(e.getPlayer().getLocation(), drop);
                                                    }
                                                }
                                            } else {
                                                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
                                            }
                                            // Message
                                            if (config.getString(fullpath + ".message") != null) {
                                                e.getPlayer().sendMessage(
                                                        ChatColor.translateAlternateColorCodes('&', config.getString(fullpath + ".message")));
                                            }
                                            // Experience
                                            if (config.getInt(fullpath + ".experience-min") == config.getInt(fullpath + ".experience-max")) {
                                                e.getPlayer().giveExp(config.getInt(fullpath + ".experience-min"));
                                            } else if (config.getInt(fullpath + ".experience-min") > 0) {
                                                int min = config.getInt(fullpath + ".experience-min");
                                                int max;
                                                if (config.getInt(fullpath + ".experience-max") > 0)
                                                    max = config.getInt(fullpath + ".experience-max");
                                                else max = 100;
                                                int finalAmount = random.nextInt(max + 1 - min) + min;
                                                e.getPlayer().giveExp(finalAmount);
                                            } else {
                                                int min = 1;
                                                int max;
                                                if (config.getInt(fullpath + ".experience-max") > 0)
                                                    max = config.getInt(fullpath + ".experience-max");
                                                else max = 100;
                                                int finalAmount = random.nextInt(max + 1 - min) + min;
                                                e.getPlayer().giveExp(finalAmount);
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if(Main.blockDroppingDirectlyToInv) {
                    e.setDropItems(false);
                    for (ItemStack drop : e.getBlock().getDrops()) {
                        if (hasAvailableSlot(e.getPlayer(), drop)) {
                            e.getPlayer().getInventory().addItem(drop);
                        } else {
                            e.getBlock().getWorld().dropItemNaturally(e.getPlayer().getLocation(), drop);
                        }
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
