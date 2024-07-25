package pl.iddmsdev.idrop.drops;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.iddmsdev.idrop.iDrop;

import java.util.Random;

public class Fortune {

    // TODO: Mob fortune

    private final static FileConfiguration cfg = iDrop.fortuneYML;

    public static double modifyChance(String drop, String type, int level, double current) {
        if (cfg.getBoolean("enabled")) {
            String fortunePath = type + "." + drop + ".chances";
            double addition = cfg.getDoubleList(fortunePath).get(level - 1);
            return current + addition;
        }
        return current;
    }

    public static int modifyAmount(String drop, String type, int level, int current) {
        if(cfg.getBoolean("enabled")) {
            String fortunePath = type + "." + drop + ".additional-drops";
            String[] additionArray = cfg.getStringList(fortunePath).get(level - 1).split(" : ");
            int min = Integer.parseInt(additionArray[0]);
            int max = Integer.parseInt(additionArray[1]);
            if(max-min!=0) {
                int addition = new Random().nextInt(max - min) + min;
                return current + addition;
            } else {
                return current;
            }
        }
        return current;
    }
    public static boolean hasFortuneModifier(String drop, String type) {
        return cfg.contains(type + "." + drop);
    }
}
