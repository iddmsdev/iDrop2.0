package pl.iddmsdev.idrop.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class ConfigFile {
    private final File file;
    private FileConfiguration cfg;
    public ConfigFile(JavaPlugin plugin, String path) {
        this.file = new File(plugin.getDataFolder(), path);
        createIfDoesntExist(file, plugin, path);
    }

    private void createIfDoesntExist(File file, Plugin plg, String path) {
        if(!file.exists()) plg.saveResource(path, false);
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        try {
            this.cfg.load(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "[iDrop] There's a problem with reload of " + file.getName() + " file. Maybe it's missing? If not, try to delete this file and reload plugin.");
        } catch (InvalidConfigurationException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "[iDrop] There's a problem with reload of " + file.getName() + " file. It has invalid configuration. Check for syntax errors!");
        }
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getCfg() {
        return cfg;
    }

    public boolean getBoolean(String path) {
        try {
            return cfg.getBoolean(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid boolean in " + file.getName() + ". Path: " + path);
            return false;
        }
    }
    public void set(String path, Object value) {
        cfg.set(path, value);
    }

    public void save(File file) {
        try {
            cfg.save(file);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean contains(String path) {
        return cfg.contains(path);
    }

    public Object get(String path) {
        try {
            return cfg.get(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid path: " + path + " in " + file.getName());
            return "Error.";
        }
    }
    public ConfigurationSection getConfigurationSection(String path) {
        try {
          return cfg.getConfigurationSection(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's no section: " + path + " in " + file.getName());
            return null;
        }
    }
    public String getRawString(String path) {
        try {
            return cfg.getString(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid string in " + file.getName() + ". Path: " + path);
            return "iDrop-error";
        }
    }

    public String getString(String path) {
        try {
            return colorize(cfg.getString(path));
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid string in " + file.getName() + ". Path: " + path);
            return "Error.";
        }
    }

    public List<String> getRawStringList(String path) {
        try {
            return cfg.getStringList(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid string list in " + file.getName() + ". Path: " + path);
            return Collections.singletonList("Error.");
        }
    }

    public List<String> getStringList(String path) {
        try {
            List<String> returnable = new ArrayList<>();
            for(String str : cfg.getStringList(path)) {
                returnable.add(colorize(str));
            }
            return returnable;
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid string list in " + file.getName() + ". Path: " + path);
            return Collections.singletonList("Error.");
        }
    }

    public boolean isList(String path) {
        try {
            return cfg.isList(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid path: " + path + " in " + file.getName());
            return false;
        }
    }
    public boolean isString(String path) {
        try {
            return cfg.isString(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid path: " + path + " in " + file.getName());
            return false;
        }
    }

    public int getInt(String path) {
        try {
            return cfg.getInt(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid int in " + file.getName() + ". Path: " + path);
            return 0;
        }
    }

    @SuppressWarnings("unused")
    public List<Integer> getIntegerList(String path) {
        try {
            return cfg.getIntegerList(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid int list in " + file.getName() + ". Path: " + path);
            return Collections.singletonList(0);
        }
    }

    public double getDouble(String path) {
        try {
            return cfg.getDouble(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid double in " + file.getName() + ". Path: " + path);
            return 0d;
        }
    }
    public List<Double> getDoubleList(String path) {
        try {
            return cfg.getDoubleList(path);
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing or invalid double list in " + file.getName() + ". Path: " + path);
            return Collections.singletonList(0d);
        }
    }

    public String colorize(String msg) {
        try {
            if(msg!=null) return ChatColor.translateAlternateColorCodes('&', msg);
            else return "";
        } catch(NullPointerException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[iDrop] There's a missing string or list in " + file.getName() + ". Check this file for missing elements.");
            return "§cError.";
        }
    }
}
