package tauri.dev.jsg.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;


@SideOnly(Side.CLIENT)
public class EventTickClient {

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.world != null) {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player != null) {
                    JSG.lastPlayerPosInWorld = player.getPosition();
                }
            }
        }
    }
}
