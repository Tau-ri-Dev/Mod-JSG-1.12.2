package tauri.dev.jsg.event;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.sound.SoundPositionedEnum;

import java.util.Timer;
import java.util.TimerTask;

import static tauri.dev.jsg.sound.JSGSoundHelperClient.playPositionedSoundClientSide;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class SoundsLoadEvent {

    public static boolean isLoadingMusicPlaying = false;
    public static final BlockPos LOADING_MUSIC_BLOCK_POS = new BlockPos(0, 0, 0);

    @SubscribeEvent
    public static void onSoundHandlerLoad(SoundLoadEvent event) {
        if(!JSGConfig.General.mainMenuConfig.loadingMusic) return;
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (JSGConfig.General.mainMenuConfig.loadingMusic) {
                    playPositionedSoundClientSide(LOADING_MUSIC_BLOCK_POS, SoundPositionedEnum.LOADING_MUSIC, true);
                    isLoadingMusicPlaying = true;
                }
                t.cancel();
            }
        }, 5000, 5000);
    }
}
