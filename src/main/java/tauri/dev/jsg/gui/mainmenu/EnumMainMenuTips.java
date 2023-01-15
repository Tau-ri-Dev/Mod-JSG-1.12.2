package tauri.dev.jsg.gui.mainmenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public enum EnumMainMenuTips {
    NETHER_GATE(0, "TIP: If you go into Nether, do not forget to save Nether's gate address!"),
    ENERGY(1, "TIP: If you plug cables for gate into multiple gate's blocks, the gate will charge faster!"),
    DHD(2, "TIP: Do not forget that DHDs can be upgraded with Capacity and Efficiency upgrades!"),
    GDO(3, "TIP: If you install iris on your gate, do not forget to sometimes check it's durability!"),
    ;

    public final String[] text;
    public final int id;

    EnumMainMenuTips(int id, String... text) {
        this.id = id;
        this.text = text;
    }

    @Nonnull
    public static EnumMainMenuTips byId(int id) {
        for (EnumMainMenuTips t : EnumMainMenuTips.values()) {
            if (t.id == id) return t;
        }
        return EnumMainMenuTips.NETHER_GATE;
    }

    @Nonnull
    public static EnumMainMenuTips random(@Nullable EnumMainMenuTips previousTip) {
        int length = EnumMainMenuTips.values().length;
        EnumMainMenuTips newTip;
        do {
            int i = new Random().nextInt(length);
            if (i >= length) i = (length - 1);
            newTip = EnumMainMenuTips.byId(i);
        } while (previousTip != null && newTip.id == previousTip.id);
        return newTip;
    }
}
