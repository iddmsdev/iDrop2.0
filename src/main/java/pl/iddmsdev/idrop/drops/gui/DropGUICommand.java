package pl.iddmsdev.idrop.drops.gui;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.iddmsdev.idrop.commands.iDropCommandExtension;

public class DropGUICommand extends iDropCommandExtension {
    public DropGUICommand(String systemName, String label) {
        super(systemName, label);
    }

    @Override
    public boolean handler(CommandSender sender, String[] args) {

        return false;
    }
}
