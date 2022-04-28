package mrjake.aunis;

import net.minecraft.util.DamageSource;

public class AunisDamageSources {
	public static final DamageSource DAMAGE_EVENT_HORIZON = new DamageSource("eventHorizon")
			.setDamageIsAbsolute().setDamageBypassesArmor();
	public static final DamageSource DAMAGE_WRONG_SIDE = new DamageSource("wrongSide")
			.setDamageIsAbsolute().setDamageBypassesArmor();
	public static final DamageSource DAMAGE_EVENT_IRIS_CREATIVE = new DamageSource("irisDeath")
			.setDamageIsAbsolute().setDamageBypassesArmor().setDamageAllowedInCreativeMode();

	public static final DamageSource DAMAGE_ZAT = new DamageSource("zatHit")
			.setDamageBypassesArmor().setMagicDamage().setProjectile();
	public static final DamageSource DAMAGE_G_STAFF = new DamageSource("staffHit")
			.setDamageBypassesArmor().setExplosion().setFireDamage().setProjectile();
}
