package pl.iddmsdev.idrop.generators;

import pl.iddmsdev.idrop.iDrop;

import java.io.File;
import java.sql.*;

public class GeneratorsDB {
    // TODO: add mysql support (all is SQLite now)

    public static Connection liteCon;

    public static void connect() {
        File db = new File(iDrop.getPlugin(iDrop.class).getDataFolder(), "generators.db");
        if (!db.exists()) {
            new File(iDrop.getPlugin(iDrop.class).getDataFolder().getPath()).mkdir();
        }
        String URL = "jdbc:sqlite:" + db;
        try {
            Class.forName("org.sqlite.JDBC");
            liteCon = DriverManager.getConnection(URL);
            createTable();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void createTable() {
        PreparedStatement ps = null;
        try {
            ps = liteCon.prepareStatement("CREATE TABLE IF NOT EXISTS generators(id INTEGER PRIMARY KEY AUTOINCREMENT, sysKey VARCHAR(50), blockX INTEGER, blockY INTEGER, blockZ INTEGER)");
            ps.execute();
            ps.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
    // nrs - Not returnable statement
    // rs - Returnable statement (mostly SELECT)
    public static void queryNRS(String statement, Object... params) {
        PreparedStatement ps = null;
        try {
            ps = GeneratorsDB.liteCon.prepareStatement(statement);
            for(int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (int) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (double) param);
                } else if (param instanceof Boolean) {
                    ps.setBoolean(i + 1, (boolean) param);
                }
            }
            ps.executeUpdate();
            ps.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
    public static ResultSet queryRS(String statement, Object... params) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = liteCon.prepareStatement(statement);
            for(int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (int) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (double) param);
                } else if (param instanceof Boolean) {
                    ps.setBoolean(i + 1, (boolean) param);
                }
            }
            rs = ps.executeQuery();
            return rs;
        } catch(SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}