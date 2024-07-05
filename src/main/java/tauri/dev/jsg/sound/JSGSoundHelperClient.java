package tauri.dev.jsg.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class JSGSoundHelperClient {

    private static final Map<BlockPos, Map<SoundPositionedEnum, JSGPositionedSound>> positionedSoundRecordsMap = new HashMap<>();

    public static JSGPositionedSound getRecord(SoundPositionedEnum soundEnum, BlockPos pos){
        Map<SoundPositionedEnum, JSGPositionedSound> soundRecordsMap = positionedSoundRecordsMap.computeIfAbsent(pos, k -> new HashMap<>());

        JSGPositionedSound soundRecord = soundRecordsMap.get(soundEnum);

        if (soundRecord == null) {
            soundRecord = JSGPositionedSound.getSoundRecord(soundEnum, pos);
            soundRecordsMap.put(soundEnum, soundRecord);
            positionedSoundRecordsMap.put(pos, soundRecordsMap);
        }

        return soundRecord;
    }

    public static void playSoundEventClientSide(BlockPos pos, SoundEventEnum sound, float volumeModifier, float pitchModifier) {
        Minecraft.getMinecraft().getSoundHandler().playSound(JSGPositionedSound.getSoundRecord(sound, pos, volumeModifier, pitchModifier));
    }

    public static void playPositionedSoundClientSide(BlockPos pos, SoundPositionedEnum soundEnum, boolean play) {
        JSGPositionedSound soundRecord = getRecord(soundEnum, pos);

        if (play)
            soundRecord.play();
        else
            soundRecord.stop();
    }
}
