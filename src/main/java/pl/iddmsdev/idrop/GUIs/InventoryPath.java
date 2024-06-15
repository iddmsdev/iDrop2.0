package pl.iddmsdev.idrop.GUIs;

import org.bukkit.configuration.file.FileConfiguration;

public class InventoryPath {
    private final FileConfiguration cfg;
    private final String fpath;
    private final String variables;
    private final String actionData;
    private final String variable;

    public String getVariables() {
        return variables;
    }

    public String getActionData() {
        return actionData;
    }

    public FileConfiguration getConfig() {
        return cfg;
    }

    public String getFullPath() {
        return fpath;
    }
    public String getVariable() {
        return variable;
    }

    public InventoryPath(FileConfiguration cfg, String fpath, String variables, String actionData, String variable) {
        this.cfg = cfg;
        this.fpath = fpath;
        this.variables = variables;
        this.actionData = actionData;
        this.variable = variable;
    }
}
