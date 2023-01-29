package tauri.dev.jsg.gui.mainmenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public enum EnumMainMenuTips {
    NETHER_GATE(0, "menu.nether_gate"),
    ENERGY(1, "menu.energy"),
    DHD(2, "menu.dhd"),
    GDO(3, "menu.gdo"),
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
