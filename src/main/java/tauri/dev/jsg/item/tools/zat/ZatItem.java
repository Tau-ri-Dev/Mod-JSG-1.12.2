package tauri.dev.jsg.item.tools.zat;

import tauri.dev.jsg.util.main.JSGDamageSources;
import tauri.dev.jsg.entity.JSGEnergyProjectile;
import tauri.dev.jsg.entity.EntityRegister;
import tauri.dev.jsg.item.tools.EnergyWeapon;
import tauri.dev.jsg.sound.SoundEventEnum;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ZatItem extends EnergyWeapon {

    public static final String ITEM_NAME = "zat";

    public ZatItem() {
        super(ITEM_NAME, 500_000, 10_000);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntityItemStackRenderer createTEISR() {
        return new ZatTEISR();
    }

    @Override
    public void playShootSound(World world, Entity entity){
        EntityRegister.playSoundEvent(SoundEventEnum.ZAT_SHOOT, entity);
    }

    @Override
    public int getWeaponCoolDown(){ return 20; }

    @Override
    public DamageSource getDamageSource(Entity source, Entity attacker){
        return JSGDamageSources.getDamageSourceZat(source, attacker);
    }

    @Override
    public void setEnergyBallParams(JSGEnergyProjectile projectile) {
        super.setEnergyBallParams(projectile);
        projectile.maxAliveTime = 25;
        projectile.paralyze = true;
        projectile.damage = 1.0F;
        projectile.igniteGround = false;
        projectile.setSize(0.0125F);
    }
}
