package pl.iddmsdev.idrop.commands;

import org.bukkit.command.CommandSender;

public class HelpCommand extends iDropCommandExtension {

    public HelpCommand(String systemName, String label) {
        super(systemName, label);
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        sender.sendMessage("helpcmd");
        return true;
    }
}
