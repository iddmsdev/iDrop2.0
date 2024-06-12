package pl.iddmsdev.idrop.commands;

import org.bukkit.command.CommandSender;

public abstract class iDropCommandExtension {
    private String systemName; // System name used only for iDrop and iDrop extensions
    private String label; // Name of an argument, for example: /idrop yourlabel

    public abstract boolean handler(CommandSender sender, String[] args);

    public String getSystemName() {
        return systemName;
    }

    public String getLabel() {
        return label;
    }

    public iDropCommandExtension(String systemName, String label) {
        this.systemName = systemName;
        this.label = label;
    }
}
