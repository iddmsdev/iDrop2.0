package pl.iddmsdev.idrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class ReloadCommand extends iDropCommandExtension {

    private static final ConfigFile commandsYML = iDrop.commandsYML;
    private static final ConfigFile messagesYML = iDrop.messagesYML;

    public ReloadCommand(String systemName, String label, String permission, List<String> aliases) {
        super(systemName, label, permission, aliases);
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        if (sender.hasPermission("idrop.reload")) {
            iDrop.reloadConfigs();
            sender.sendMessage(messagesYML.getString("reloaded"));
        } else {
            sender.sendMessage(messagesYML.getString("no-permission"));
        }
        return true;
    }


    @Override
    public String getUsage() {
        return "Usage: /idrop reload";
    }
}
