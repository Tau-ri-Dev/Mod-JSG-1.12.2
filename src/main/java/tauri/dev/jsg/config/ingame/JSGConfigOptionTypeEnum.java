package tauri.dev.jsg.config.ingame;

public enum JSGConfigOptionTypeEnum {
    TEXT(0),
    NUMBER(1),
    BOOLEAN(2),
    SWITCH(3);

    public int id;

    JSGConfigOptionTypeEnum(int id) {
        this.id = id;
    }

    public static JSGConfigOptionTypeEnum byId(int id) {
        for (JSGConfigOptionTypeEnum value : values()) {
            if (value.id == id)
                return value;
        }
        return TEXT;
    }
}
