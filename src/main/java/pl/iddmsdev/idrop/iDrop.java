package pl.iddmsdev.idrop;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.iddmsdev.idrop.commands.*;
import pl.iddmsdev.idrop.drops.BlockDrop;
import pl.iddmsdev.idrop.drops.MobDrop;
import pl.iddmsdev.idrop.generators.GeneratorBlockRegister;
import pl.iddmsdev.idrop.generators.GeneratorCommand;
import pl.iddmsdev.idrop.generators.Generators;
import pl.iddmsdev.idrop.generators.GeneratorsDB;

import java.io.File;

public final class iDrop extends JavaPlugin {

    public static File dataFolder;

    public static File blocks;
    public static FileConfiguration blocksYML;

    public static File config;
    public static FileConfiguration configYML;

    public static File mobs;
    public static FileConfiguration mobsYML;
    public static File generators;
    public static FileConfiguration generatorsYML;

    public static boolean blockDroppingDirectlyToInv;
    public static boolean mobDroppingDirectlyToInv;
    public static boolean expDroppingDirectlyToPlayer;

    // todo:
    // - on all commands replace Player p = (Player) sender; to this + check if sender is not player
    // - remove all hardcoded messages (except logs)

    @Override
    public void onEnable() {
        System.out.println("iDrop has just loaded ABV: 5");
        System.out.println("iDrop enabled. I wish you a lot of diamonds!");
        dataFolder = this.getDataFolder();
        setupConfigFiles();
        setupListeners();
        setupCommands();
        setupDBs();

        Generators.compileGenerators();

        blockDroppingDirectlyToInv = configYML.getBoolean("block-dropping-directly");
        mobDroppingDirectlyToInv = configYML.getBoolean("mob-dropping-directly");
        expDroppingDirectlyToPlayer = configYML.getBoolean("exp-dropping-directly");
    }

    @Override
    public void onDisable() {
        System.out.println("iDrop disabled. Bye!");
    }

    private void setupDBs() {
        GeneratorsDB.connect();
    }

    private void setupCommands() {
        getCommand("idrop").setExecutor(new iDropCommand());
        getCommand("idrop-dis").setExecutor(new Disable());

        // IDROP EXTENSIONS

        // if valid value in config is set:
        iDropCommandExtension helpCmd = new HelpCommand("idrop:helpcmd", "help");
        iDropCommand.setHelpCommand(helpCmd);
        iDropCommand.registerExtension(helpCmd);
        // end of if

        iDropCommand.registerExtension(new TestCommand("idrop:testcmd", "test"));
        iDropCommand.registerExtension(new GeneratorCommand("idrop:gencmd", "generators"));
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new BlockDrop(), this);
        Bukkit.getPluginManager().registerEvents(new MobDrop(), this);

        Bukkit.getPluginManager().registerEvents(new GeneratorBlockRegister(), this);
    }

    private void setupConfigFiles() {
        blocks = new File(dataFolder, "dropconfig/blocks.yml");
        blocksYML = YamlConfiguration.loadConfiguration(blocks);
        if(!blocks.exists()) {
            saveResource("dropconfig/blocks.yml", false);
        }
        mobs = new File(dataFolder, "dropconfig/mobs.yml");
        mobsYML = YamlConfiguration.loadConfiguration(mobs);
        if(!mobs.exists()) {
            saveResource("dropconfig/mobs.yml", false);
        }
        config = new File(dataFolder, "config.yml");
        configYML = YamlConfiguration.loadConfiguration(config);
        if(!config.exists()) {
            saveResource("config.yml", false);
        }
        generators = new File(dataFolder, "generators/generators.yml");
        generatorsYML = YamlConfiguration.loadConfiguration(generators);
        if(!generators.exists()) {
            saveResource("generators/generators.yml", false);
        }

    }

}
