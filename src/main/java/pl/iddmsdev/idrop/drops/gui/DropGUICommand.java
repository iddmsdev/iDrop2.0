package pl.iddmsdev.idrop.drops.gui;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.iddmsdev.idrop.commands.iDropCommandExtension;
import pl.iddmsdev.idrop.iDrop;

public class DropGUICommand extends iDropCommandExtension {
    private static final FileConfiguration cfg = iDrop.commandsYML;
    public DropGUICommand(String systemName, String label, String permission) {
        super(systemName, label, permission, cfg.getStringList("gui.aliases"));
    }

    @Override
    public String getUsage() {
        return colorize(cfg.getString("usages.gui"));
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            DropGUI dg = new DropGUI(p);
            dg.openGUI();
            return true;
        }
        return false;
    }

    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
