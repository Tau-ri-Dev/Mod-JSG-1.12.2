package mrjake.aunis.sound;

import mrjake.aunis.config.AunisConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class AunisSoundHelperClient {

	private static final Map<BlockPos, Map<SoundPositionedEnum, PositionedSoundRecord>> positionedSoundRecordsMap = new HashMap<>();
	
	private static PositionedSoundRecord getSoundRecord(SoundPositionedEnum soundEnum, BlockPos pos) {
		return new PositionedSoundRecord(soundEnum.resourceLocation, soundEnum.soundCategory, soundEnum.volume * AunisConfig.avConfig.volume * 5f, 1.0f, soundEnum.repeat, 0, ISound.AttenuationType.LINEAR, pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f);
	}
	
	public static void playPositionedSoundClientSide(BlockPos pos, SoundPositionedEnum soundEnum, boolean play) {
		Map<SoundPositionedEnum, PositionedSoundRecord> soundRecordsMap = positionedSoundRecordsMap.computeIfAbsent(pos, k -> new HashMap<>());

		PositionedSoundRecord soundRecord = soundRecordsMap.get(soundEnum);
		
		if (soundRecord == null) {
			soundRecord = getSoundRecord(soundEnum, pos);
			soundRecordsMap.put(soundEnum, soundRecord);
		}
			
		SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
		
		if (play) {
			if (!soundHandler.isSoundPlaying(soundRecord)) {
				try {
					soundHandler.playSound(soundRecord);
				}
				catch (Exception ignored) {}
			}
		}
		
		else
			Minecraft.getMinecraft().getSoundHandler().stopSound(soundRecord);
	}

	public static void stopAllSounds(){
		Minecraft.getMinecraft().getSoundHandler().stopSounds();
	}
}
