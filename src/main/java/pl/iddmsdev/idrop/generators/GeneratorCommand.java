package pl.iddmsdev.idrop.generators;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.iddmsdev.idrop.GUIs.iDropGuiInterpreter;
import pl.iddmsdev.idrop.commands.iDropCommandExtension;
import pl.iddmsdev.idrop.iDrop;

import java.util.ArrayList;
import java.util.List;

public class GeneratorCommand extends iDropCommandExtension {

    FileConfiguration gens = iDrop.generatorsYML;
    FileConfiguration gengui = iDrop.genGuiYML;

    // Todo:
    // - STRUCTURE (-D = default/don't need args, -DH = if not args, print help):
    // USERS: /idrop generators [menu-D/recipes/list]
    // ADMIN: /idrop generators <admin-DH> ([get] -> [genid/all])

    private static final FileConfiguration cfg = iDrop.commandsYML;

    public GeneratorCommand(String systemName, String label, String permission) {
        super(systemName, label, permission, cfg.getStringList("generators.aliases"));
    }

    @Override
    public String getUsage() {
        return colorize(cfg.getString("usages.generators"));
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        String prefix = "generators-";
        String adminL = prefix+"admin.label";
        String adminA = prefix+"admin.aliases";
        String menuL = prefix+"menu.label";
        String menuA = prefix+"menu.aliases";
        String recipesL = prefix+"recipes.label";
        String recipesA = prefix+"recipes.aliases";
        String listL = prefix+"list.label";
        String listA = prefix+"list.aliases";
        String adminGetL = prefix+"admin-get.label";
        String adminGetA = prefix+"admin-get.aliases";
        String adminGetAllL = prefix+"admin-get-all.label";
        String adminGetAllA = prefix+"admin-get-all.aliases";
        String thisDefault = cfg.getString("defaults.generators");
        if (args.length == 0) {
            if(thisDefault.equalsIgnoreCase("help")) userHelp(sender);
            else if(thisDefault.equalsIgnoreCase("usage")) return false;
            else openGUI((Player) sender, gens.getString("home-gui-name"));
            return true;
        } else if (args.length == 1 && isValidArguments(adminL, adminA, args[0])) {
            if (sender.hasPermission("idrop.generators.admin")) {
                adminHelp(sender);
                return true;
            } else {
                sender.sendMessage("§cNo permission!");
                return true;
            }
        } else if (args.length == 1 && !isValidArguments(menuL, menuA, args[0]) && !isValidArguments(recipesL, recipesA, args[0]) && !isValidArguments(listL, listA, args[0])) {
            userHelp(sender);
            return true;
        } else if (isValidArguments(adminL, adminA, args[0])) {
            if (sender.hasPermission("idrop.generators.admin")) {
                if (isValidArguments(adminGetL, adminGetA, args[1])) {
                    if (args.length == 3) {
                        if (isValidArguments(adminGetAllL, adminGetAllA, args[2])) {
                            List<Generator> generatorsToGive = new ArrayList<>();
                            for (String key : gens.getConfigurationSection("generators").getKeys(false)) {
                                Generator gen = new Generator("idrop-g:" + key, gens, key);
                                generatorsToGive.add(gen);
                            }
                            for (Generator gen : generatorsToGive) {
                                Player p = (Player) sender;
                                Location loc = p.getLocation();
                                p.getWorld().dropItemNaturally(loc, gen.getItem());
                                p.sendMessage("§aDropped generator: '" + gen.getSystemName() + "'");
                            }
                        } else {
                            for (String key : gens.getConfigurationSection("generators").getKeys(false)) {
                                if (key.equalsIgnoreCase(args[2])) {
                                    Generator gen = new Generator("idrop-g:" + key, gens, key);
                                    Player p = (Player) sender;
                                    Location loc = p.getLocation();
                                    p.getWorld().dropItemNaturally(loc, gen.getItem());
                                    p.sendMessage("§aDropped generator: '" + gen.getSystemName() + "'");
                                    return true;
                                }
                            }
                            sender.sendMessage("§cGenerator '" + args[2] + "' does not exists!");
                        }
                    } else {
                        sender.sendMessage("§cNo permission!");
                    }
                }
            } else {
                if (sender.hasPermission("idrop.generators.admin")) adminHelp(sender);
                else sender.sendMessage("§cNo permission!");
            }
            return true;
        } else if (isValidArguments(menuL, menuA, args[0])) {
            if(sender.hasPermission("idrop.generators.menu")) {
                openGUI((Player) sender, gens.getString("home-gui-name"));
            } else sender.sendMessage("§cNo permission!");
            return true;
        } else if (isValidArguments(recipesL, recipesA, args[0])) {
            if(sender.hasPermission("idrop.generators.recipes")) {
                if (gens.isList("recipes-display")) {
                    for (String str : gens.getStringList("recipes-display")) {
                        sender.sendMessage(colorize(str));
                    }
                } else if (gens.isString("recipes-display")) {
                    openGUI((Player) sender, gens.getString("recipes-display"));
                }
            } else sender.sendMessage("§cNo permission!");
            return true;
        } else if (isValidArguments(listL, listA, args[0])) {
            if(sender.hasPermission("idrop.generators.list")) {
                if (gens.isList("list-display")) {
                    for (String str : gens.getStringList("list-display")) {
                        sender.sendMessage(colorize(str));
                    }
                } else if (gens.isString("list-display")) {
                    openGUI((Player) sender, gens.getString("list-display"));
                }
            } else sender.sendMessage("§cNo permission!");
            return true;
        }
        return false;
    }

    private void openGUI(Player p, String gui) {
        iDropGuiInterpreter interpreter = new iDropGuiInterpreter(gengui, "guis", "variables");
        p.openInventory(interpreter.compile(gui));
    }

    private void userHelp(CommandSender p) {
        p.sendMessage("§aGenerators help");
        p.sendMessage("§7/idrop generators §8- §aDisplays this message");
        p.sendMessage("§7/idrop generators menu §8- §aDisplays main generators menu");
        p.sendMessage("§7/idrop generators list §8- §aDisplays list of generators");
        p.sendMessage("§7/idrop generators recipes §8- §aDisplays recipes menu");
    }

    private void adminHelp(CommandSender p) {
        p.sendMessage("§aGenerators §7- §cADMIN");
        p.sendMessage("§7/idrop generators admin §8- §aDisplays this message");
        p.sendMessage("§7/idrop generators admin get <all/generator-id> §8- §aDrops specified generator(s) at your location");
    }


    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }


}
