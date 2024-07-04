package pl.iddmsdev.idrop.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import pl.iddmsdev.idrop.iDrop;

public class HelpCommand extends iDropCommandExtension {

    private static final FileConfiguration cfg = iDrop.commandsYML;

    public HelpCommand(String systemName, String label, String permission) {
        super(systemName, label, permission, cfg.getStringList("help.aliases"));
    }

    @Override
    public String getUsage() {
        return "§cUsage: §7/idrop help";
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        sender.sendMessage("helpcmd");
        return true;
    }
}
