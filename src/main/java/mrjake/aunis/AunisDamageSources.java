package mrjake.aunis;

import net.minecraft.util.DamageSource;

public class AunisDamageSources {
	public static final DamageSource DAMAGE_EVENT_HORIZON = new DamageSource("eventHorizon")
			.setDamageIsAbsolute().setDamageBypassesArmor();
	public static final DamageSource DAMAGE_EVENT_IRIS = new DamageSource("irisDeath")
			.setDamageIsAbsolute().setDamageBypassesArmor().setProjectile();
	public static final DamageSource DAMAGE_EVENT_IRIS_CREATIVE = new DamageSource("irisDeath")
			.setDamageIsAbsolute().setDamageBypassesArmor().setProjectile().setDamageAllowedInCreativeMode();
}
