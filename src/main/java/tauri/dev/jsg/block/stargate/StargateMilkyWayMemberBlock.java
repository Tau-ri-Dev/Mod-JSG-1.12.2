package tauri.dev.jsg.block.stargate;

import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.item.stargate.StargateMilkyWayMemberItemBlock;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.stargate.merging.StargateAbstractMergeHelper;
import tauri.dev.jsg.stargate.merging.StargateMilkyWayMergeHelper;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayMemberTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public final class StargateMilkyWayMemberBlock extends StargateClassicMemberBlock {

	public static final String BLOCK_NAME = "stargate_milkyway_member_block";
	
	public final int RING_META = getMetaFromState(getDefaultState().withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.RING));
	public final int CHEVRON_META = getMetaFromState(getDefaultState().withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.CHEVRON));
	
	public StargateMilkyWayMemberBlock() {
		super(BLOCK_NAME);
	}

	@Override
	protected StargateAbstractMergeHelper getMergeHelper() {
		return StargateMilkyWayMergeHelper.INSTANCE;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new StargateMilkyWayMemberTile();
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return StargateMilkyWayMemberTile.class;
	}

	@Override
	public ItemBlock getItemBlock() {
		return new StargateMilkyWayMemberItemBlock(this);
	}

	@Override
	public Map<Integer, String> getAllMetaTypes(){
		Map<Integer, String> map = new HashMap<>();
		map.put(RING_META, "jsg:stargate_milkyway_ring_block");
		map.put(CHEVRON_META, "jsg:stargate_milkyway_chevron_block");
		return map;
	}
}
