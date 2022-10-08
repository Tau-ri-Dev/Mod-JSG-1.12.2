package tauri.dev.jsg.stargate;

import java.util.HashMap;
import java.util.Map;

public enum EnumIrisState {
        OPENED((byte) 0),
        CLOSED((byte) 1),
        OPENING((byte) 2),
        CLOSING((byte) 3),
        ERROR((byte) -1);

    public final byte id;
    EnumIrisState(byte id) {
        this.id = id;
    }

    public static final Map<Byte, EnumIrisState> EnumIrisStateMap = new HashMap<>();

    static {
        for (EnumIrisState value : EnumIrisState.values()) {
            EnumIrisStateMap.put(value.id, value);
        }
    }

    public static EnumIrisState getValue(byte id) {
        EnumIrisState value = EnumIrisStateMap.get(id);
        if(value == null) return EnumIrisState.ERROR;
        return value;
    }
}
