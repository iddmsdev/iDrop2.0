package pl.iddmsdev.idrop.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import pl.iddmsdev.idrop.iDrop;

import java.util.Arrays;

public class TestCommand extends iDropCommandExtension {


    FileConfiguration cfg = iDrop.generatorsYML;

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        sender.sendMessage(cfg.getString("generators.stone.base-block"));
        return true;
    }

    public TestCommand(String systemName, String label) {
        super(systemName, label);
    }

}
