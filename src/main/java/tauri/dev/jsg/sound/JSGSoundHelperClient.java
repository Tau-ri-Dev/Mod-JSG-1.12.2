package tauri.dev.jsg.sound;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class JSGSoundHelperClient {

    private static final Map<BlockPos, Map<SoundPositionedEnum, JSGPositionedSound>> positionedSoundRecordsMap = new HashMap<>();

    public static void playPositionedSoundClientSide(BlockPos pos, SoundPositionedEnum soundEnum, boolean play) {
        Map<SoundPositionedEnum, JSGPositionedSound> soundRecordsMap = positionedSoundRecordsMap.computeIfAbsent(pos, k -> new HashMap<>());

        JSGPositionedSound soundRecord = soundRecordsMap.get(soundEnum);

        if (soundRecord == null) {
            soundRecord = JSGPositionedSound.getSoundRecord(soundEnum, pos);
            soundRecordsMap.put(soundEnum, soundRecord);
            positionedSoundRecordsMap.put(pos, soundRecordsMap);
        }

        if (play)
            soundRecord.play();
        else
            soundRecord.stop();
    }
}
