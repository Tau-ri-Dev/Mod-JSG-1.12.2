package mrjake.aunis.item.gdo;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.util.EnumKeyInterface;
import mrjake.aunis.util.EnumKeyMap;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum GDOMode implements EnumKeyInterface<Byte> {
	MILKYWAY(0, "item.aunis.gdo.milkyway", true, "linkedGate", "nearby",
			BlockMatcher.forBlock(AunisBlocks.STARGATE_MILKY_WAY_BASE_BLOCK)),
	PEGASUS(0, "item.aunis.gdo.pegasus", true, "linkedGate", "nearby",
			 BlockMatcher.forBlock(AunisBlocks.STARGATE_PEGASUS_BASE_BLOCK)),
	UNIVERSE(0, "item.aunis.gdo.universe", true, "linkedGate", "nearby",
			 BlockMatcher.forBlock(AunisBlocks.STARGATE_UNIVERSE_BASE_BLOCK));

	public final byte id;
	public final String translationKey;
	public final boolean linkable;
	public final String tagPosName;
	public final String tagListName;
	public final BlockMatcher matcher;

	private GDOMode(int id, String translationKey, boolean linkable, String tagPosName, String tagListName, BlockMatcher matcher) {
		this.id = (byte) id;
		this.translationKey = translationKey;
		this.linkable = linkable;
		this.tagPosName = tagPosName;
		this.tagListName = tagListName;
		this.matcher = matcher;
	}

	public GDOMode next() {
		switch (this) {
			case MILKYWAY: return PEGASUS;
			case PEGASUS: return UNIVERSE;
			case UNIVERSE: return MILKYWAY;
		}
		
		return null;
	}
	
	public GDOMode prev() {
		switch (this) {
			case UNIVERSE: return PEGASUS;
			case PEGASUS: return MILKYWAY;
			case MILKYWAY: return UNIVERSE;
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
	
	private static final EnumKeyMap<Byte, GDOMode> ID_MAP = new EnumKeyMap<Byte, GDOMode>(values());
	
	public static GDOMode valueOf(byte id) {
		return ID_MAP.valueOf(id);
	}
}