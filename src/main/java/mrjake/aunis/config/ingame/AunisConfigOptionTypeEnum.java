package mrjake.aunis.config.ingame;

public enum AunisConfigOptionTypeEnum {
    TEXT(0),
    NUMBER(1),
    BOOLEAN(2),
    SWITCH(3);

    public int id;

    AunisConfigOptionTypeEnum(int id) {
        this.id = id;
    }

    public static AunisConfigOptionTypeEnum byId(int id) {
        for (AunisConfigOptionTypeEnum value : values()) {
            if (value.id == id)
                return value;
        }
        return TEXT;
    }
}
