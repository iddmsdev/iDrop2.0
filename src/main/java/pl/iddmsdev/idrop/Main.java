package pl.iddmsdev.idrop;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.iddmsdev.idrop.drops.BlockDrop;
import pl.iddmsdev.idrop.drops.MobDrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Main extends JavaPlugin {

    public static File dataFolder;

    public static File blocks;
    public static FileConfiguration blocksYML;

    public static File config;
    public static FileConfiguration configYML;

    public static boolean blockDroppingDirectlyToInv;
    public static boolean mobDroppingDirectlyToInv;
    public static boolean expDroppingDirectlyToPlayer;

    @Override
    public void onEnable() {
        System.out.println("iDrop has just loaded ABV: 1");
        System.out.println("iDrop enabled. I wish you a lot of diamonds!");
        dataFolder = this.getDataFolder();
        setupConfigFiles();
        setupListeners();

        blockDroppingDirectlyToInv = configYML.getBoolean("block-dropping-directly");
        mobDroppingDirectlyToInv = configYML.getBoolean("mob-dropping-directly");
        expDroppingDirectlyToPlayer = configYML.getBoolean("exp-dropping-directly");
    }

    @Override
    public void onDisable() {
        System.out.println("iDrop disabled. Bye!");
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new BlockDrop(), this);
        Bukkit.getPluginManager().registerEvents(new MobDrop(), this);
    }

    private void setupConfigFiles() {
        blocks = new File(dataFolder, "dropconfig/blocks.yml");
        blocksYML = YamlConfiguration.loadConfiguration(blocks);
        if(!blocks.exists()) {
            saveResource("dropconfig/blocks.yml", false);
        }
        config = new File(dataFolder, "config.yml");
        configYML = YamlConfiguration.loadConfiguration(config);
        if(!config.exists()) {
            saveResource("config.yml", false);
        }

    }

}
