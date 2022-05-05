package mrjake.aunis.item.linkable.dialer;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.util.EnumKeyInterface;
import mrjake.aunis.util.EnumKeyMap;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum UniverseDialerMode implements EnumKeyInterface<Byte> {	
	NEARBY(0, "item.aunis.universe_dialer.mode_scan", true, "linkedGate", "nearby", new Block[]{AunisBlocks.STARGATE_UNIVERSE_BASE_BLOCK}),
	MEMORY(1, "item.aunis.universe_dialer.mode_saved", true, "linkedGate", "saved", new Block[]{AunisBlocks.STARGATE_UNIVERSE_BASE_BLOCK}),
	RINGS(2, "item.aunis.universe_dialer.mode_rings", true, "linkedRings", "rings", AunisBlocks.RINGS_BLOCKS),
	OC(3, "item.aunis.universe_dialer.mode_oc", false, null, "ocmess", null),
	GATE_INFO(4, "item.aunis.universe_dialer.mode_info", true, "linkedGate", "info", new Block[]{AunisBlocks.STARGATE_UNIVERSE_BASE_BLOCK});

	
	public final byte id;
	public final String translationKey;
	public final boolean linkable;
	public final String tagPosName;
	public final String tagListName;
	public final Block[] matchBlocks;

	private UniverseDialerMode(int id, String translationKey, boolean linkable, String tagPosName, String tagListName, Block[] matchBlocks) {
		this.id = (byte) id;
		this.translationKey = translationKey;
		this.linkable = linkable;
		this.tagPosName = tagPosName;
		this.tagListName = tagListName;
		this.matchBlocks = matchBlocks;
	}

	public UniverseDialerMode next() {
		switch (this) {
		case NEARBY: return MEMORY;
		case MEMORY: return RINGS;
		case RINGS: return Aunis.ocWrapper.isModLoaded() ? OC : NEARBY;
		case OC: return GATE_INFO;
		case GATE_INFO: return NEARBY;
		}
		
		return null;
	}
	
	public UniverseDialerMode prev() {
		switch (this) {
			case NEARBY: return GATE_INFO;
			case MEMORY: return NEARBY;
			case RINGS: return MEMORY;
			case OC: return RINGS;
			case GATE_INFO: return Aunis.ocWrapper.isModLoaded() ? OC : RINGS;
		}
		
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public String localize() {
		return I18n.format(translationKey);
	}
	
	@Override
	public Byte getKey() {
		return id;
	}
	
	private static final EnumKeyMap<Byte, UniverseDialerMode> ID_MAP = new EnumKeyMap<Byte, UniverseDialerMode>(values());
	
	public static UniverseDialerMode valueOf(byte id) {
		return ID_MAP.valueOf(id);
	}
}