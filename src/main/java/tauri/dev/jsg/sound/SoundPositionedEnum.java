package tauri.dev.jsg.sound;

import tauri.dev.jsg.JSG;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

import java.util.HashMap;
import java.util.Map;

public enum SoundPositionedEnum {

  // gate ring sounds
  MILKYWAY_RING_ROLL(0, "gate_milkyway_ring_roll", JSG.JSG_SOUNDS, true, 0.4f),
  MILKYWAY_RING_ROLL_START(14, "gate_milkyway_ring_roll_start", JSG.JSG_SOUNDS, false, 0.4f),

  UNIVERSE_RING_ROLL(2, "gate_universe_roll", JSG.JSG_SOUNDS, true, 0.5f),
  UNIVERSE_RING_ROLL_START(15, "gate_universe_roll_start", JSG.JSG_SOUNDS, false, 0.5f),

  PEGASUS_RING_ROLL(4, "gate_pegasus_ring_roll", JSG.JSG_SOUNDS, true, 0.4f),
  PEGASUS_RING_ROLL_START(16, "gate_pegasus_ring_roll_start", JSG.JSG_SOUNDS, false, 0.4f),

  // loops
  BEAMER_LOOP(3, "beamer_loop", JSG.JSG_SOUNDS, true, 0.3f),
  WORMHOLE_LOOP(1, "wormhole_loop", JSG.JSG_SOUNDS, true, 0.4f),

  // main menu
  MAINMENU_MUSIC(6, "mainmenu_music", JSG.JSG_SOUNDS, false, 0.05f),
  LOADING_MUSIC(7, "loading_music", JSG.JSG_SOUNDS, true, 0.07f);

  public final int id;
  public final ResourceLocation resourceLocation;
  public final SoundCategory soundCategory;
  public final boolean repeat;
  public final float volume;

  SoundPositionedEnum(int id, String name, SoundCategory soundCategory, boolean repeat, float volume) {
    this.id = id;
    this.resourceLocation = new ResourceLocation(JSG.MOD_ID, name);
    this.soundCategory = soundCategory;
    this.repeat = repeat;
    this.volume = volume;
  }

  private static final Map<Integer, SoundPositionedEnum> ID_MAP = new HashMap<>(values().length);

  static {
    for (SoundPositionedEnum positionedSound : values()) {
      ID_MAP.put(positionedSound.id, positionedSound);
    }
  }

  public static SoundPositionedEnum valueOf(int id) {
    return ID_MAP.get(id);
  }
}
