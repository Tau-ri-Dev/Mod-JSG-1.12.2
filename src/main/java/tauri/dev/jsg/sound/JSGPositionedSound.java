package tauri.dev.jsg.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.config.JSGConfig;

@SideOnly(Side.CLIENT)
public class JSGPositionedSound extends PositionedSoundRecord {
    public static JSGPositionedSound getSoundRecord(SoundPositionedEnum soundEnum, BlockPos pos) {
        return new JSGPositionedSound(soundEnum.resourceLocation, soundEnum.soundCategory, soundEnum.volume * JSGConfig.General.audio.volume * 5f, 1.0f, soundEnum.repeat, 0, ISound.AttenuationType.LINEAR, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
    }

    private JSGPositionedSound(ResourceLocation soundId, SoundCategory categoryIn, float volumeIn, float pitchIn, boolean repeatIn, int repeatDelayIn, AttenuationType attenuationTypeIn, float xIn, float yIn, float zIn) {
        super(soundId, categoryIn, volumeIn, pitchIn, repeatIn, repeatDelayIn, attenuationTypeIn, xIn, yIn, zIn);
    }

    private SoundHandler getHandler() {
        return FMLClientHandler.instance().getClient().getSoundHandler();
    }

    public void stop() {
        getHandler().stopSound(this);
    }

    public boolean isPlaying(){
        return getHandler().isSoundPlaying(this);
    }

    public void play() {
        if (!isPlaying()) {
            getHandler().playSound(this);
        }
    }

}
