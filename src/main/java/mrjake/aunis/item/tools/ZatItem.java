package mrjake.aunis.item.tools;

import mrjake.aunis.Aunis;
import mrjake.aunis.entity.AunisEnergyProjectile;
import mrjake.aunis.entity.EntityRegister;
import mrjake.aunis.sound.SoundEventEnum;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ZatItem extends Item {

    public static final String ITEM_NAME = "zat";

    public ZatItem() {
        setRegistryName(new ResourceLocation(Aunis.ModID, ITEM_NAME));
        setUnlocalizedName(Aunis.ModID + "." + ITEM_NAME);
        setMaxStackSize(1);

        setCreativeTab(Aunis.aunisToolsCreativeTab);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            EntityRegister.playSoundEvent(SoundEventEnum.ZAT_SHOOT, player);
            world.spawnEntity(AunisEnergyProjectile.createEnergyBall(world, player));
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("Work In Progress Item!");
    }

    // todo(Mine): add "durability" bar for "charging" or "cooldown"
}
