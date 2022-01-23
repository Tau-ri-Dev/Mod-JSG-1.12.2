package mrjake.aunis.zpm;


public enum EnumZPMState {
    DOWN((byte) 0),
    UP((byte) 1),
    SLIDING_DOWN((byte) 2),
    SLIDING_UP((byte) 3);

    public final byte id;

    EnumZPMState(byte id) {
        this.id = id;
    }

    public static EnumZPMState getValue(byte id) {
        if (id > values().length || id < 0) throw new IllegalArgumentException("ID not set");
        return EnumZPMState.values()[id];
    }
}
