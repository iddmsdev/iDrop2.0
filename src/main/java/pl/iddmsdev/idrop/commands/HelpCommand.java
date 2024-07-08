package pl.iddmsdev.idrop.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import pl.iddmsdev.idrop.iDrop;

public class HelpCommand extends iDropCommandExtension {

    private static final FileConfiguration cfg = iDrop.commandsYML;
    private static final FileConfiguration msg = iDrop.messagesYML;

    public HelpCommand(String systemName, String label, String permission) {
        super(systemName, label, permission, cfg.getStringList("help.aliases"));
    }

    @Override
    public String getUsage() {
        return "§cUsage: §7/idrop help";
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        for(String str : msg.getStringList("main-help")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
        }
        return true;
    }
}
