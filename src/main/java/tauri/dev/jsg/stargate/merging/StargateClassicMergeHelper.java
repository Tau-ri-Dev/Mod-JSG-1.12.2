package tauri.dev.jsg.stargate.merging;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.stargate.StargateClassicBaseBlock;
import tauri.dev.jsg.block.stargate.StargateMilkyWayBaseBlock;
import tauri.dev.jsg.block.stargate.StargateMilkyWayMemberBlock;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.tileentity.stargate.StargateClassicMemberTile;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayMemberTile;
import tauri.dev.jsg.util.FacingToRotation;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class StargateClassicMergeHelper extends StargateAbstractMergeHelper {

    protected static final JSGAxisAlignedBB BASE_SEARCH_BOX_SMALL = new JSGAxisAlignedBB(-3, -7, 0, 3, 0, 0);
    protected static final JSGAxisAlignedBB BASE_SEARCH_BOX_LARGE = new JSGAxisAlignedBB(-5, -9, 0, 5, 0, 0);
    protected static final JSGAxisAlignedBB BASE_SEARCH_BOX_EXTRA_LARGE = new JSGAxisAlignedBB(-7, -13, 0, 7, 0, 0);

    protected static final List<BlockPos> RING_BLOCKS_SMALL = Arrays.asList(
            new BlockPos(1, 7, 0),
            new BlockPos(3, 5, 0),
            new BlockPos(3, 3, 0),
            new BlockPos(2, 1, 0),
            new BlockPos(-2, 1, 0),
            new BlockPos(-3, 3, 0),
            new BlockPos(-3, 5, 0),
            new BlockPos(-1, 7, 0)
    );

    protected static final List<BlockPos> CHEVRON_BLOCKS_SMALL = Arrays.asList(
            new BlockPos(2, 6, 0),
            new BlockPos(3, 4, 0),
            new BlockPos(3, 2, 0),
            new BlockPos(-3, 2, 0),
            new BlockPos(-3, 4, 0),
            new BlockPos(-2, 6, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 7, 0)
    );

    protected static final List<BlockPos> RING_BLOCKS_LARGE = Arrays.asList(
            new BlockPos(-1, 0, 0),
            new BlockPos(-3, 1, 0),
            new BlockPos(-4, 3, 0),
            new BlockPos(-5, 4, 0),
            new BlockPos(-4, 6, 0),
            new BlockPos(-4, 7, 0),
            new BlockPos(-2, 9, 0),
            new BlockPos(-1, 9, 0),
            new BlockPos(1, 9, 0),
            new BlockPos(2, 9, 0),
            new BlockPos(4, 7, 0),
            new BlockPos(4, 6, 0),
            new BlockPos(5, 4, 0),
            new BlockPos(4, 3, 0),
            new BlockPos(3, 1, 0),
            new BlockPos(1, 0, 0)
    );

    protected static final List<BlockPos> CHEVRON_BLOCKS_LARGE = Arrays.asList(
            new BlockPos(3, 8, 0),
            new BlockPos(5, 5, 0),
            new BlockPos(4, 2, 0),
            new BlockPos(-4, 2, 0),
            new BlockPos(-5, 5, 0),
            new BlockPos(-3, 8, 0),
            new BlockPos(2, 0, 0),
            new BlockPos(-2, 0, 0),
            new BlockPos(0, 9, 0)
    );

    protected static final List<BlockPos> RING_BLOCKS_EXTRA_LARGE = Arrays.asList(
            new BlockPos(1, 0, 0),
            new BlockPos(2, 1, 0),
            new BlockPos(3, 1, 0),
            new BlockPos(4, 1, 0),
            new BlockPos(4, 2, 0),
            new BlockPos(5, 2, 0),
            new BlockPos(5, 4, 0),
            new BlockPos(6, 4, 0),
            new BlockPos(6, 5, 0),
            new BlockPos(6, 6, 0),
            new BlockPos(6, 8, 0),
            new BlockPos(5, 8, 0),
            new BlockPos(5, 9, 0),
            new BlockPos(5, 10, 0),
            new BlockPos(4, 11, 0),
            new BlockPos(3, 11, 0),
            new BlockPos(2, 11, 0),
            new BlockPos(2, 12, 0),
            new BlockPos(1, 12, 0),
            new BlockPos(-1, 12, 0),
            new BlockPos(-2, 12, 0),
            new BlockPos(-2, 11, 0),
            new BlockPos(-3, 11, 0),
            new BlockPos(-4, 11, 0),
            new BlockPos(-5, 10, 0),
            new BlockPos(-5, 9, 0),
            new BlockPos(-5, 8, 0),
            new BlockPos(-6, 8, 0),
            new BlockPos(-6, 6, 0),
            new BlockPos(-6, 5, 0),
            new BlockPos(-6, 4, 0),
            new BlockPos(-5, 4, 0),
            new BlockPos(-5, 2, 0),
            new BlockPos(-4, 2, 0),
            new BlockPos(-4, 1, 0),
            new BlockPos(-3, 1, 0),
            new BlockPos(-2, 1, 0),
            new BlockPos(-1, 0, 0)
    );

    protected static final List<BlockPos> CHEVRON_BLOCKS_EXTRA_LARGE = Arrays.asList(
            new BlockPos(4, 10, 0),
            new BlockPos(6, 7, 0),
            new BlockPos(5, 3, 0),
            new BlockPos(2, 0, 0),
            new BlockPos(-4, 10, 0),
            new BlockPos(-6, 7, 0),
            new BlockPos(-5, 3, 0),
            new BlockPos(-2, 0, 0),
            new BlockPos(0, 12, 0)
    );


    @Nonnull
    @Override
    public List<BlockPos> getRingBlocks() {
        switch (JSGConfig.stargateSize) {
            case LARGE:
                return RING_BLOCKS_LARGE;
            case EXTRA_LARGE:
                return RING_BLOCKS_EXTRA_LARGE;

            default:
                return RING_BLOCKS_SMALL;
        }
    }

    @Nonnull
    @Override
    public List<BlockPos> getChevronBlocks() {
        switch (tauri.dev.jsg.config.JSGConfig.stargateSize) {
            case LARGE:
                return CHEVRON_BLOCKS_LARGE;
            case EXTRA_LARGE:
                return CHEVRON_BLOCKS_EXTRA_LARGE;

            default:
                return CHEVRON_BLOCKS_SMALL;
        }
    }

    @Override
    public JSGAxisAlignedBB getBaseSearchBox() {
        switch (tauri.dev.jsg.config.JSGConfig.stargateSize) {
            case SMALL:
            case MEDIUM:
                return BASE_SEARCH_BOX_SMALL;

            case LARGE:
                return BASE_SEARCH_BOX_LARGE;

            case EXTRA_LARGE:
                return BASE_SEARCH_BOX_EXTRA_LARGE;

            default:
                return null;
        }
    }

    /**
     * Converts merged Stargate from old pattern (1.5)
     * to new pattern (1.6).
     *
     * @param world               {@link World} instance.
     * @param basePos             Position of {@link StargateClassicBaseBlock} the tiles should be linked to.
     * @param baseFacing          Facing of {@link StargateClassicBaseBlock}.
     * @param currentStargateSize Current Stargate size as read from NBT.
     * @param targetStargateSize  Target Stargate size as defined in config.
     */
    public void convertToPattern(World world, BlockPos basePos, EnumFacing baseFacing, StargateSizeEnum currentStargateSize, StargateSizeEnum targetStargateSize) {
        JSG.debug(basePos + ": Converting Stargate from " + currentStargateSize + " to " + targetStargateSize);
        List<BlockPos> oldPatternBlocks = new ArrayList<>();

        switch (currentStargateSize) {
            case SMALL:
            case MEDIUM:
                oldPatternBlocks.addAll(RING_BLOCKS_SMALL);
                oldPatternBlocks.addAll(CHEVRON_BLOCKS_SMALL);
                break;

            case LARGE:
                oldPatternBlocks.addAll(RING_BLOCKS_LARGE);
                oldPatternBlocks.addAll(CHEVRON_BLOCKS_LARGE);
                break;
            case EXTRA_LARGE:
                oldPatternBlocks.addAll(RING_BLOCKS_EXTRA_LARGE);
                oldPatternBlocks.addAll(CHEVRON_BLOCKS_EXTRA_LARGE);
                break;
        }

        for (BlockPos pos : oldPatternBlocks)
            world.setBlockToAir(pos.rotate(FacingToRotation.get(baseFacing)).add(basePos));

        IBlockState memberState = getMemberBlock().getDefaultState()
                .withProperty(JSGProps.FACING_HORIZONTAL, baseFacing)
                .withProperty(JSGProps.RENDER_BLOCK, false);

        for (BlockPos pos : getRingBlocks())
            world.setBlockState(pos.rotate(FacingToRotation.get(baseFacing)).add(basePos), memberState.withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.RING));

        for (BlockPos pos : getChevronBlocks())
            world.setBlockState(pos.rotate(FacingToRotation.get(baseFacing)).add(basePos), memberState.withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.CHEVRON));
    }


    protected boolean checkMemberBlock(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing, EnumMemberVariant variant) {
        IBlockState state = blockAccess.getBlockState(pos);

        return matchMember(state) && state.getValue(JSGProps.FACING_HORIZONTAL) == facing && state.getValue(JSGProps.MEMBER_VARIANT) == variant;
    }

    protected void updateMemberMergeStatus(World world, BlockPos checkPos, BlockPos basePos, EnumFacing baseFacing, boolean shouldBeMerged) {
        checkPos = checkPos.rotate(FacingToRotation.get(baseFacing)).add(basePos);
        IBlockState state = world.getBlockState(checkPos);

        if (matchMember(state)) {
            StargateClassicMemberTile memberTile = (StargateClassicMemberTile) world.getTileEntity(checkPos);

            if ((shouldBeMerged && !memberTile.isMerged()) || (memberTile.isMerged() && memberTile.getBasePos().equals(basePos))) {

                ItemStack camoStack = memberTile.getCamoItemStack();
                if (camoStack != null) {
                    InventoryHelper.spawnItemStack(world, checkPos.getX(), checkPos.getY(), checkPos.getZ(), camoStack);
                }

                if (memberTile.getCamoState() != null) {
                    memberTile.setCamoState(null);
                }

                // This also sets merge status
                memberTile.setBasePos(shouldBeMerged ? basePos : null);

                world.setBlockState(checkPos, state.withProperty(JSGProps.RENDER_BLOCK, !shouldBeMerged), 3);
            }
        }
    }

    /**
     * Updates the {@link StargateMilkyWayBaseBlock} position of the
     * {@link StargateMilkyWayMemberTile}.
     *
     * @param blockAccess Usually {@link World}.
     * @param pos         Position of the currently updated {@link StargateMilkyWayMemberBlock}.
     * @param basePos     Position of {@link StargateMilkyWayBaseBlock} the tiles should be linked to.
     * @param baseFacing  Facing of {@link StargateMilkyWayBaseBlock}.
     */
    private void updateMemberBasePos(IBlockAccess blockAccess, BlockPos pos, BlockPos basePos, EnumFacing baseFacing) {
        IBlockState state = blockAccess.getBlockState(pos);

        if (matchMember(state)) {
            StargateClassicMemberTile memberTile = (StargateClassicMemberTile) blockAccess.getTileEntity(pos);

            memberTile.setBasePos(basePos);
        }
    }

    /**
     * Updates all {@link StargateMilkyWayMemberTile} to contain
     * correct {@link StargateMilkyWayBaseBlock} position.
     *
     * @param blockAccess Usually {@link World}.
     * @param basePos     Position of {@link StargateMilkyWayBaseBlock} the tiles should be linked to.
     * @param baseFacing  Facing of {@link StargateMilkyWayBaseBlock}.
     */
    @Override
    public void updateMembersBasePos(IBlockAccess blockAccess, BlockPos basePos, EnumFacing baseFacing) {
        for (BlockPos pos : getRingBlocks())
            updateMemberBasePos(blockAccess, pos.rotate(FacingToRotation.get(baseFacing)).add(basePos), basePos, baseFacing);

        for (BlockPos pos : getChevronBlocks())
            updateMemberBasePos(blockAccess, pos.rotate(FacingToRotation.get(baseFacing)).add(basePos), basePos, baseFacing);
    }
}
