package mrjake.aunis.item.tools.staff;

import mrjake.aunis.AunisDamageSources;
import mrjake.aunis.entity.AunisEnergyProjectile;
import mrjake.aunis.entity.EntityRegister;
import mrjake.aunis.item.tools.EnergyWeapon;
import mrjake.aunis.sound.SoundEventEnum;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class StaffItem extends EnergyWeapon {

    public static final String ITEM_NAME = "staff";

    public StaffItem() {
        super(ITEM_NAME, 10_000_000, 100_000);
    }

    @Override
    public TileEntityItemStackRenderer createTEISR() {
        return new StaffTEISR();
    }

    @Override
    public void playShootSound(World world, EntityPlayer player){
        EntityRegister.playSoundEvent(SoundEventEnum.ZAT_SHOOT, player);
    }

    @Override
    public DamageSource getDamageSource(){
        return AunisDamageSources.DAMAGE_G_STAFF;
    }

    @Override
    public void setEnergyBallParams(AunisEnergyProjectile projectile) {
        super.setEnergyBallParams(projectile);
        projectile.explode = true;
        projectile.maxAliveTime = 50;
        projectile.damage = 10.0f;
        projectile.igniteGround = true;
    }
}
