package mrjake.aunis.block.stargate;

import mrjake.aunis.AunisProps;
import mrjake.aunis.item.StargatePegasusMemberItemBlock;
import mrjake.aunis.item.StargateUniverseMemberItemBlock;
import mrjake.aunis.stargate.EnumMemberVariant;
import mrjake.aunis.stargate.merging.StargateAbstractMergeHelper;
import mrjake.aunis.stargate.merging.StargateUniverseMergeHelper;
import mrjake.aunis.tileentity.energy.CapacitorTile;
import mrjake.aunis.tileentity.stargate.StargateUniverseMemberTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public final class StargateUniverseMemberBlock extends StargateClassicMemberBlock {

	public static final String BLOCK_NAME = "stargate_universe_member_block";
	
	public final int RING_META = getMetaFromState(getDefaultState().withProperty(AunisProps.MEMBER_VARIANT, EnumMemberVariant.RING));
	public final int CHEVRON_META = getMetaFromState(getDefaultState().withProperty(AunisProps.MEMBER_VARIANT, EnumMemberVariant.CHEVRON));
	
	public StargateUniverseMemberBlock() {
		super(BLOCK_NAME);
		setResistance(40.0f);
	}

	@Override
	protected StargateAbstractMergeHelper getMergeHelper() {
		return StargateUniverseMergeHelper.INSTANCE;
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return StargateUniverseMemberTile.class;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new StargateUniverseMemberTile();
	}

	@Override
	public ItemBlock getItemBlock() {
		return new StargateUniverseMemberItemBlock(this);
	}

	@Override
	public Map<Integer, String> getAllMetaTypes() {
		Map<Integer, String> map = new HashMap<>();
		map.put(RING_META, "aunis:stargate_universe_ring_block");
		map.put(CHEVRON_META, "aunis:stargate_universe_chevron_block");
		return map;
	}

}
