package tauri.dev.jsg.item.stargate;

import tauri.dev.jsg.block.stargate.StargatePegasusMemberBlock;

public final class StargatePegasusMemberItemBlock extends StargateMemberItemBlock {

  public StargatePegasusMemberItemBlock(StargatePegasusMemberBlock block) {
    super(block);
  }

  @Override
  protected String getRingUnlocalizedName() {
    return "tile.jsg.stargate_pegasus_ring_block";
  }

  @Override
  protected String getChevronUnlocalizedName() {
    return "tile.jsg.stargate_pegasus_chevron_block";
  }
}
