package pl.iddmsdev.idrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import pl.iddmsdev.idrop.iDrop;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ReloadCommand extends iDropCommandExtension {

    private static FileConfiguration commandsYML = iDrop.commandsYML;
    private static FileConfiguration messagesYML = iDrop.messagesYML;

    public ReloadCommand(String systemName, String label, String permission, List<String> aliases) {
        super(systemName, label, permission, aliases);
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        if(sender.hasPermission("idrop.reload")) {
            iDrop.reloadConfigs();
            sender.sendMessage(c(messagesYML.getString("reloaded")));
        } else {
            sender.sendMessage(c(messagesYML.getString("no-permission")));
        }
        return true;
    }

    private String c(String m) {
        return ChatColor.translateAlternateColorCodes('&', m);
    }

    @Override
    public String getUsage() {
        return "Usage: /idrop reload";
    }
}
