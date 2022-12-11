package tauri.dev.jsg.renderer.biomes;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.BlockHelpers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.EnumSet;
import java.util.HashMap;

public enum BiomeOverlayEnum {
	NORMAL(0, "", TextFormatting.GRAY),
	FROST(1, "_frost", TextFormatting.DARK_AQUA),
	MOSSY(2, "_mossy", TextFormatting.DARK_GREEN),
	AGED(3, "_aged", TextFormatting.GRAY),
	SOOTY(4, "_sooty", TextFormatting.DARK_GRAY),

	// for transport rings
	TR_NORMAL(10, "", TextFormatting.GOLD)
	;
	
	public final String suffix;
	public final int id;
	private final TextFormatting color;
	private final String unlocalizedName;

	BiomeOverlayEnum(int id, String suffix, TextFormatting color) {
		this.id = id;
		this.suffix = suffix;
		this.color = color;
		this.unlocalizedName = "gui.stargate.biome_overlay." + name().toLowerCase();
	}

	private static final HashMap<Integer, BiomeOverlayEnum> ID_MAP = new HashMap<>();
	static{
		for(BiomeOverlayEnum overlay : BiomeOverlayEnum.values()){
			ID_MAP.put(overlay.id, overlay);
		}
	}

	public static BiomeOverlayEnum byId(int id){
		return ID_MAP.get(id);
	}
	
	public String getLocalizedColorizedName() {
		return color + JSG.proxy.localize(unlocalizedName);
	}
	
	/**
	 * Called every 1-2 seconds from {@link TileEntity} to update it's
	 * frosted/moss state.
	 * 
	 * @param world
	 * @param topmostBlock Topmost block of the structure (Stargates should pass top chevron/ring)
	 * @param supportedOverlays will only return enums which are in this Set
	 * @return BiomeOverlayEnum
	 */
	public static BiomeOverlayEnum updateBiomeOverlay(World world, BlockPos topmostBlock, EnumSet<BiomeOverlayEnum> supportedOverlays) {
		BiomeOverlayEnum ret = getBiomeOverlay(world, topmostBlock);
		
		if (supportedOverlays.contains(ret))
			return ret;
		
		return NORMAL;
	}
	
	private static BiomeOverlayEnum getBiomeOverlay(World world, BlockPos topmostBlock) {
		Biome biome = world.getBiome(topmostBlock);

		// If not Nether and block not under sky
		if (world.provider.getDimensionType() != DimensionType.NETHER && !BlockHelpers.isBlockDirectlyUnderSky(world, topmostBlock))
			return NORMAL;
		
		if (biome.getTemperature(topmostBlock) <= JSGConfig.stargateConfig.frostyTemperatureThreshold)
			return FROST;
		
		BiomeOverlayEnum overlay = tauri.dev.jsg.config.JSGConfig.stargateConfig.getBiomeOverrideBiomes().get(biome);
		
		if (overlay != null)
			return overlay;
		
		return NORMAL;
	}

	public static BiomeOverlayEnum fromString(String name) {
		for (BiomeOverlayEnum biomeOverlay : values()) {
			if (biomeOverlay.toString().equals(name)) {
				return biomeOverlay;
			}
		}
		
		return null;
	}
}
