package mrjake.aunis.block.props;

import mrjake.aunis.Aunis;
import mrjake.aunis.item.props.TRPlatformItem;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.util.main.AunisProps;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class TRPlatformBlock extends AunisAbstractProp {

    public static final String BASE = "platform_";
    public static final String END = "_block";
    public static final String BLOCK_NAME = BASE + "rings" + END;

    public TRPlatformBlock() {
        super(Material.IRON);

        setRegistryName(Aunis.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(Aunis.MOD_ID + ":" + BLOCK_NAME);

        setSoundType(SoundType.STONE);

        setHardness(3.0f);
        setHarvestLevel("pickaxe", 3);
    }

    @Override
    public void getSubBlocks(@Nonnull CreativeTabs creativeTabs, @Nonnull NonNullList<ItemStack> items) {
        for (TRPlatformItem.TRPlatformVariants variant : TRPlatformItem.TRPlatformVariants.values()) {
            items.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(AunisProps.PROP_VARIANT, variant.id))));
        }
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
