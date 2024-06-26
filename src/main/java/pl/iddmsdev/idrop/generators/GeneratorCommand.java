package pl.iddmsdev.idrop.generators;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
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

    public GeneratorCommand(String systemName, String label) {
        super(systemName, label);
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        if(args.length==0) {
            openGUI((Player) sender, gens.getString("home-gui-name"));
        } else if(args.length == 1 && args[0].equalsIgnoreCase("admin")) {
            adminHelp(sender);
        } else if(args.length == 1 && !args[0].equalsIgnoreCase("menu") && !args[0].equalsIgnoreCase("recipes") && !args[0].equalsIgnoreCase("list")) {
            userHelp(sender);
        } else if(args[0].equalsIgnoreCase("admin")) {
            if(args[1].equalsIgnoreCase("get")) {
                if(args.length == 3) {
                    if(args[2].equalsIgnoreCase("all")) {
                        List<Generator> generatorsToGive = new ArrayList<>();
                        for(String key : gens.getConfigurationSection("generators").getKeys(false)) {
                            Generator gen = new Generator("idrop-g:"+key, gens, key);
                            generatorsToGive.add(gen);
                        }
                        for(Generator gen : generatorsToGive) {
                            Player p = (Player) sender;
                            Location loc = p.getLocation();
                            p.getWorld().dropItemNaturally(loc, gen.getItem());
                            p.sendMessage("§aDropped generator: '" + gen.getSystemName() + "'");
                        }
                    } else {
                        for(String key : gens.getConfigurationSection("generators").getKeys(false)) {
                            if(key.equalsIgnoreCase(args[2])) {
                                Generator gen = new Generator("idrop-g:"+key, gens, key);
                                Player p = (Player) sender;
                                Location loc = p.getLocation();
                                p.getWorld().dropItemNaturally(loc, gen.getItem());
                                p.sendMessage("§aDropped generator: '" + gen.getSystemName() + "'");
                                return true;
                            }
                        }
                        sender.sendMessage("§cGenerator '" + args[2] + "' does not exists!");
                        // todo: usage
                    }
                }
            } else {
                adminHelp(sender);
            }
            return true;
        } else if(args[0].equalsIgnoreCase("menu")) {
            openGUI((Player) sender, gens.getString("home-gui-name"));
        } else if(args[0].equalsIgnoreCase("recipes")) {
            if(gens.isList("recipes-display")) {
                for(String str : gens.getStringList("recipes-display")) {
                    sender.sendMessage(colorize(str));
                }
            } else if(gens.isString("recipes-display")) {
                openGUI((Player) sender, gens.getString("recipes-display"));
            }
        } else if(args[0].equalsIgnoreCase("list")) {
            if(gens.isList("list-display")) {
                for(String str : gens.getStringList("list-display")) {
                    sender.sendMessage(colorize(str));
                }
            } else if(gens.isString("recipes-display")) {
                openGUI((Player) sender, gens.getString("list-display"));
            }
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
