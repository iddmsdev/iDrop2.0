package pl.iddmsdev.idrop.utils;

import org.bukkit.Material;

public class Miscellaneous {

    public static Material tryToGetMaterial(String material) {
        return Material.valueOf(material.toUpperCase());
    }

}
