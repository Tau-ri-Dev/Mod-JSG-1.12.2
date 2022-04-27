package mrjake.aunis.item.tools;

import mrjake.aunis.Aunis;
import mrjake.aunis.entity.EntityRegister;
import mrjake.aunis.sound.SoundEventEnum;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ZatItem extends Item {

    public static final String ITEM_NAME = "zat";

    public ZatItem(){
        setRegistryName(new ResourceLocation(Aunis.ModID, ITEM_NAME));
        setUnlocalizedName(Aunis.ModID + "." + ITEM_NAME);
        setMaxStackSize(1);

        setCreativeTab(Aunis.aunisToolsCreativeTab);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            Vec3d vector = player.getLookVec();
            double x = vector.x;
            double y = vector.y;
            double z = vector.z;
            //Aunis.info("Clicked: X:" + x + ", Y:" + y + ", Z:" + z);
            EntitySmallFireball fireBall = new EntitySmallFireball(world, player, x * 50, y * 50, z * 50);
            fireBall.setLocationAndAngles(player.posX, player.posY + 1, player.posZ, player.rotationYaw, player.rotationPitch);
            fireBall.setPosition(fireBall.posX, fireBall.posY, fireBall.posZ);
            EntityRegister.playSoundEvent(SoundEventEnum.ZAT_SHOOT, player);
            fireBall.setInvisible(true);
            world.spawnEntity(fireBall);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("Work In Progress Item!");
    }

    // todo(Mine): add "durability" bar for "charging" or "cooldown"
}
