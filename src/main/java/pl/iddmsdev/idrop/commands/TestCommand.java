package pl.iddmsdev.idrop.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.iddmsdev.idrop.GUIs.iDropGuiInterpreter;
import pl.iddmsdev.idrop.iDrop;

public class TestCommand extends iDropCommandExtension {


    FileConfiguration cfg = iDrop.genGuiYML;

    @Override
    public boolean handler(CommandSender sender, String[] args) {
        iDropGuiInterpreter gi = new iDropGuiInterpreter(cfg, "guis", "variables");
//        Inventory inv = gi.compileAll().get(0) nie tera <-
        Inventory inv = gi.compile("home-gui");
        Player p = (Player) sender;
        p.openInventory(inv);
        return true;
    }

    public TestCommand(String systemName, String label) {
        super(systemName, label);
    }

}
