package pl.iddmsdev.idrop.drops.megadrop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.iddmsdev.idrop.commands.iDropCommandExtension;
import pl.iddmsdev.idrop.iDrop;

public class MegaDropCommand extends iDropCommandExtension {

    private static final FileConfiguration cfg = iDrop.commandsYML;

    public MegaDropCommand(String systemName, String label, String permission) {
        super(systemName, label, permission, cfg.getStringList("megadrop.aliases"));
    }

    @Override
    public String getUsage() {
        return colorize(cfg.getString("usages.megadrop"));
    }

    // commands:
    // /idrop megadrop give <player> <time> - give a player megadrop for x time
    // /idrop megadrop - check time
    // /idrop megadrop check <player> - check player time

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        System.out.println("dsa");
        if(sender instanceof Player) {
            System.out.println("asd");
            String prefix = "megadrop-";
            String checkL = prefix+"check.label";
            String checkA = prefix+"check.aliases";
            String giveL = prefix+"give.label";
            String giveA = prefix+"give.aliases";
            Player p = (Player) sender;
            if(args.length>0) {
                if (isValidArguments(giveL, giveA, args[0]) && p.hasPermission("idrop.megadrop.give") && args.length==3) {
                    if(Bukkit.getPlayer(args[1])!=null) {
                        Player t = Bukkit.getPlayer(args[1]);
                        try {
                            if(Integer.parseInt(args[2])>0) {
                                MegaDrop.setPlayerTimer(t, MegaDrop.getPlayerTimer(t) + Integer.parseInt(args[2]));
                                p.sendMessage("§bThis player has now §d" + MegaDrop.getPlayerTimer(t) + "s §bof megadrop!");
                                t.sendMessage("§bYou received §d" + Integer.parseInt(args[2]) + "s §bof megadrop!");
                            }
                            else p.sendMessage("§cTime must be greater than zero!");
                        } catch(NumberFormatException ex) {
                            p.sendMessage("§cYou must type numbers (not too long) in field 'time'!");
                        }
                    } else {
                        p.sendMessage("§cThere's no player: §7" + args[1]);
                    }
                    return true;
                } else if(isValidArguments(checkL, checkA, args[0]) && p.hasPermission("idrop.megadrop.check") && args.length==2) {
                    if(Bukkit.getPlayer(args[1])!=null) {
                        Player t = Bukkit.getPlayer(args[1]);
                        p.sendMessage("§d" + t.getName() + "§b have megadrop remaining time: §d" + MegaDrop.getPlayerTimer(t));
                    } else {
                        p.sendMessage("§cThere's no player: §7" + args[1]);
                    }
                } else {
                    return false;
                }
                return true;
            } else {
                p.sendMessage("§bYour remaining megadrop time: §d" + MegaDrop.getPlayerTimer(p) + "s");
                return true;
            }
        }
        return false;
    }

    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
