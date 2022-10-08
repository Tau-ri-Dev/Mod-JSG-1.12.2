package tauri.dev.jsg.block.props;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.item.props.TRPlatformItem;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.util.main.JSGProps;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TRPlatformBlock extends JSGAbstractProp {

    public static final String BASE = "platform_";
    public static final String END = "_block";
    public static final String BLOCK_NAME = BASE + "rings" + END;

    public TRPlatformBlock() {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + ":" + BLOCK_NAME);

        setSoundType(SoundType.STONE);

        setHardness(3.0f);
        setHarvestLevel("pickaxe", 3);
    }

    @Override
    public void getSubBlocks(@Nonnull CreativeTabs creativeTabs, @Nonnull NonNullList<ItemStack> items) {
        for (TRPlatformItem.TRPlatformVariants variant : TRPlatformItem.TRPlatformVariants.values()) {
            items.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(JSGProps.PROP_VARIANT, variant.id))));
        }
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(JSG.getInProgress());
    }

    public SoundEventEnum getPlatformSound(boolean closing) {
        if(closing)
            return getPlatform().closingSound;
        return getPlatform().openingSound;
    }

    @SideOnly(Side.CLIENT)
    public ElementEnum getPlatformModelBase() {
        return getPlatform().modelBase;
    }

    @SideOnly(Side.CLIENT)
    public ElementEnum getPlatformModelMoving() {
        return getPlatform().modelMoving;
    }

    @SideOnly(Side.CLIENT)
    public ElementEnum getPlatformModelToOverlay() {
        return getPlatform().modelOverlay;
    }

    public boolean canRenderUnderZero(){
        return getPlatform().canRenderUnderZero;
    }

    public BlockPos[] getPattern(){
        return getPlatform().pattern;
    }

    public TRPlatformItem.TRPlatformVariants getPlatform(){
        return TRPlatformItem.TRPlatformVariants.byId(this.getMetaFromState(this.getBlockState().getBaseState()));
    }

    @Override
    public ItemBlock getItemBlock() {
        return new TRPlatformItem(this);
    }

    @Override
    public int getAllMetasCount(){
        return TRPlatformItem.TRPlatformVariants.values().length;
    }

    @Override
    public String getBlockName(){
        return BLOCK_NAME;
    }
}
