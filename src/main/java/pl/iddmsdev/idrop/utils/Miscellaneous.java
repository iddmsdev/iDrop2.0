package pl.iddmsdev.idrop.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.File;
import java.util.logging.Level;

public class Miscellaneous {

    public static Material tryToGetMaterial(String material) {
        return Material.valueOf(material.toUpperCase());
    }

}
