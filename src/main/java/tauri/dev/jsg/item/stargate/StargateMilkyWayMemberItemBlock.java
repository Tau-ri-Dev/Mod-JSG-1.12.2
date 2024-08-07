package tauri.dev.jsg.item.stargate;

import tauri.dev.jsg.block.stargate.StargateMilkyWayMemberBlock;

public final class StargateMilkyWayMemberItemBlock extends StargateMemberItemBlock {

	public StargateMilkyWayMemberItemBlock(StargateMilkyWayMemberBlock block) {
		super(block);
	}

	@Override
	protected String getRingUnlocalizedName() {
		return "tile.jsg.stargate_milkyway_ring_block";
	}

	@Override
	protected String getChevronUnlocalizedName() {
		return "tile.jsg.stargate_milkyway_chevron_block";
	}
}
