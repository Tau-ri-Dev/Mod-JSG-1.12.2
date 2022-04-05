package mrjake.aunis.block.transportrings;

import mrjake.aunis.Aunis;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
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

public abstract class TransportRingsAbstractBlock extends Block {

  public TransportRingsAbstractBlock(String name) {
    super(Material.IRON);

    setRegistryName(Aunis.ModID + ":" + name);
    setUnlocalizedName(Aunis.ModID + "." + name);

    setSoundType(SoundType.STONE);
    setCreativeTab(Aunis.aunisRingsCreativeTab);

    setLightOpacity(0);

    setHardness(3.0f);
    setHarvestLevel("pickaxe", 3);
  }

  // ------------------------------------------------------------------------
  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(pos);

    if (!world.isRemote) {
      if (ringsTile != null) {
        AunisPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_STATE, ringsTile.getState(StateTypeEnum.GUI_STATE)), (EntityPlayerMP) player);
      }
    }

    return true;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(pos);

    if (!world.isRemote) {
      if (ringsTile != null) {
        ringsTile.updateLinkStatus();
      }
    }
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {
    TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(pos);
    if (ringsTile != null) {
      if(ringsTile.isLinked()) ringsTile.getLinkedControllerTile(world).setLinkedRings(null, -1);
      ringsTile.removeAllRings();
      ringsTile.onBreak();
    }
    super.breakBlock(world, pos, state);
  }

  // ------------------------------------------------------------------------
  @Override
  public boolean hasTileEntity(IBlockState state) {
    return true;
  }

  @Override
  public abstract TransportRingsAbstractTile createTileEntity(World world, IBlockState state);
}
