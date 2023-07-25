package tauri.dev.jsg.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tauri.dev.jsg.util.CreativeItemsChecker;

@Mod.EventBusSubscriber
public class CreativeItemsBypassEvent {
    @SubscribeEvent
    public static void onPickUp(EntityItemPickupEvent event) {
        ItemStack i = event.getItem().getItem();
        if (!CreativeItemsChecker.canInteractWith(i, event.getEntityPlayer().isCreative())) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
        }
    }
}
