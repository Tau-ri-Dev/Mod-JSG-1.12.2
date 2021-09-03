package mrjake.aunis.stargate;

import java.util.HashMap;
import java.util.Map;

public enum EnumIrisStates {
        OPENED((byte) 0),
        CLOSED((byte) 1),
        OPENING((byte) 2),
        CLOSING((byte) 3),
        ERROR((byte) -1);

    public final byte id;
    EnumIrisStates(byte id) {
        this.id = id;
    }

    public static final Map<Byte, EnumIrisStates> enumIrisStatesMap = new HashMap<>();

    static {
        for (EnumIrisStates value : EnumIrisStates.values()) {
            enumIrisStatesMap.put(value.id, value);
        }
    }

    public static EnumIrisStates getValue(byte id) {
        EnumIrisStates value = enumIrisStatesMap.get(id);
        if(value == null) return EnumIrisStates.ERROR;
        return value;
    }
}
