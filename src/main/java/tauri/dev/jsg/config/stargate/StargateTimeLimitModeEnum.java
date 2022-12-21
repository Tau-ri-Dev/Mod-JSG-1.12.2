package tauri.dev.jsg.config.stargate;

import javax.annotation.Nonnull;

public enum StargateTimeLimitModeEnum {
    DISABLED(0, "DISABLED"),
    CLOSE_GATE(1, "CLOSE_GATE"),
    DRAW_MORE_POWER(2, "DRAW_POWER");

    public final int id;
    public final String name;

    StargateTimeLimitModeEnum(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }

    @Nonnull
    public static StargateTimeLimitModeEnum byId(int id){
        for(StargateTimeLimitModeEnum e : StargateTimeLimitModeEnum.values()) {
            if (e.id == id)
                return e;
        }
        return DISABLED;
    }
}
