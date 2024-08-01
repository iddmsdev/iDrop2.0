package pl.iddmsdev.idrop.drops.megadrop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MegaDrop {

    private static ConfigFile cfg = iDrop.megadropYML;
    private static Map<String, Integer> megadropTimer = new HashMap<>();
    private static boolean running = false;

    public static double getModifiedChance(String drop, String dropType, double current) {
        return (cfg.getBoolean("enabled")) ? current + cfg.getDouble(dropType + "." + drop) : current;
    }

    public static boolean hasPlayerMegaDrop(Player player) {
        if(cfg.getBoolean("enabled")) {
            if (megadropTimer.get(player.getUniqueId().toString()) == null) return false;
            return megadropTimer.get(player.getUniqueId().toString()) > 0;
        } else {
            return false;
        }
    }

    public static int getPlayerTimer(Player player) {
        return (cfg.getBoolean("enabled")) ? megadropTimer.get(player.getUniqueId().toString()) : 0;
    }

    public static void setPlayerTimer(Player player, int time) {
        if(cfg.getBoolean("enabled")) megadropTimer.put(player.getUniqueId().toString(), time);
        else megadropTimer.put(player.getUniqueId().toString(), 0);
    }

    public static void addPlayer(Player player) {
        megadropTimer.put(player.getUniqueId().toString(), 0);
    }

    public static Set<Map.Entry<String, Integer>> getEntrySet() {
        return megadropTimer.entrySet();
    }

    public static void start(iDrop plugin) {
        if(cfg.getBoolean("enabled")) {
            if (!running) {
                running = true;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Map.Entry<String, Integer> entry : megadropTimer.entrySet()) {
                            if (entry.getValue() > 0) {
                                megadropTimer.put(entry.getKey(), entry.getValue() - 1);
                            }
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L);
            }
        }
    }

}
