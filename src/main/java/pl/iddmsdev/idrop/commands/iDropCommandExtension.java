package pl.iddmsdev.idrop.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class iDropCommandExtension {
    private final String systemName; // System name used only for iDrop and iDrop extensions
    private final String label; // Name of an argument, for example: /idrop yourlabel
    private final String permission;
    private List<String> aliases;

    public String getPermission() {
        return permission;
    }

    public abstract boolean handler(CommandSender sender, String[] args);

    public String getSystemName() {
        return systemName;
    }

    public String getLabel() {
        return label;
    }

    public iDropCommandExtension(String systemName, String label, String permission, List<String> aliases) {
        this.systemName = systemName;
        this.label = label;
        this.permission = permission;
        this.aliases = aliases;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public abstract String getUsage();

    protected boolean isValidArguments(String labelPath, String aliasesPath, String arg) {
        ConfigFile cfg = iDrop.commandsYML;
        if(cfg.isString(labelPath) && cfg.getString(labelPath).equalsIgnoreCase(arg)) {
            return true;
        } else if(cfg.isList(aliasesPath)) {
            return cfg.getStringList(aliasesPath).stream().anyMatch(item -> item.equalsIgnoreCase(arg)); // return containsIgnoreCase
        }
        return false;
    }
}
