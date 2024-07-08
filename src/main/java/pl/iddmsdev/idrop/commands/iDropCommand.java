package pl.iddmsdev.idrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.iddmsdev.idrop.drops.gui.DropGUI;
import pl.iddmsdev.idrop.iDrop;

import java.util.*;

public class iDropCommand extends BukkitCommand {

    // TODO: add translations

    private static iDropCommandExtension helpCommand;
    private static final FileConfiguration cfg = iDrop.commandsYML;
    private static final FileConfiguration msg = iDrop.messagesYML;

    private static final Set<iDropCommandExtension> extensions = new HashSet<>();

    public iDropCommand(String name) {
        super(name);
        this.description = "Main drop command.";
        if (cfg.contains("idrop.aliases") && cfg.isList("idrop.aliases") && !cfg.getStringList("idrop.aliases").isEmpty()) {
            this.setAliases(cfg.getStringList("idrop.aliases"));
        }
    }

    public static void setHelpCommand(iDropCommandExtension cmd) {
        helpCommand = cmd;
    }

    @Override
    public boolean execute(CommandSender sender, String cmdLabel, String[] args) {
        if (sender.hasPermission("idrop.command")) {
            if (args.length == 0) {
                return defaultHandler(sender);
            }
            for (iDropCommandExtension ext : extensions) {
                if (sender.hasPermission(ext.getPermission()) || ext.getPermission().equals("all")) {
                    boolean containsIgnoreCase = ext.getAliases().stream().anyMatch(item -> item.equalsIgnoreCase(args[0]));
                    if (args[0].equalsIgnoreCase(ext.getLabel()) || containsIgnoreCase) {
                        String[] newArgs = Arrays.stream(args).skip(1).toArray(String[]::new);
                        if (!ext.handler(sender, newArgs)) {
                            sender.sendMessage(ext.getUsage());
                        }
                        return true;
                    }
                } else {
                    sender.sendMessage(colorize(msg.getString("no-permission")));
                }
            }
            String[] hArgs = {};
            return helpCommand.handler(sender, hArgs);
        } else {
            sender.sendMessage(colorize(msg.getString("no-permission")));
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean registerExtension(iDropCommandExtension extension) {
        for (iDropCommandExtension ext : extensions) {
            if (ext.getSystemName().equals(extension.getSystemName())) {
                Bukkit.getLogger().warning("[iDrop] Cannot register command extension: " + extension.getSystemName() + ", because it already exists!");
                return false;
            } else if (ext.getLabel().equals(extension.getLabel())) {
                Bukkit.getLogger().warning("[iDrop] Cannot register command extension: " + extension.getSystemName() + ", because label of this extensions is already registered!");
                return false;
            }
        }
        extensions.add(extension);
        Bukkit.getLogger().info("[iDrop] Registered new command extension: " + extension.getSystemName());
        return true;
    }

    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private boolean defaultHandler(CommandSender sender) {
        String thisDefault = cfg.getString("defaults.idrop");
        if (thisDefault.equalsIgnoreCase("help")) {
            String[] args = {};
            return helpCommand.handler(sender, args);
        } else if (thisDefault.equalsIgnoreCase("gui")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                DropGUI dg = new DropGUI(p);
                dg.openGUI();
                return true;
            } else {
                sender.sendMessage(colorize(msg.getString("must-be-a-player")));
                return true;
            }
        } else if (thisDefault.equalsIgnoreCase("usage")) {
            sender.sendMessage(colorize(cfg.getString("usages.idrop")));
            return false;
        }
        return false;
    }

}
