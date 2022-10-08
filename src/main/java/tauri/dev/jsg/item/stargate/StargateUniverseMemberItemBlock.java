package tauri.dev.jsg.item.stargate;

import tauri.dev.jsg.block.stargate.StargateUniverseMemberBlock;

public final class StargateUniverseMemberItemBlock extends StargateMemberItemBlock {

	public StargateUniverseMemberItemBlock(StargateUniverseMemberBlock block) {
		super(block);
	}

	@Override
	protected String getRingUnlocalizedName() {
		return "tile.jsg.stargate_universe_ring_block";
	}

	@Override
	protected String getChevronUnlocalizedName() {
		return "tile.jsg.stargate_universe_chevron_block";
	}
}
