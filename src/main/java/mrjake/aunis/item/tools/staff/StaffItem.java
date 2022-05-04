package mrjake.aunis.item.tools.staff;

import mrjake.aunis.AunisDamageSources;
import mrjake.aunis.entity.AunisEnergyProjectile;
import mrjake.aunis.entity.EntityRegister;
import mrjake.aunis.item.tools.EnergyWeapon;
import mrjake.aunis.sound.SoundEventEnum;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StaffItem extends EnergyWeapon {

    public static final String ITEM_NAME = "staff";

    public StaffItem() {
        super(ITEM_NAME, 10_000_000, 100_000);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntityItemStackRenderer createTEISR() {
        return new StaffTEISR();
    }

    @Override
    public void playShootSound(World world, Entity entity){
        EntityRegister.playSoundEvent(SoundEventEnum.STAFF_SHOOT, entity);
    }

    @Override
    public int getWeaponCoolDown(){ return 30; }

    @Override
    public DamageSource getDamageSource(Entity source, Entity attacker){
        return AunisDamageSources.getDamageSourceStaff(source, attacker);
    }

    @Override
    public void setEnergyBallParams(AunisEnergyProjectile projectile) {
        super.setEnergyBallParams(projectile);
        projectile.explode = true;
        projectile.maxAliveTime = 90;
        projectile.damage = 10.0f;
        projectile.igniteGround = true;
    }
}
