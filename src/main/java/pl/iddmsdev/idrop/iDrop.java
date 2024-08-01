package pl.iddmsdev.idrop;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
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
import pl.iddmsdev.idrop.utils.ConfigFile;

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


    public static ConfigFile blocksYML;
    public static ConfigFile mobsYML;
    public static ConfigFile generatorsYML;
    public static ConfigFile genGuiYML;
    public static ConfigFile genRecipesYML;
    public static ConfigFile fortuneYML;
    public static ConfigFile megadropYML;
    public static ConfigFile dropGuiYML;
    public static ConfigFile commandsYML;
    public static ConfigFile messagesYML;
    private static ConfigFile megadropTimersYML;

    public static boolean blockDroppingDirectlyToInv;
    public static boolean mobDroppingDirectlyToInv;
    public static boolean expDroppingDirectlyToPlayer;

    // todo:
    // - on all commands replace Player p = (Player) sender; to this + check if sender is not player
    // - remove all hardcoded messages (except logs)
    // - reloady


    // NS - Null-Safe

    @Override
    public void onEnable() {
        System.out.println("iDrop has just loaded ABV: 12");
        System.out.println("iDrop enabled. I wish you a lot of diamonds!");
        dataFolder = this.getDataFolder();
        setupConfigFiles();
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
            megadropTimersYML.save(megadropTimersYML.getFile());
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
                        Bukkit.getLogger().log(Level.SEVERE, "Cannot get player timer for some reasons. (iDrop, ln:121) \n" +
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
                ItemStack is = new Generator("idrop-g:" + id, id).getItem();
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
            commandMap.register(this.getName(), new iDropCommand(commandsYML.getRawString("idrop.label")));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot register main commands. Report it to developer!");
        }
        getCommand("idrop-dis").setExecutor(new Disable());

        // IDROP EXTENSIONS

        ConfigFile cfg = commandsYML;

        // if valid value in config is set:
        iDropCommandExtension helpCmd = new HelpCommand("idrop:helpcmd", cfg.getRawString("help.label"), "all");
        iDropCommand.setHelpCommand(helpCmd);
        iDropCommand.registerExtension(helpCmd);
        // end of if

        if(generatorsYML.getBoolean("enabled")) iDropCommand.registerExtension(new GeneratorCommand("idrop:gencmd", cfg.getRawString("generators.label"), "idrop.generators.user"));
        if(megadropYML.getBoolean("enabled")) iDropCommand.registerExtension(new MegaDropCommand("idrop:mdcmd", cfg.getRawString("megadrop.label"), "idrop.megadrop.user"));
        iDropCommand.registerExtension(new DropGUICommand("idrop:dguicmd", cfg.getRawString("gui.label"), "idrop.gui"));
        iDropCommand.registerExtension(new ReloadCommand("idrop:reload", cfg.getRawString("reload.label"), "idrop.reload", cfg.getRawStringList("reload.aliases")));
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getPluginManager().registerEvents(new iDropGuiInterpreter(null, null, null), this);

        if(blocksYML.getBoolean("enabled")) Bukkit.getPluginManager().registerEvents(new BlockDrop(), this);
        if(mobsYML.getBoolean("enabled")) Bukkit.getPluginManager().registerEvents(new MobDrop(), this);

        if(generatorsYML.getBoolean("enabled")) Bukkit.getPluginManager().registerEvents(new GeneratorBlocks(), this);
    }

    // sf -> setupFiles
    private void setupConfigFiles() {
        blocksYML = new ConfigFile(this, "dropconfig/blocks.yml");
        mobsYML = new ConfigFile(this, "dropconfig/mobs.yml");
        generatorsYML = new ConfigFile(this, "generators/generators.yml");
        genGuiYML = new ConfigFile(this, "generators/gen-gui.yml");
        genRecipesYML = new ConfigFile(this, "generators/gen-recipes.yml");
        fortuneYML = new ConfigFile(this, "dropconfig/fortune.yml");
        dropGuiYML = new ConfigFile(this, "dropconfig/drops-gui.yml");
        megadropYML = new ConfigFile(this, "dropconfig/megadrop.yml");
        commandsYML = new ConfigFile(this, "commands.yml");
        messagesYML = new ConfigFile(this, "messages.yml");
        megadropTimersYML = new ConfigFile(this, "data/megadrop-timers.yml");
    }
    public static void reloadConfigs() {
        blocksYML.reload();
        mobsYML.reload();
        generatorsYML.reload();
        genGuiYML.reload();
        genRecipesYML.reload();
        fortuneYML.reload();
        megadropYML.reload();
        commandsYML.reload();
        messagesYML.reload();
        dropGuiYML.reload();
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
