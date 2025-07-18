package pl.iddmsdev.idrop.GUIs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.iddmsdev.idrop.iDrop;
import pl.iddmsdev.idrop.utils.ConfigFile;
import pl.iddmsdev.idrop.utils.Miscellaneous;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

//todo: item dependencies (e.g. guis.example.items.example-item.dependency) and dependency data (in plans used for automated recipes gui)

public class iDropGuiInterpreter implements Listener {

    static Set<GUIAction> actions = new HashSet<>();
    private static Map<Inventory, InventoryPath> registeredInventories = new HashMap<>();

    public static void registerAction(GUIAction action) {
        actions.add(action);
    }

    // IT'S VERY IMPORTANT! YOU CAN CREATE MULTIPLE GUI CONFIG FILES, BUT THE THEME MUST BE THE SAME AS IN OTHERS!!

    private final ConfigFile a; // gui config yml
    private final String b; // GUIs path (def: guis)
    private final String c; // Variables path (def: variables)
    private final String d; // GUI title path (def: guis-path.title)
    private final String e; // GUI rows path (1-6) (def: guis-path.rows)
    private final String f; // GUI items path (def: guis-path.items)
    private final String g; // GUI item material path (def: guis-path.items.item-path.item)
    private final String h; // GUI item name path (def: guis-path.items.item-path.name)
    private final String i; // GUI item lore path (def: guis-path.items.item-path.lore)
    private final String j; // GUI item action path (def: guis-path.items.item-path.action)
    private final String k; // GUI item as variable path (def: guis-path.items.item-path.var)
    private final String l; // GUI item amount path (def: guis-path.items.item-path.amount)
    private final String m; // GUI item action data path (def: guis-path.items.item.item-path.action-data)

    public iDropGuiInterpreter(ConfigFile config, String GUIsPath, String variablesPath) {
        this.a = config;
        this.b = GUIsPath;
        this.c = variablesPath;
        this.d = "title";
        this.e = "rows";
        this.f = "items";
        this.g = "item";
        this.h = "name";
        this.i = "lore";
        this.j = "action";
        this.k = "var";
        this.l = "amount";
        this.m = "action-data";
    }

    public iDropGuiInterpreter(ConfigFile config, String GUIsPath, String variablesPath, String GUITitlePath, String GUIRowsPath, String GUIItemsPath, String itemMaterialPath, String itemNamePath, String itemLorePath, String itemActionPath, String itemVariablePath, String itemAmountPath, String itemActionDataPath) {
        this.a = config;
        this.b = GUIsPath;
        this.c = variablesPath;
        this.d = GUITitlePath;
        this.e = GUIRowsPath;
        this.f = GUIItemsPath;
        this.g = itemMaterialPath;
        this.h = itemNamePath;
        this.i = itemLorePath;
        this.j = itemActionPath;
        this.k = itemVariablePath;
        this.l = itemAmountPath;
        this.m = itemActionDataPath;
    }

    // todo: handle errors
    public Inventory compile(String GUIName) {
        try {
            String fpath = b + "." + GUIName + ".";
            String title = a.getString(fpath + d);
            int size = a.getInt(fpath + e) * 9;
            Inventory returnable = Bukkit.createInventory(null, size, title);
            List<ItemStack> items = compileItems(compileVariables(), GUIName);
            int i = 0;
            for (ItemStack item : items) {
                returnable.setItem(i, item);
                i++;
            }
            InventoryPath invPath = new InventoryPath(a, fpath + f, c, m, k);
            registeredInventories.put(returnable, invPath);
            return returnable;
        } catch (Exception ex) {
            Bukkit.getLogger().severe("[iDrop] Here's GUI interpreter: Cannot compile GUI " + GUIName);
        }
        return null;
    }

    // sorted by index order
    protected Map<String, ItemStack> compileVariables() {
        Map<String, ItemStack> returnable = new HashMap<>();
        for (String key : a.getConfigurationSection(c).getKeys(false)) {
            String fpath = c + "." + key + ".";
            try {
                Material mat;
                try {
                    mat = Miscellaneous.tryToGetMaterial(a.getRawString(fpath + g));
                } catch(IllegalArgumentException ex) {
                    mat = Material.STONE;
                    String epath = fpath + g;
                    Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this item. Here's info:" +
                            "File: " + a.getFile().getName() +
                            "Path: " + epath.replaceAll("\\.", " -> "));
                }
                int amount = a.getInt(fpath + l);
                amount = (amount == 0) ? 1 : amount;
                ItemStack item = new ItemStack(mat, amount);
                ItemMeta im = item.getItemMeta();
                if (a.getString(fpath + h) != null) {
                    if (a.getString(fpath + h).equals("")) im.setDisplayName("§r");
                    else im.setDisplayName(a.getString(fpath + h));
                }
                im.setLore(a.getStringList(fpath + i));
                PersistentDataContainer pdc = im.getPersistentDataContainer();
                NamespacedKey nKey = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-gui-action");
                if (a.getString(fpath + j) != null) {
                    pdc.set(nKey, PersistentDataType.STRING, a.getString(fpath + j));
                } else {
                    pdc.set(nKey, PersistentDataType.STRING, "none");
                }
                item.setItemMeta(im);
                returnable.put(key, item);
            } catch (Exception ex) {
                Bukkit.getLogger().severe("[iDrop] Here's GUI interpreter: Cannot compile variables in " + fpath);
            }
        }
        return returnable;
    }

    @SuppressWarnings("DataFlowIssue")
    protected List<ItemStack> compileItems(Map<String, ItemStack> variables, String GUI) {
        List<ItemStack> returnable = new ArrayList<>();
        for (String key : a.getConfigurationSection(b + "." + GUI + "." + f).getKeys(false)) {
            String fpath = b + "." + GUI + "." + f + "." + key + ".";
            try {
                String var = a.getString(fpath + k);
                if (var != null && variables.containsKey(var)) {
                    returnable.add(variables.get(var));
                } else if (a.contains(fpath + k) && a.isString(fpath + k) && !variables.containsKey(var)) {
                    ItemStack item = new ItemStack(Material.STONE);
                    ItemMeta im = item.getItemMeta();
                    im.setDisplayName("Unrecognized variable: " + var);
                    im.setLore(Collections.singletonList("§f§rIf 'var' key is redundant remove it, 'var' keys are priority!"));
                    item.setItemMeta(im);
                    returnable.add(item);
                } else {
                    if (a.getString(fpath + g).equalsIgnoreCase("air")) {
                        returnable.add(new ItemStack(Material.AIR, 0));
                        continue;
                    }
                    Material mat;
                    try {
                         mat = Miscellaneous.tryToGetMaterial(a.getRawString(fpath + g));
                    } catch (IllegalArgumentException ex) {
                        String epath = fpath + g;
                        Bukkit.getLogger().log(Level.SEVERE, "[iDrop] Check for any errors with this item. Here's info:" +
                                "File: " + a.getFile().getName() +
                                "Path: " + epath.replaceAll("\\.", " -> "));
                        mat = Material.STONE;
                    }
                    int amount = a.getInt(fpath + l);
                    amount = (amount == 0) ? 1 : amount;
                    ItemStack item = new ItemStack(mat, amount);
                    ItemMeta im = item.getItemMeta();
                    if (a.getString(fpath + h) != null) {
                        if (a.getString(fpath + h).equals("")) im.setDisplayName("§r");
                        else im.setDisplayName(a.getString(fpath + h));
                    }
                    PersistentDataContainer pdc = im.getPersistentDataContainer();
                    NamespacedKey nKey = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-gui-action");
                    if (a.getString(fpath + j) != null) {
                        pdc.set(nKey, PersistentDataType.STRING, a.getString(fpath + j));
                    } else {
                        pdc.set(nKey, PersistentDataType.STRING, "none");
                    }
                    im.setLore(a.getStringList(fpath + i));
                    item.setItemMeta(im);
                    returnable.add(item);
                }
            } catch (Exception ex) {
                Bukkit.getLogger().severe("[iDrop] Here's GUI interpreter: Cannot compile items (GUI: " + GUI + ") in " + fpath);
                Bukkit.getLogger().log(Level.SEVERE, ex.getMessage());
            }
        }
        return returnable;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if (inv != null) {
            if (inv.getHolder() == null) {
                ItemStack item = e.getCurrentItem();
                if (item != null) {
                    PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-gui-action");
                    if (pdc.has(key, PersistentDataType.STRING)) {
                        String action = pdc.get(key, PersistentDataType.STRING);
                        if (action.equalsIgnoreCase("none")) {
                            e.setCancelled(true);
                            return;
                        }
                        for (GUIAction guiAction : actions) {
                            if (action.equalsIgnoreCase(guiAction.getLabel())) {
                                if (registeredInventories.containsKey(e.getClickedInventory())) {
                                    String GUIItemIndex;
                                    int itemIndex = 0;
                                    InventoryPath invPath = registeredInventories.get(inv);
                                    String itemsPath = invPath.getFullPath();
                                    String actionDataPath = "none";
                                    for (String path : invPath.getConfig()
                                            .getConfigurationSection(itemsPath).getKeys(false)) {
                                        if (itemIndex == e.getSlot()) {
                                            GUIItemIndex = path;
                                            if (invPath.getConfig().contains(itemsPath + "." + GUIItemIndex + "." + invPath.getVariable())) {
                                                String var = invPath.getConfig().getString(
                                                        itemsPath + "." + GUIItemIndex + "." + invPath.getVariable());
                                                if (invPath.getConfig().contains(invPath.getVariables() + "." + var)) {
                                                    actionDataPath = invPath.getVariables() + "." + var + "." + invPath.getActionData();
                                                    break;
                                                }
                                            }
                                            actionDataPath = itemsPath + "." + GUIItemIndex + "." + invPath.getActionData();
                                            break;
                                        }
                                        itemIndex++;
                                    }
                                    ConfigFile config = invPath.getConfig();
                                    guiAction.handler(e, actionDataPath, config);
                                }
                                return;
                            }
                        }
                    }
                } else e.setCancelled(true);
            }
        }
    }
}
