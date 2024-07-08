package pl.iddmsdev.idrop;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.iddmsdev.idrop.GUIs.actions.OpenAnotherGUI;
import pl.iddmsdev.idrop.GUIs.actions.OpenGeneratorRecipeGUI;
import pl.iddmsdev.idrop.GUIs.actions.SendChatMessage;
import pl.iddmsdev.idrop.GUIs.iDropGuiInterpreter;
import pl.iddmsdev.idrop.commands.*;
import pl.iddmsdev.idrop.drops.BlockDrop;
import pl.iddmsdev.idrop.drops.gui.DropGUICommand;
import pl.iddmsdev.idrop.drops.megadrop.MegaDrop;
import pl.iddmsdev.idrop.drops.MobDrop;
import pl.iddmsdev.idrop.drops.megadrop.MegaDropCommand;
import pl.iddmsdev.idrop.generators.Generator;
import pl.iddmsdev.idrop.generators.GeneratorBlocks;
import pl.iddmsdev.idrop.generators.GeneratorCommand;
import pl.iddmsdev.idrop.generators.GeneratorsDB;
import pl.iddmsdev.idrop.generators.recipes.Recipe;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class iDrop extends JavaPlugin implements Listener {

    public static List<Recipe> generatorRecipes = new ArrayList<>();

    public static File dataFolder;

    public static File blocks;
//    public static File config;
    public static File mobs;
    public static File generators;
    public static File genGUI;
    public static File genRecipes;
    public static File fortune;
    public static File megadrop;
    public static File dropGUI;
    public static File commands;
    public static File messages;
    private static File megadropTimers;


    public static FileConfiguration blocksYML;
//    public static FileConfiguration configYML;
    public static FileConfiguration mobsYML;
    public static FileConfiguration generatorsYML;
    public static FileConfiguration genGuiYML;
    public static FileConfiguration genRecipesYML;
    public static FileConfiguration fortuneYML;
    public static FileConfiguration megadropYML;
    public static FileConfiguration dropGuiYML;
    public static FileConfiguration commandsYML;
    public static FileConfiguration messagesYML;
    private static FileConfiguration megadropTimersYML;

    public static boolean blockDroppingDirectlyToInv;
    public static boolean mobDroppingDirectlyToInv;
    public static boolean expDroppingDirectlyToPlayer;

    // todo:
    // - on all commands replace Player p = (Player) sender; to this + check if sender is not player
    // - remove all hardcoded messages (except logs)
    // - reloady

    @Override
    public void onEnable() {
        System.out.println("iDrop has just loaded ABV: 10");
        System.out.println("iDrop enabled. I wish you a lot of diamonds!");
        dataFolder = this.getDataFolder();
        setupConfigFiles(true, this);
        setupListeners();
        setupCommands();
        setupDBs();
        setupGUIActions();
        setupRecipes();
        setupMegadrop();

        blockDroppingDirectlyToInv = false;
        mobDroppingDirectlyToInv = false;
        expDroppingDirectlyToPlayer = true;
    }

    @Override
    public void onDisable() {
        if(megadropYML.getBoolean("enabled")) {
            for (Map.Entry<String, Integer> entry : MegaDrop.getEntrySet()) {
                if (entry.getValue() > 0) {
                    megadropTimersYML.set(entry.getKey(), entry.getValue());
                }
            }
            try {
                megadropTimersYML.save(megadropTimers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Bukkit.getLogger().log(Level.INFO, "Megadrop timers saved.");
        }
        System.out.println("iDrop disabled. Bye!");
    }

    private void setupDBs() {
        if(generatorsYML.getBoolean("enabled")) {
            GeneratorsDB.connect();
        }
    }

    private void setupMegadrop() {
        if(megadropYML.getBoolean("enabled")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (megadropTimersYML.contains(p.getUniqueId().toString())) {
                    try {
                        //noinspection DataFlowIssue
                        MegaDrop.setPlayerTimer(p, (int) megadropTimersYML.get(p.getUniqueId().toString()));
                        continue;
                    } catch (Exception ex) {
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
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(megadropYML.getBoolean("enabled")) {
            if (!MegaDrop.hasPlayerMegaDrop(e.getPlayer())) {
                MegaDrop.addPlayer(e.getPlayer());
            }
        }
    }

    private void setupRecipes() {
        if(generatorsYML.getBoolean("enabled")) {
            for (String key : genRecipesYML.getConfigurationSection("recipes").getKeys(false)) {
                String id = genRecipesYML.getString("recipes." + key + ".result");
                ItemStack is = new Generator("idrop-g:" + id, generatorsYML, id).getItem();
                NamespacedKey namespacedKey = new NamespacedKey(this, "idrop-gen-recipe." + key);
                Recipe rec = new Recipe(namespacedKey, is, key);
                rec.assignToPlugin();
                generatorRecipes.add(rec);
                getLogger().log(Level.INFO, "Registered new generator crafting: " + key);
            }
        }
    }

    private void setupGUIActions() {
        iDropGuiInterpreter.registerAction(new SendChatMessage());
        iDropGuiInterpreter.registerAction(new OpenAnotherGUI());
        iDropGuiInterpreter.registerAction(new OpenGeneratorRecipeGUI());
    }

    private void setupCommands() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(getServer());
            commandMap.register(this.getName(), new iDropCommand(commandsYML.getString("idrop.label")));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot register main commands. Report it to developer!");
        }
        getCommand("idrop-dis").setExecutor(new Disable());

        // IDROP EXTENSIONS

        FileConfiguration cfg = commandsYML;

        // if valid value in config is set:
        iDropCommandExtension helpCmd = new HelpCommand("idrop:helpcmd", cfg.getString("help.label"), "all");
        iDropCommand.setHelpCommand(helpCmd);
        iDropCommand.registerExtension(helpCmd);
        // end of if

        if(generatorsYML.getBoolean("enabled")) iDropCommand.registerExtension(new GeneratorCommand("idrop:gencmd", "generators", "idrop.generators.user"));
        if(megadropYML.getBoolean("enabled")) iDropCommand.registerExtension(new MegaDropCommand("idrop:mdcmd", cfg.getString("megadrop.label"), "idrop.megadrop.user"));
        iDropCommand.registerExtension(new DropGUICommand("idrop:dguicmd", cfg.getString("gui.label"), "idrop.gui"));
        iDropCommand.registerExtension(new ReloadCommand("idrop:reload", cfg.getString("reload.label"), "idrop.reload", cfg.getStringList("reload.aliases")));
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getPluginManager().registerEvents(new iDropGuiInterpreter(null, null, null), this);

        if(blocksYML.getBoolean("enabled")) Bukkit.getPluginManager().registerEvents(new BlockDrop(), this);
        if(mobsYML.getBoolean("enabled")) Bukkit.getPluginManager().registerEvents(new MobDrop(), this);

        if(generatorsYML.getBoolean("enabled")) Bukkit.getPluginManager().registerEvents(new GeneratorBlocks(), this);
    }

    // sf -> setupFiles
    public static void setupConfigFiles(boolean sf, Plugin p) {
        // zrobic cos z tym
        if(sf) {
            blocks = new File(dataFolder, "dropconfig/blocks.yml");
            if (!blocks.exists()) p.saveResource("dropconfig/blocks.yml", false);
            mobs = new File(dataFolder, "dropconfig/mobs.yml");
            if (!mobs.exists()) p.saveResource("dropconfig/mobs.yml", false);
            generators = new File(dataFolder, "generators/generators.yml");
            if (!generators.exists()) p.saveResource("generators/generators.yml", false);
            genGUI = new File(dataFolder, "generators/gen-gui.yml");
            if (!genGUI.exists()) p.saveResource("generators/gen-gui.yml", false);
            genRecipes = new File(dataFolder, "generators/gen-recipes.yml");
            if (!genRecipes.exists()) p.saveResource("generators/gen-recipes.yml", false);
            fortune = new File(dataFolder, "dropconfig/fortune.yml");
            if (!fortune.exists()) p.saveResource("dropconfig/fortune.yml", false);
            megadrop = new File(dataFolder, "dropconfig/megadrop.yml");
            if (!megadrop.exists()) p.saveResource("dropconfig/megadrop.yml", false);
            megadropTimers = new File(dataFolder, "data/megadrop-timers.yml");
            if (!megadropTimers.exists()) p.saveResource("data/megadrop-timers.yml", false);
            dropGUI = new File(dataFolder, "dropconfig/drops-gui.yml");
            if (!dropGUI.exists()) p.saveResource("dropconfig/drops-gui.yml", false);
            commands = new File(dataFolder, "commands.yml");
            if (!commands.exists()) p.saveResource("commands.yml", false);
            messages = new File(dataFolder, "messages.yml");
            if (!messages.exists()) p.saveResource("messages.yml", false);
        }

        blocksYML = YamlConfiguration.loadConfiguration(blocks);
        mobsYML = YamlConfiguration.loadConfiguration(mobs);
        generatorsYML = YamlConfiguration.loadConfiguration(generators);
        genGuiYML = YamlConfiguration.loadConfiguration(genGUI);
        genRecipesYML = YamlConfiguration.loadConfiguration(genRecipes);
        fortuneYML = YamlConfiguration.loadConfiguration(fortune);
        megadropYML = YamlConfiguration.loadConfiguration(megadrop);
        megadropTimersYML = YamlConfiguration.loadConfiguration(megadropTimers);
        dropGuiYML = YamlConfiguration.loadConfiguration(dropGUI);
        commandsYML = YamlConfiguration.loadConfiguration(commands);
        messagesYML = YamlConfiguration.loadConfiguration(messages);
    }
    public static Recipe getRecipeByID(String identifier) {
        if(generatorsYML.getBoolean("enabled")) {
            Recipe recipe = null;
            for (Recipe rec : generatorRecipes) {
                if (rec.getRecipeIdentifier().equals(identifier)) {
                    recipe = rec;
                }
            }
            return recipe;
        } return null;
    }

}
