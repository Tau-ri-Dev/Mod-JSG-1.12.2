package tauri.dev.jsg.sound;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.util.EnumKeyInterface;
import tauri.dev.jsg.util.EnumKeyMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public enum SoundEventEnum implements EnumKeyInterface<Integer> {

    // ----------------------------------------------------------
    // Stargate - General
    WORMHOLE_GO(0, "wormhole_go", 0.5f),
    WORMHOLE_FLICKER(1, "wormhole_flicker", 0.5f),

    IRIS_HIT(130, "iris_hit", 0.5f),
    IRIS_CLOSING(131, "iris_closing", 0.5f),
    IRIS_OPENING(132, "iris_opening", 0.5f),

    SHIELD_HIT(133, "shield_hit", 0.5f),
    SHIELD_CLOSING(134, "shield_closing", 0.5f),
    SHIELD_OPENING(135, "shield_opening", 0.5f),

    // ----------------------------------------------------------
    // Stargate - Milky Way
    DHD_MILKYWAY_PRESS(10, "dhd_milkyway_press", 0.1f),
    DHD_MILKYWAY_PRESS_BRB(11, "dhd_milkyway_press_brb", 0.1f),

    GATE_MILKYWAY_OPEN(12, "gate_milkyway_open", 0.5f),
    GATE_MILKYWAY_CLOSE(13, "gate_milkyway_close", 0.4f),
    GATE_MILKYWAY_DIAL_FAILED(14, "gate_milkyway_dial_fail", 0.5f),
    GATE_MILKYWAY_DIAL_FAILED_COMPUTER(15, "gate_milkyway_dial_fail_computer", 0.5f),
    GATE_MILKYWAY_INCOMING(16, "gate_milkyway_incoming", 0.4f),

    GATE_MILKYWAY_CHEVRON_SHUT(17, "gate_milkyway_chevron_shut", 0.5f),
    GATE_MILKYWAY_CHEVRON_OPEN(18, "gate_milkyway_chevron_open", 0.5f),


    // ----------------------------------------------------------
    // Stargate - Universe
    GATE_UNIVERSE_DIAL_START(70, "gate_universe_dial_start", 0.5f),
    GATE_UNIVERSE_CHEVRON_LOCK(71, "gate_universe_chevron_lock", 0.6f),
    GATE_UNIVERSE_CHEVRON_TOP_LOCK(72, "gate_universe_chevron_top_lock", 0.6f),
    GATE_UNIVERSE_DIAL_FAILED(73, "gate_universe_fail", 0.6f),
    GATE_UNIVERSE_OPEN(74, "gate_universe_open", 0.5f),
    GATE_UNIVERSE_CLOSE(75, "gate_universe_close", 0.5f),

    // ----------------------------------------------------------
    // Stargate - Pegasus
    DHD_PEGASUS_PRESS(80, "dhd_pegasus_press", 0.08f),
    DHD_PEGASUS_PRESS_BRB(81, "dhd_pegasus_press_brb", 0.08f),

    GATE_PEGASUS_CHEVRON_OPEN(82, "gate_pegasus_chevron_open", 0.5f),
    GATE_PEGASUS_OPEN(83, "gate_pegasus_open", 0.5f),
    GATE_PEGASUS_INCOMING(84, "gate_pegasus_incoming", 0.4f),
    GATE_PEGASUS_DIAL_FAILED(85, "gate_pegasus_dial_fail", 0.5f),

    // ----------------------------------------------------------
    // Stargate - Orlin
    GATE_ORLIN_DIAL(90, "gate_orlin_dial", 0.5f),
    GATE_ORLIN_FAIL(91, "gate_orlin_dial_fail", 0.5f),
    GATE_ORLIN_BROKE(92, "gate_orlin_broke", 0.5f),

    // ----------------------------------------------------------
    // Ring transporter
    RINGS_TRANSPORT(100, "rings_transport", 0.5f),
    TR_CONTROLLER_GOAULD_BUTTON(101, "rings_controller_goauld_button", 0.5f),
    TR_CONTROLLER_GOAULD_BUTTON_FINAL(102, "rings_controller_goauld_button_final", 0.5f),

    RINGS_PLATFORM_SHIPS_OPEN(105, "platform_ships_open", 0.5f),
    RINGS_PLATFORM_SHIPS_CLOSE(106, "platform_ships_close", 0.5f),

    // ----------------------------------------------------------
    // Beamer
    BEAMER_START(110, "beamer_start", 0.5f),
    BEAMER_STOP(111, "beamer_stop", 0.5f),

    // ----------------------------------------------------------
    // Misc
    PAGE_FLIP(120, "page_flip", 0.2f),
    GUI_SEND_CODE_BUTTON_PRESS(121, "gui_send_code_button_press", 0.1f),
    UNIVERSE_DIALER_MODE_CHANGE(122, "universe_dialer_mode_change", 0.1f),
    UNIVERSE_DIALER_CONNECTED(123, "universe_dialer_connect", 0.2f),
    UNIVERSE_DIALER_START_DIAL(124, "universe_dialer_start_dial", 0.1f),

    DESTINY_COUNTDOWN_START(125, "destiny_countdown_start", 0.2f),
    DESTINY_COUNTDOWN_STOP(126, "destiny_countdown_stop", 0.2f),

    // ----------------------------------------------------------
    // Entities
    ZAT_SHOOT(140, "item_zat_shoot", 0.1f),
    STAFF_SHOOT(141, "item_staff_shoot", 0.1f);

    // ----------------------------------------------------------


    private static final EnumKeyMap<Integer, SoundEventEnum> idMap = new EnumKeyMap<Integer, SoundEventEnum>(values());
    public int id;
    public SoundEvent soundEvent;
    public float volume;
    private final String name;

    SoundEventEnum(int id, String name, float volume) {
        this.id = id;
        this.volume = volume;
        this.name = name;
        this.soundEvent = createSoundEvent(name);
    }

    public static SoundEventEnum valueOf(int id) {
        return idMap.valueOf(id);
    }

    private static SoundEvent createSoundEvent(String name) {
        ResourceLocation resourceLocation = new ResourceLocation(JSG.MOD_ID, name);
        return new SoundEvent(resourceLocation).setRegistryName(resourceLocation);
    }

    @Override
    public Integer getKey() {
        return id;
    }

    @Nullable
    public static SoundEvent remapSound(String oldBlockName) {
        switch (oldBlockName) {
            case "jsg:platform_goauld_close":
                return createSoundEvent(RINGS_PLATFORM_SHIPS_CLOSE.name);
            case "jsg:platform_goauld_open":
                return createSoundEvent(RINGS_PLATFORM_SHIPS_OPEN.name);

            default:
                return null;
        }
    }

    @SubscribeEvent
    public static void onMissingSoundMappings(RegistryEvent.MissingMappings<SoundEvent> event) {
        for (RegistryEvent.MissingMappings.Mapping<SoundEvent> mapping : event.getMappings()) {
            SoundEvent newSound = remapSound(mapping.key.toString());

            if (newSound != null) mapping.remap(newSound);
        }
    }
}
