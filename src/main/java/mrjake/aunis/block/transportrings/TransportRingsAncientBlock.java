package mrjake.aunis.block.transportrings;

import mrjake.aunis.Aunis;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.tileentity.TransportRingsAncientTile;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TransportRingsAncientBlock extends Block {

  private static final String blockName = "transportrings_block";

  public TransportRingsAncientBlock() {
    super(Material.IRON);

    setRegistryName(Aunis.ModID + ":" + blockName);
    setUnlocalizedName(Aunis.ModID + "." + blockName);

    setSoundType(SoundType.STONE);
    setCreativeTab(Aunis.aunisCreativeTab);

    setLightOpacity(0);

    setHardness(3.0f);
    setHarvestLevel("pickaxe", 3);
  }

  // ------------------------------------------------------------------------
  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    TransportRingsAncientTile ringsTile = (TransportRingsAncientTile) world.getTileEntity(pos);

    if (!world.isRemote) {
      //			if (player.getHeldItem(hand).getItem() == AunisItems.analyzerAncient)
      AunisPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_STATE, ringsTile.getState(StateTypeEnum.GUI_STATE)), (EntityPlayerMP) player);
    }

    return true;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    TransportRingsAncientTile ringsTile = (TransportRingsAncientTile) world.getTileEntity(pos);

    if (!world.isRemote) {
      ringsTile.updateLinkStatus();
    }
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {
    TransportRingsAncientTile ringsTile = (TransportRingsAncientTile) world.getTileEntity(pos);

    if (ringsTile.isLinked()) ringsTile.getLinkedControllerTile(world).setLinkedRings(null, -1);

    ringsTile.removeAllRings();
  }

  // ------------------------------------------------------------------------
  @Override
  public boolean hasTileEntity(IBlockState state) {
    return true;
  }

  @Override
  public TransportRingsAncientTile createTileEntity(World world, IBlockState state) {
    return new TransportRingsAncientTile();
  }
}
