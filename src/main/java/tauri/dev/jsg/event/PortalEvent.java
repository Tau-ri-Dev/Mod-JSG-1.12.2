package tauri.dev.jsg.event;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.JSGMinecraftHelper;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class PortalEvent {

    public static final Map<Integer, Entity> ENTITIES_IGNORED = new HashMap<>();

    @SubscribeEvent
    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
        Entity e = event.getEntity();

        // Pass stargate teleport
        if (ENTITIES_IGNORED.containsKey(e.getEntityId())) {
            ENTITIES_IGNORED.remove(e.getEntityId());
            return;
        }

        JSGConfig.General.Events events = JSGConfig.General.events;

        if (!events.allowEndPortals && JSGMinecraftHelper.isEntityCollidingWithBlock(e, Blocks.END_PORTAL.getDefaultState())) {
            event.setCanceled(true);
        } else if (!events.allowNetherPortals && JSGMinecraftHelper.isEntityCollidingWithBlock(e, Blocks.PORTAL.getDefaultState())) {
            event.setCanceled(true);
        }
    }
}
