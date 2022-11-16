package tauri.dev.jsg.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import tauri.dev.jsg.JSG;

public class JSGAdvancements {
    /* GATE MERGING */
    public static final JSGAdvancement MERGED_ORLIN = new JSGAdvancement("merged_orlin");
    public static final JSGAdvancement MERGED_MILKYWAY = new JSGAdvancement("merged_milkyway");
    public static final JSGAdvancement MERGED_PEGASUS = new JSGAdvancement("merged_pegasus");
    public static final JSGAdvancement MERGED_UNIVERSE = new JSGAdvancement("merged_universe");

    /* GATE OPEN */
    public static final JSGAdvancement CHEVRON_SEVEN_LOCKED = new JSGAdvancement("chevron_seven_locked");
    public static final JSGAdvancement CHEVRON_EIGHT_LOCKED = new JSGAdvancement("chevron_eight_locked");
    public static final JSGAdvancement CHEVRON_NINE_LOCKED = new JSGAdvancement("chevron_nine_locked");

    /* OTHER */
    public static final JSGAdvancement WORMHOLE_GO = new JSGAdvancement("wormhole_go");
    public static final JSGAdvancement IRIS_IMPACT = new JSGAdvancement("iris_impact");
    public static final JSGAdvancement GDO_USED = new JSGAdvancement("gdo_used");
    public static final JSGAdvancement KAWOOSH_CREMATION = new JSGAdvancement("kawoosh_cremation");
    public static final JSGAdvancement UNSTABLE_SURVIVE = new JSGAdvancement("unstable_eh_survive");

    /* ZPM */
    public static final JSGAdvancement THREE_ZPMS = new JSGAdvancement("three_zpms");
    public static final JSGAdvancement ZPM_SLOT = new JSGAdvancement("zpm_slot");

    public static final JSGAdvancement[] TRIGGER_ARRAY = new JSGAdvancement[]{
            MERGED_ORLIN,
            MERGED_MILKYWAY,
            MERGED_PEGASUS,
            MERGED_UNIVERSE,

            CHEVRON_SEVEN_LOCKED,
            CHEVRON_EIGHT_LOCKED,
            CHEVRON_NINE_LOCKED,

            WORMHOLE_GO,
            IRIS_IMPACT,
            GDO_USED,
            KAWOOSH_CREMATION,
            UNSTABLE_SURVIVE,

            THREE_ZPMS,
            ZPM_SLOT
    };

    public static void register() {
        for (int i = 0; i < JSGAdvancements.TRIGGER_ARRAY.length; i++) {
            CriteriaTriggers.register(JSGAdvancements.TRIGGER_ARRAY[i]);
        }
        JSG.info("Advancements successfully loaded!");
    }
}
