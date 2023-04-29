package tauri.dev.jsg.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tauri.dev.jsg.item.armor.AncientShield;

import java.util.List;

@Mod.EventBusSubscriber
public class EntityHitEvent {
    @SubscribeEvent
    public static void onHit(LivingDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.world.isRemote) return;
            List<ItemStack> stacks = player.inventory.armorInventory;
            for (ItemStack s : stacks) {
                if (s.getItem() instanceof AncientShield) {
                    ((AncientShield) s.getItem()).shieldHit(s, player);
                }
            }
        }
    }
}
