package pl.iddmsdev.idrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.iddmsdev.idrop.iDrop;

public class Disable implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage("§ciDrop disabled!");
        Bukkit.getServer().getPluginManager().disablePlugin(iDrop.getPlugin(iDrop.class));
        return false;
    }
}
