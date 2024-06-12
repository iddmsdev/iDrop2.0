package pl.iddmsdev.idrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class iDropCommand implements CommandExecutor {

    // TODO: add translations

    private static iDropCommandExtension helpCommand;

    private static final Set<iDropCommandExtension> extensions = new HashSet<>();
    public static void setHelpCommand(iDropCommandExtension cmd) {
        helpCommand = cmd;
    }

    // STRUCTURE: /idrop <extension_label> [extension_args]
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if(args.length == 0) { String[] helpArgs = {}; return helpCommand.handler(sender, helpArgs); }
        for(iDropCommandExtension ext : extensions) {
            if(args[0].equals(ext.getLabel())) {
                String[] newArgs = Arrays.stream(args).skip(1).toArray(String[]::new);
                return ext.handler(sender, newArgs);
            }
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean registerExtension(iDropCommandExtension extension) {
        for(iDropCommandExtension ext : extensions) {
            if(ext.getSystemName().equals(extension.getSystemName())) {
                Bukkit.getLogger().warning("[iDrop] Cannot register command extension: " + extension.getSystemName() + ", because it already exists!");
                return false;
            } else if(ext.getLabel().equals(extension.getLabel())) {
                Bukkit.getLogger().warning("[iDrop] Cannot register command extension: " + extension.getSystemName() + ", because label of this extensions is already registered!");
                return false;
            }
        }
        extensions.add(extension);
        Bukkit.getLogger().info("[iDrop] Registered new command extension: " + extension.getSystemName());
        return true;
    }

}
