package tauri.dev.jsg.item.mysterious;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerItem;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.worldgen.StargateGenerator;

import javax.annotation.Nonnull;

public class UniversePageMysteriousItem extends AbstractPageMysteriousItem {

    public UniversePageMysteriousItem() {
        super("universe", SymbolTypeEnum.UNIVERSE, 1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (!world.isRemote) {
            StargateGenerator.GeneratedStargate stargate = StargateGenerator.generateStargate(world, symbolType, dimensionToSpawn);

            if (stargate != null) {
                ItemStack stack = new ItemStack(JSGItems.UNIVERSE_DIALER);
                UniverseDialerItem.initNBT(stack);

                if (stack.getTagCompound() != null) {
                    NBTTagList saved = stack.getTagCompound().getTagList("saved", Constants.NBT.TAG_COMPOUND);
                    NBTTagCompound compound = stargate.address.serializeNBT();
                    compound.setBoolean("hasUpgrade", stargate.hasUpgrade);
                    saved.appendTag(compound);
                }

                ItemStack held = player.getHeldItem(hand);
                held.shrink(1);

                if (held.isEmpty())
                    player.setHeldItem(hand, stack);

                else {
                    player.setHeldItem(hand, held);
                    player.addItemStackToInventory(stack);
                }

                if (JSGConfig.mysteriousConfig.pageCooldown > 0)
                    player.getCooldownTracker().setCooldown(this, JSGConfig.mysteriousConfig.pageCooldown);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
