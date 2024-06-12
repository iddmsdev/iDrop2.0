package pl.iddmsdev.idrop.generators;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.iddmsdev.idrop.commands.iDropCommandExtension;
import pl.iddmsdev.idrop.iDrop;

import java.util.ArrayList;
import java.util.List;

public class GeneratorCommand extends iDropCommandExtension {

    FileConfiguration gens = iDrop.generatorsYML;

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
            // todo: help
        } else if(args.length == 1 && args[0].equalsIgnoreCase("admin")) {
            // todo: admin help
        } else if(args.length == 1) {
            // todo: user help of specified command
        } else if(args[0].equalsIgnoreCase("admin")) {
            System.out.println(1);
            if(args[1].equalsIgnoreCase("get")) {
                System.out.println(2);
                if(args.length == 3) {
                    System.out.println(3);
                    if(args[2].equalsIgnoreCase("all")) {
                        System.out.println(4);
                        List<Generator> generatorsToGive = new ArrayList<>();
                        for(String key : gens.getConfigurationSection("generators").getKeys(false)) {
                            System.out.println("6 -> " + key);
                            Generator gen = new Generator("idrop-g:"+key, gens, key);
                            generatorsToGive.add(gen);
                        }
                        for(Generator gen : generatorsToGive) {
                            System.out.println("5 -> " + gen.getSystemName());
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
                // todo: admin help
            }
            return true;
        } else if(args[0].equalsIgnoreCase("menu")) {
            // todo: menu
        } else if(args[0].equalsIgnoreCase("recipes")) {
            // todo: recipes
        } else if(args[0].equalsIgnoreCase("list")) {
            // todo: list
        }
        return false;
    }

    private void userHelp() {
        // todo
    }
}
