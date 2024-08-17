package pl.iddmsdev.idrop.generators;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;
import pl.iddmsdev.idrop.utils.Miscellaneous;
import pl.iddmsdev.idrop.utils.Prefabs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

import static pl.iddmsdev.idrop.generators.GeneratorsDB.queryNRS;
import static pl.iddmsdev.idrop.generators.GeneratorsDB.queryRS;

public class GeneratorBlocks implements Listener {

    private final static ConfigFile gens = iDrop.generatorsYML;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {
        if(!e.isCancelled()) {
            if (e.getItemInHand().hasItemMeta()) {
                NamespacedKey key = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-data");
                ItemMeta meta = e.getItemInHand().getItemMeta();
                if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    if (meta.getPersistentDataContainer().get(key, PersistentDataType.STRING).equals("generator")) {
                        NamespacedKey key2 = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-gen");
                        if (meta.getPersistentDataContainer().has(key2, PersistentDataType.STRING)) {
                            String genKey = meta.getPersistentDataContainer().get(key2, PersistentDataType.STRING);
                            Location loc = e.getBlock().getLocation();
                            GeneratorsDB.queryNRS("INSERT INTO generators(sysKey, blockX, blockY, blockZ) VALUES (?, ?, ?, ?)",
                                    genKey, loc.getX(), loc.getY(), loc.getZ());
                            Material base;
                            try {
                                base = Miscellaneous.tryToGetMaterial(gens.getRawString("generators." + genKey + ".base-block"));
                            } catch (IllegalArgumentException ex) {
                                base = Material.END_STONE;
                                String path = "generators." + genKey + ".base-block";
                                Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this item. Here's info:" +
                                        "File: " + gens.getFile().getName() +
                                        "Path: " + path.replaceAll("\\.", " -> "));
                            }
                            if (e.getBlock().getType() != base) {
                                e.getBlock().setType(base);
                            }
                            String path = "generators." + genKey + ".generate.";
                            Map<String, Material> blocks = new HashMap<>();
                            Map<String, Double> chances = new HashMap<>();
                            for (String generated : gens.getConfigurationSection(path).getKeys(false)) {
                                String fpath = path + generated + ".";
                                try {
                                    Material mat = Miscellaneous.tryToGetMaterial(gens.getString(fpath + "block"));
                                    blocks.put(generated, mat);
                                } catch (IllegalArgumentException ex) {
                                    e.getPlayer().sendMessage("§c[iDrop] Check console for errors.");
                                    String epath = fpath + "block";
                                    Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this item. Here's info:" +
                                            "File: " + gens.getFile().getName() +
                                            "Path: " + epath.replaceAll("\\.", " -> "));
                                    return;
                                }
                                chances.put(generated, gens.getDouble(fpath + "chance"));
                            }
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    generate(chances, blocks, loc);
                                }
                            }.runTaskLater(iDrop.getPlugin(iDrop.class), 20L);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        if(!e.isCancelled()) {
            Material playerItem = e.getPlayer().getInventory().getItemInMainHand().getType();
            Material requiredItem = null;
            if (!gens.getString("destroying-item").equalsIgnoreCase("any")) {
                try {
                    requiredItem = Miscellaneous.tryToGetMaterial(gens.getString("destroying-item"));
                } catch (IllegalArgumentException ex) {
                    e.getPlayer().sendMessage("§c[iDrop] Check console for errors.");
                    String epath = "destroying-item";
                    Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this item. Here's info:" +
                            "File: " + gens.getFile().getName() +
                            "Path: " + epath.replaceAll("\\.", " -> "));
                    requiredItem = Material.AIR;
                }
            }
            Location loc = e.getBlock().getLocation();
            ResultSet rs = queryRS("SELECT * FROM generators WHERE blockX = ? AND blockY = ? AND blockZ = ?", loc.getX(), loc.getY(), loc.getZ());
            try {
                if (rs != null && rs.next()) {
                    if (requiredItem != null && requiredItem != playerItem) {
                        e.setCancelled(true);
                        return;
                    }
                    int id = rs.getInt("id");
                    String name = rs.getString("sysKey");
                    queryNRS("DELETE FROM generators WHERE id = ?", id);
                    e.setDropItems(false);
                    Generator gen = new Generator("idrop-g:" + name, name);
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), gen.getItem());
                }
                if (rs != null) {
                    rs = queryRS("SELECT * FROM generators WHERE blockX = ? AND blockY = ? AND blockZ = ?", loc.getX(), loc.getY() - 1, loc.getZ());
                    if (rs != null && rs.next()) {
                        String key = rs.getString("sysKey");
                        String path = "generators." + key + ".generate.";
                        Map<String, Material> blocks = new HashMap<>();
                        Map<String, Double> chances = new HashMap<>();
                        for (String generated : gens.getConfigurationSection(path).getKeys(false)) {
                            String fpath = path + generated + ".";
                            try {
                                Material mat = Miscellaneous.tryToGetMaterial(gens.getString(fpath + "block"));
                                blocks.put(generated, mat);
                            } catch (IllegalArgumentException ex) {
                                e.getPlayer().sendMessage("§c[iDrop] Check console for errors.");
                                String epath = fpath + "block";
                                Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this item. Here's info:" +
                                        "File: " + gens.getFile().getName() +
                                        "Path: " + epath.replaceAll("\\.", " -> "));
                                return;
                            }
                            chances.put(generated, gens.getDouble(fpath + "chance"));
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                ResultSet rs = queryRS("SELECT * FROM generators WHERE blockX = ? AND blockY = ? AND blockZ = ?", loc.getX(), loc.getY() - 1, loc.getZ());
                                try {
                                    if (rs != null && rs.next()) {
                                        generate(chances, blocks, e.getBlock().getLocation().add(0, -1, 0));
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }.runTaskLater(iDrop.getPlugin(iDrop.class), (long) gens.getDouble("generators." + key + ".delay") * 20);
                    } else {
                        rs.close();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void generate(Map<String, Double> chances, Map<String, Material> blocks, Location loc) {
        List<Material> airs = Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);
        if (airs.contains(loc.add(0, 1, 0).getBlock().getType())) {
            List<Material> mats = new ArrayList<>(blocks.values());
            List<Double> chance = new ArrayList<>(chances.values());
            Material mat = draw(mats, chance);
            loc.getBlock().setType(mat);
        }
    }

    private Material draw(List<Material> mats, List<Double> chances) {
        if (mats.size() != chances.size()) {
            throw new IllegalArgumentException("Items and probabilities must be of the same size");
        }

        double totalWeight = 0;
        for (double weight : chances) {
            totalWeight += weight;
        }

        double randomValue = Math.random() * totalWeight;

        double cumulativeWeight = 0.0;
        for (int i = 0; i < mats.size(); i++) {
            cumulativeWeight += chances.get(i);
            if (randomValue <= cumulativeWeight) {
                return mats.get(i);
            }
        }
        return mats.get(mats.size() - 1);
    }

}