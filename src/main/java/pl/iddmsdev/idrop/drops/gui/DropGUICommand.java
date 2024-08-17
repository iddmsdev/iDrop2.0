package pl.iddmsdev.idrop.drops.gui;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.iddmsdev.idrop.commands.iDropCommandExtension;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;

public class DropGUICommand extends iDropCommandExtension {
    private static final ConfigFile cfg = iDrop.commandsYML;
    private static final ConfigFile msg = iDrop.messagesYML;

    public DropGUICommand(String systemName, String label, String permission) {
        super(systemName, label, permission, cfg.getStringList("gui.aliases"));
    }

    @Override
    public String getUsage() {
        return cfg.getString("usages.gui");
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            DropGUI dg = new DropGUI(p);
            dg.openGUI();
        } else {
            sender.sendMessage(msg.getString("must-be-a-player"));
        }
        return true;
    }

}
