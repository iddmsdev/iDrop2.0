package pl.iddmsdev.idrop;

import jdk.vm.ci.meta.Value;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pl.iddmsdev.idrop.GUIs.actions.OpenAnotherGUI;
import pl.iddmsdev.idrop.GUIs.actions.OpenGeneratorRecipeGUI;
import pl.iddmsdev.idrop.GUIs.actions.SendChatMessage;
import pl.iddmsdev.idrop.GUIs.iDropGuiInterpreter;
import pl.iddmsdev.idrop.commands.*;
import pl.iddmsdev.idrop.drops.BlockDrop;
import pl.iddmsdev.idrop.drops.megadrop.MegaDrop;
import pl.iddmsdev.idrop.drops.MobDrop;
import pl.iddmsdev.idrop.drops.megadrop.MegaDropCommand;
import pl.iddmsdev.idrop.generators.Generator;
import pl.iddmsdev.idrop.generators.GeneratorBlocks;
import pl.iddmsdev.idrop.generators.GeneratorCommand;
import pl.iddmsdev.idrop.generators.GeneratorsDB;
import pl.iddmsdev.idrop.generators.recipes.Recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class iDrop extends JavaPlugin implements Listener {

    public static List<Recipe> generatorRecipes = new ArrayList<>();

    public static File dataFolder;

    public static File blocks;
    public static File config;
    public static File mobs;
    public static File generators;
    public static File genGUI;
    public static File genRecipes;
    public static File fortune;
    public static File megadrop;
    private File megadropTimers;


    public static FileConfiguration blocksYML;
    public static FileConfiguration configYML;
    public static FileConfiguration mobsYML;
    public static FileConfiguration generatorsYML;
    public static FileConfiguration genGuiYML;
    public static FileConfiguration genRecipesYML;
    public static FileConfiguration fortuneYML;
    public static FileConfiguration megadropYML;
    private FileConfiguration megadropTimersYML;

    public static boolean blockDroppingDirectlyToInv;
    public static boolean mobDroppingDirectlyToInv;
    public static boolean expDroppingDirectlyToPlayer;

    // todo:
    // - on all commands replace Player p = (Player) sender; to this + check if sender is not player
    // - remove all hardcoded messages (except logs)

    @Override
    public void onEnable() {
        System.out.println("iDrop has just loaded ABV: 9");
        System.out.println("iDrop enabled. I wish you a lot of diamonds!");
        dataFolder = this.getDataFolder();
        setupConfigFiles();
        setupListeners();
        setupCommands();
        setupDBs();
        setupGUIActions();
        setupRecipes();
        setupMegadrop();

        blockDroppingDirectlyToInv = configYML.getBoolean("block-dropping-directly");
        mobDroppingDirectlyToInv = configYML.getBoolean("mob-dropping-directly");
        expDroppingDirectlyToPlayer = configYML.getBoolean("exp-dropping-directly");
    }

    @Override
    public void onDisable() {
        for(Map.Entry<String, Integer> entry : MegaDrop.getEntrySet()) {
            if(entry.getValue()>0) {
                megadropTimersYML.set(entry.getKey(), entry.getValue());
            }
        }
        try {
            megadropTimersYML.save(megadropTimers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getLogger().log(Level.INFO, "Megadrop timers saved.");
        System.out.println("iDrop disabled. Bye!");
    }

    private void setupDBs() {
        GeneratorsDB.connect();
    }

    private void setupMegadrop() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(megadropTimersYML.contains(p.getUniqueId().toString())) {
                try {
                    MegaDrop.setPlayerTimer(p, (int) megadropTimersYML.get(p.getUniqueId().toString()));
                    continue;
                } catch(Exception ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Cannot get player timer for some reasons. (iDrop, ln:106-107) \n" +
                            "Details: \n" +
                            "Player: " + p.getName() + ", UUID: " + p.getUniqueId() + "\n" +
                            "Contains: " + megadropTimersYML.contains(p.getUniqueId().toString()) + "\n" +
                            "Please report it to developer if this error is often.");
                }
            }
            MegaDrop.addPlayer(p);
        }
        MegaDrop.start(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(!MegaDrop.hasPlayerMegaDrop(e.getPlayer())) MegaDrop.addPlayer(e.getPlayer());
    }

    private void setupRecipes() {
        for(String key : genRecipesYML.getConfigurationSection("recipes").getKeys(false)) {
            String id = genRecipesYML.getString("recipes."+key+".result");
            ItemStack is = new Generator("idrop-g:"+id, generatorsYML, id).getItem();
            NamespacedKey namespacedKey = new NamespacedKey(this, "idrop-gen-recipe." + key);
            Recipe rec = new Recipe(namespacedKey, is, key);
            rec.assignToPlugin();
            generatorRecipes.add(rec);
            getLogger().log(Level.INFO, "Registered new generator crafting: " + key);
        }
    }

    private void setupGUIActions() {
        iDropGuiInterpreter.registerAction(new SendChatMessage());
        iDropGuiInterpreter.registerAction(new OpenAnotherGUI());
        iDropGuiInterpreter.registerAction(new OpenGeneratorRecipeGUI());
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
        iDropCommand.registerExtension(new MegaDropCommand("idrop:mdcmd", "megadrop"));
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new iDropGuiInterpreter(null, null, null), this);

        Bukkit.getPluginManager().registerEvents(new BlockDrop(), this);
        Bukkit.getPluginManager().registerEvents(new MobDrop(), this);

        Bukkit.getPluginManager().registerEvents(new GeneratorBlocks(), this);
    }

    private void setupConfigFiles() {
        // zrobic cos z tym
        blocks = new File(dataFolder, "dropconfig/blocks.yml");
        if(!blocks.exists()) {
            saveResource("dropconfig/blocks.yml", false);
        }
        mobs = new File(dataFolder, "dropconfig/mobs.yml");
        if(!mobs.exists()) {
            saveResource("dropconfig/mobs.yml", false);
        }
        config = new File(dataFolder, "config.yml");
        if(!config.exists()) {
            saveResource("config.yml", false);
        }
        generators = new File(dataFolder, "generators/generators.yml");
        if(!generators.exists()) {
            saveResource("generators/generators.yml", false);
        }
        genGUI = new File(dataFolder, "generators/gen-gui.yml");
        if(!genGUI.exists()) {
            saveResource("generators/gen-gui.yml", false);
        }
        genRecipes = new File(dataFolder, "generators/gen-recipes.yml");
        if(!genRecipes.exists()) {
            saveResource("generators/gen-recipes.yml", false);
        }
        fortune = new File(dataFolder, "dropconfig/fortune.yml");
        if(!fortune.exists()) saveResource("dropconfig/fortune.yml", false);
        megadrop = new File(dataFolder, "dropconfig/megadrop.yml");
        if(!megadrop.exists()) saveResource("dropconfig/megadrop.yml", false);
        megadropTimers = new File(dataFolder, "data/megadrop-timers.yml");
        if(!megadropTimers.exists()) saveResource("data/megadrop-timers.yml", false);

        blocksYML = YamlConfiguration.loadConfiguration(blocks);
        mobsYML = YamlConfiguration.loadConfiguration(mobs);
        configYML = YamlConfiguration.loadConfiguration(config);
        generatorsYML = YamlConfiguration.loadConfiguration(generators);
        genGuiYML = YamlConfiguration.loadConfiguration(genGUI);
        genRecipesYML = YamlConfiguration.loadConfiguration(genRecipes);
        fortuneYML = YamlConfiguration.loadConfiguration(fortune);
        megadropYML = YamlConfiguration.loadConfiguration(megadrop);
        megadropTimersYML = YamlConfiguration.loadConfiguration(megadropTimers);
    }
    public static Recipe getRecipeByID(String identifier) {
        Recipe recipe = null;
        for(Recipe rec : generatorRecipes) {
            if(rec.getRecipeIdentifier().equals(identifier)) {
                recipe = rec;
            }
        }
        return recipe;
    }

}
