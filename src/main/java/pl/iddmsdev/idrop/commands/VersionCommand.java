package pl.iddmsdev.idrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;

import java.util.List;

public class VersionCommand extends iDropCommandExtension {

    public VersionCommand(String systemName, String label, String permission, List<String> aliases) {
        super(systemName, label, permission, aliases);
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        sender.sendMessage("§eYou're using iDrop: " + iDrop.getPlugin(iDrop.class).getDescription().getVersion() + " on " + Bukkit.getServer().getVersion());
        return true;
    }

    @Override
    public String getUsage() {
        return "§7i wonder how did you get here...";
    }
}
