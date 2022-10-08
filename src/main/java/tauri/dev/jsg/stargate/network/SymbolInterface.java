package tauri.dev.jsg.stargate.network;

import net.minecraft.util.ResourceLocation;

public interface SymbolInterface {

	boolean origin();
	float getAngle();
	int getAngleIndex();
	int getId();
	String getEnglishName();
	ResourceLocation getIconResource();
	String localize();
	SymbolTypeEnum getSymbolType();
}
