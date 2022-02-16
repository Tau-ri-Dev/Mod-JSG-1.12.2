package mrjake.aunis.stargate.network;

import mrjake.aunis.stargate.EnumSpinDirection;
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
