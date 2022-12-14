package tauri.dev.jsg.stargate.merging;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.stargate.StargatePegasusMemberBlock;
import tauri.dev.jsg.stargate.EnumMemberVariant;

import javax.annotation.Nullable;

public class StargatePegasusMergeHelper extends StargateClassicMergeHelper {

    public static final StargatePegasusMergeHelper INSTANCE = new StargatePegasusMergeHelper();

    public static final BlockMatcher BASE_MATCHER = BlockMatcher.forBlock(JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK);
    public static final BlockMatcher MEMBER_MATCHER = BlockMatcher.forBlock(JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK);

    @Override
    @Nullable
    public EnumMemberVariant getMemberVariantFromItemStack(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemBlock)) return null;

        // No need to use .equals() because blocks are singletons
        if (((ItemBlock) stack.getItem()).getBlock() != JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK) return null;

        int meta = stack.getMetadata();

        if (meta == JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK.RING_META) return EnumMemberVariant.RING;

        if (meta == JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK.CHEVRON_META) return EnumMemberVariant.CHEVRON;

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
    public StargatePegasusMemberBlock getMemberBlock() {
        return JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK;
    }
}
