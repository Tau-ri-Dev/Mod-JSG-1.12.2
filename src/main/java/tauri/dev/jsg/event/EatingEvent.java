package tauri.dev.jsg.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tauri.dev.jsg.item.JSGItems;

import java.util.List;

@Mod.EventBusSubscriber
public class EatingEvent {

    /**
     * Disable eating when using shield
     */
    @SubscribeEvent
    public static void onEatingFinished(LivingEntityUseItemEvent.Start event) {
        ItemStack itemStack = event.getItem();
        if (itemStack.getItem() instanceof ItemFood) {
            //if (event.getDuration() < (itemStack.getMaxItemUseDuration() - 2)) return;
            EntityLivingBase entity = event.getEntityLiving();
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                List<ItemStack> i = player.inventory.armorInventory;
                for (ItemStack s : i) {
                    if (s.getItem() == JSGItems.SHIELD_EMITTER) {
                        //player.dropItem(false);
                        event.setCanceled(true);
                        //event.setDuration(0);
                        return;
                    }
                }
            }
        }
    }
}
