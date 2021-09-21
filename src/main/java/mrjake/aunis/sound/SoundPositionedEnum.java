package mrjake.aunis.sound;

import mrjake.aunis.Aunis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

import java.util.HashMap;
import java.util.Map;

public enum SoundPositionedEnum {
  MILKYWAY_RING_ROLL(0, "gate_milkyway_ring_roll", AunisSoundHelper.AUNIS_SOUND_CATEGORY, false),
  WORMHOLE_LOOP(1, "wormhole_loop", AunisSoundHelper.AUNIS_SOUND_CATEGORY, true),
  UNIVERSE_RING_ROLL(2, "gate_universe_roll", AunisSoundHelper.AUNIS_SOUND_CATEGORY, false),
  BEAMER_LOOP(3, "beamer_loop", AunisSoundHelper.AUNIS_SOUND_CATEGORY, true),
  PEGASUS_RING_ROLL(4, "gate_pegasus_ring_roll", AunisSoundHelper.AUNIS_SOUND_CATEGORY, false),
  MAINMENU_RINGROLL(5, "mainmenu_ring_roll", AunisSoundHelper.AUNIS_SOUND_CATEGORY, true),
  MAINMENU_MUSIC(6, "mainmenu_music", AunisSoundHelper.AUNIS_SOUND_CATEGORY, true);

  public int id;
  public ResourceLocation resourceLocation;
  public SoundCategory soundCategory;
  public boolean repeat;

  SoundPositionedEnum(int id, String name, SoundCategory soundCategory, boolean repeat) {
    this.id = id;
    this.resourceLocation = new ResourceLocation(Aunis.ModID, name);
    this.soundCategory = soundCategory;
    this.repeat = repeat;
  }

  private static Map<Integer, SoundPositionedEnum> ID_MAP = new HashMap<Integer, SoundPositionedEnum>(values().length);

  static {
    for (SoundPositionedEnum positionedSound : values()) {
      ID_MAP.put(positionedSound.id, positionedSound);
    }
  }

  public static SoundPositionedEnum valueOf(int id) {
    return ID_MAP.get(id);
  }
}
