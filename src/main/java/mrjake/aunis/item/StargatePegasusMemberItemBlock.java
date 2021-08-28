package mrjake.aunis.item;

import mrjake.aunis.block.stargate.StargatePegasusMemberBlock;

public final class StargatePegasusMemberItemBlock extends StargateMemberItemBlock {

  public StargatePegasusMemberItemBlock(StargatePegasusMemberBlock block) {
    super(block);
  }

  @Override
  protected String getRingUnlocalizedName() {
    return "tile.aunis.stargate_pegasus_ring_block";
  }

  @Override
  protected String getChevronUnlocalizedName() {
    return "tile.aunis.stargate_pegasus_chevron_block";
  }
}
