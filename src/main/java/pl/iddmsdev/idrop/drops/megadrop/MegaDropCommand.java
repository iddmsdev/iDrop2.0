package pl.iddmsdev.idrop.drops.megadrop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.iddmsdev.idrop.commands.iDropCommandExtension;
import pl.iddmsdev.idrop.iDrop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MegaDropCommand extends iDropCommandExtension {

    private static final FileConfiguration cfg = iDrop.commandsYML;
    private static final FileConfiguration msg = iDrop.messagesYML;

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
        if(sender instanceof Player) {
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
                                String receivedTimeString = timeString(Integer.parseInt(args[2]));
                                MegaDrop.setPlayerTimer(t, MegaDrop.getPlayerTimer(t) + Integer.parseInt(args[2]));
                                String timeString = timeString(MegaDrop.getPlayerTimer(t));
                                p.sendMessage(colorize(msg.getString("this-player-received")
                                        .replaceAll("%player%", t.getName()).replaceAll("%time%", timeString)
                                        .replaceAll("%received%", receivedTimeString)));
                                t.sendMessage(colorize(msg.getString("you-received").
                                        replaceAll("%received%", receivedTimeString).replaceAll("%time%", timeString)));
                            }
                            else p.sendMessage(colorize(msg.getString("greater-than-zero-error")));
                        } catch(NumberFormatException ex) {
                            p.sendMessage(colorize(msg.getString("number-parsing-error")));
                        }
                    } else {
                        p.sendMessage(colorize(msg.getString("player-not-found-error").replaceAll("%player%", args[1])));
                    }
                    return true;
                } else if(isValidArguments(checkL, checkA, args[0]) && p.hasPermission("idrop.megadrop.check") && args.length==2) {
                    if(Bukkit.getPlayer(args[1])!=null) {
                        Player t = Bukkit.getPlayer(args[1]);
                        int time = MegaDrop.getPlayerTimer(t);
                        if(time>0) p.sendMessage(colorize(msg.getString("megadrop-check")
                                .replaceAll("%player%", t.getName()).replaceAll("%time%", timeString(time))));
                        else p.sendMessage(colorize(msg.getString("megadrop-check-no-time").
                                replaceAll("%player%", t.getName())));
                    } else {
                        p.sendMessage(colorize(msg.getString("player-not-found-error").replaceAll("%player%", args[1])));
                    }
                } else {
                    return false;
                }
                return true;
            } else {
                int time = MegaDrop.getPlayerTimer(p);
                if(time>0) {
                    p.sendMessage(colorize(msg.getString("megadrop-self-check").replaceAll("%time%", timeString(time))));
                }
                else p.sendMessage(colorize(msg.getString("megadrop-self-check-no-time")));
                return true;
            }
        } else {
            sender.sendMessage(colorize(msg.getString("must-be-a-player")));
            return true;
        }
    }

    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    private String timeString(int totalSeconds) {
        long days = totalSeconds / 86400;
        long remainingSeconds = totalSeconds % 86400;

        long hours = remainingSeconds / 3600;
        remainingSeconds = remainingSeconds % 3600;

        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;

        String secondsString = colorize(msg.getString("seconds"));
        String minutesString = colorize(msg.getString("minutes"));
        String hoursString = colorize(msg.getString("hours"));
        String daysString = colorize(msg.getString("days"));
        String separator = colorize(msg.getString("time-separator"));
        List<String> returnable = new ArrayList<>();
        if(days>0) returnable.add(days + daysString);
        if(hours>0) returnable.add(hours + hoursString);
        if(minutes>0) returnable.add(minutes + minutesString);
        if(seconds>0) returnable.add(seconds + secondsString);
        return String.join(separator, returnable);
    }
}
