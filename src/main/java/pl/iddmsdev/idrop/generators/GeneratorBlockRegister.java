package pl.iddmsdev.idrop.generators;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.iddmsdev.idrop.iDrop;

import java.sql.ResultSet;
import java.sql.SQLException;

import static pl.iddmsdev.idrop.generators.GeneratorsDB.queryNRS;
import static pl.iddmsdev.idrop.generators.GeneratorsDB.queryRS;

public class GeneratorBlockRegister implements Listener {

    // todo: removing from db

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getItemInHand().hasItemMeta()) {
            NamespacedKey key = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-data");
            ItemMeta meta = e.getItemInHand().getItemMeta();
            if(meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                if (meta.getPersistentDataContainer().get(key, PersistentDataType.STRING).equals("generator")) {
                    NamespacedKey key2 = new NamespacedKey(iDrop.getPlugin(iDrop.class), "idrop-gen");
                    if (meta.getPersistentDataContainer().has(key2, PersistentDataType.STRING)) {
                        String genKey = meta.getPersistentDataContainer().get(key2, PersistentDataType.STRING);
                        Location loc = e.getBlock().getLocation();
                        GeneratorsDB.queryNRS("INSERT INTO generators(sysKey, blockX, blockY, blockZ) VALUES (?, ?, ?, ?)",
                                genKey, loc.getX(), loc.getY(), loc.getZ());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        ResultSet rs = queryRS("SELECT * FROM generators WHERE blockX = ? AND blockY = ? AND blockZ = ?", loc.getX(), loc.getY(), loc.getZ());
        try {
            if(rs != null && rs.next()) {
                int id = rs.getInt("id");
                System.out.println("ID: " + id);
                queryNRS("DELETE FROM generators WHERE id = ?", id);
            } if(rs != null) {
                rs.close();
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

}
