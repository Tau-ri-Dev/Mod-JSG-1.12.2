package tauri.dev.jsg.block.stargate;

import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.item.stargate.StargatePegasusMemberItemBlock;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.stargate.merging.StargateAbstractMergeHelper;
import tauri.dev.jsg.stargate.merging.StargatePegasusMergeHelper;
import tauri.dev.jsg.tileentity.stargate.StargatePegasusMemberTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public final class StargatePegasusMemberBlock extends StargateClassicMemberBlock {

    public static final String BLOCK_NAME = "stargate_pegasus_member_block";

    public final int RING_META = getMetaFromState(getDefaultState().withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.RING));
    public final int CHEVRON_META = getMetaFromState(getDefaultState().withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.CHEVRON));

    public StargatePegasusMemberBlock() {
        super(BLOCK_NAME);
    }

    @Override
    protected StargateAbstractMergeHelper getMergeHelper() {
        return StargatePegasusMergeHelper.INSTANCE;
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return StargatePegasusMemberTile.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new StargatePegasusMemberTile();
    }

    @Override
    public ItemBlock getItemBlock() {
        return new StargatePegasusMemberItemBlock(this);
    }

    @Override
    public Map<Integer, String> getAllMetaTypes() {
        Map<Integer, String> map = new HashMap<>();
        map.put(RING_META, "jsg:stargate_pegasus_ring_block");
        map.put(CHEVRON_META, "jsg:stargate_pegasus_chevron_block");
        return map;
    }
}
