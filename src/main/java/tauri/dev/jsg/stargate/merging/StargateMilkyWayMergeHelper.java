package tauri.dev.jsg.stargate.merging;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.stargate.StargateMilkyWayMemberBlock;
import tauri.dev.jsg.stargate.EnumMemberVariant;

import javax.annotation.Nullable;

public class StargateMilkyWayMergeHelper extends StargateClassicMergeHelper {

    public static final StargateMilkyWayMergeHelper INSTANCE = new StargateMilkyWayMergeHelper();

    public static final BlockMatcher BASE_MATCHER = BlockMatcher.forBlock(JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK);
    public static final BlockMatcher MEMBER_MATCHER = BlockMatcher.forBlock(JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK);

    @Override
    @Nullable
    public EnumMemberVariant getMemberVariantFromItemStack(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemBlock))
            return null;

        // No need to use .equals() because blocks are singletons
        if (((ItemBlock) stack.getItem()).getBlock() != JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK)
            return null;

        int meta = stack.getMetadata();

        if (meta == JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK.RING_META)
            return EnumMemberVariant.RING;

        if (meta == JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK.CHEVRON_META)
            return EnumMemberVariant.CHEVRON;

        return null;
    }

    @Override
    public boolean matchBase(IBlockState state) {
        return BASE_MATCHER.apply(state);
    }

    @Override
    public boolean matchMember(IBlockState state) {
        return MEMBER_MATCHER.apply(state);
    }

    @Override
    public StargateMilkyWayMemberBlock getMemberBlock() {
        return JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK;
    }
}
