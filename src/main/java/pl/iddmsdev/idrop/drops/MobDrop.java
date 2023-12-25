package pl.iddmsdev.idrop.drops;

import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDrop implements Listener {
    public void onMobDeath(EntityDeathEvent e) {
        if(e.getEntity().getKiller()!=null) {
            if(!e.getEntityType().equals(EntityType.PLAYER)) {

            }
        }
    }
}
