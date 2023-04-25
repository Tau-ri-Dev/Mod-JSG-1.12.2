package tauri.dev.jsg.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
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
            Minecraft mc = Minecraft.getMinecraft();
            ServerData serverData = mc.getCurrentServerData();
            if (event.world != null) {
                EntityPlayer player = mc.player;
                if (player != null) {
                    JSG.lastPlayerPosInWorld = player.getPosition();
                }
            }
            else if(serverData != null){
                PlayerControllerMP playerController = mc.playerController;
                if (playerController != null) {
                    JSG.lastPlayerPosInWorld = playerController.currentBlock;
                }
            }
        }
    }
}
