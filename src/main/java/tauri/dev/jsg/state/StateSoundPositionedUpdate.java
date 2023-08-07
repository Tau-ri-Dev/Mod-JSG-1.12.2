package tauri.dev.jsg.state;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.sound.SoundPositionedEnum;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class StateSoundPositionedUpdate extends State {

    public Map<Integer, Boolean> soundMap = new HashMap<>();

    public StateSoundPositionedUpdate() {
        soundMap = new HashMap<>();
    }

    public StateSoundPositionedUpdate(@Nullable SoundPositionedEnum sound, boolean play) {
        if(sound == null) return;
        soundMap.put(sound.id, play);
    }

    public void add(@Nullable SoundPositionedEnum sound, boolean play){
        if(sound == null) return;
        soundMap.put(sound.id, play);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(soundMap.size());
        for(Map.Entry<Integer, Boolean> e : soundMap.entrySet()){
            buf.writeInt(e.getKey());
            buf.writeBoolean(e.getValue());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        soundMap = new HashMap<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            int id = buf.readInt();
            boolean play = buf.readBoolean();
            soundMap.put(id, play);
        }
    }
}
