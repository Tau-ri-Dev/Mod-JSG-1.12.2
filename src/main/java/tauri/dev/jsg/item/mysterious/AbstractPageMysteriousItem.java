package tauri.dev.jsg.item.mysterious;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.advancements.JSGAdvancements;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.notebook.PageNotebookItem;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.worldgen.structures.stargate.StargateGenerator;
import tauri.dev.jsg.worldgen.util.GeneratedStargate;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class AbstractPageMysteriousItem extends Item {
    public static final String BASE_NAME = "page_mysterious";
    protected final SymbolTypeEnum symbolType;
    protected final int dimensionToSpawn;

    public AbstractPageMysteriousItem(String typeName, SymbolTypeEnum symbolType, int dimensionToSpawn) {
        this.symbolType = symbolType;
        this.dimensionToSpawn = dimensionToSpawn;

        setRegistryName(JSG.MOD_ID + ":" + BASE_NAME + "_" + typeName);
        setUnlocalizedName(JSG.MOD_ID + "." + BASE_NAME + "_" + typeName);

        setCreativeTab(JSGCreativeTabsHandler.JSG_ITEMS_CREATIVE_TAB);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.ITALIC + JSG.proxy.localize("item.jsg.page_mysterious.tooltip"));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (!world.isRemote) {
            sendPlayerMessageAboutGeneration(player, true, false);
            if (JSGConfig.WorldGen.mystPage.pageCooldown > 0)
                player.getCooldownTracker().setCooldown(this, JSGConfig.WorldGen.mystPage.pageCooldown);
            GeneratedStargate stargate = StargateGenerator.mystPageGeneration(world, symbolType, dimensionToSpawn, player);

            if (stargate != null) {
                givePlayerPage(player, hand, stargate);
                sendPlayerMessageAboutGeneration(player, false, true);
                if (player instanceof EntityPlayerMP)
                    JSGAdvancements.MYST_PAGE.trigger((EntityPlayerMP) player);
            } else {
                player.getCooldownTracker().setCooldown(this, 0);
                sendPlayerMessageAboutGeneration(player, false, false);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public void givePlayerPage(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, GeneratedStargate stargate) {
        NBTTagCompound compound = PageNotebookItem.getCompoundFromAddress(stargate.address, stargate.hasUpgrade, stargate.path, stargate.originId);

        ItemStack stack = new ItemStack(JSGItems.PAGE_NOTEBOOK_ITEM, 1, 1);
        stack.setTagCompound(compound);

        ItemStack held = player.getHeldItem(hand);
        held.shrink(1);

        if (held.isEmpty())
            player.setHeldItem(hand, stack);

        else {
            player.setHeldItem(hand, held);
            player.addItemStackToInventory(stack);
        }
    }

    protected void sendPlayerMessageAboutGeneration(@Nonnull EntityPlayer player, boolean generationStart, boolean generationSuccess) {
        if (generationStart)
            player.sendStatusMessage(new TextComponentTranslation("item.jsg.page_mysterious.generation.start"), true);
        else if (generationSuccess)
            player.sendStatusMessage(new TextComponentTranslation("item.jsg.page_mysterious.generation.success"), true);
        else
            player.sendStatusMessage(new TextComponentTranslation("item.jsg.page_mysterious.generation.failed"), true);
    }
}
