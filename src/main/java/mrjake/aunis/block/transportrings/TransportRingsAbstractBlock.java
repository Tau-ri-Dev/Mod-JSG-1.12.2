package mrjake.aunis.block.transportrings;

import mrjake.aunis.Aunis;
import mrjake.aunis.gui.GuiIdEnum;
import mrjake.aunis.tileentity.dialhomedevice.DHDAbstractTile;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.util.ItemHandlerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

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
            if (!player.isSneaking() && ringsTile != null) {
                if (!ringsTile.tryInsertUpgrade(player, hand)) {
                    player.openGui(Aunis.instance, GuiIdEnum.GUI_RINGS.id, world, pos.getX(), pos.getY(), pos.getZ());
                }
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
        if (!world.isRemote) {
            TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(pos);
            if (ringsTile != null) {
                if (ringsTile.isLinked()) ringsTile.getLinkedControllerTile(world).setLinkedRings(null, -1);
                ringsTile.removeAllRings();
                ringsTile.onBreak();
                ItemHandlerHelper.dropInventoryItems(world, pos, ringsTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
            }
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
