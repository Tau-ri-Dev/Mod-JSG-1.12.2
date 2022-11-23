package tauri.dev.jsg.block.transportrings;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.util.ItemHandlerHelper;
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

import javax.annotation.Nonnull;

public abstract class TransportRingsAbstractBlock extends JSGBlock {

    public TransportRingsAbstractBlock(String name) {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + name);
        setUnlocalizedName(JSG.MOD_ID + "." + name);

        setSoundType(SoundType.STONE);
        setCreativeTab(JSGCreativeTabsHandler.JSG_RINGS_CREATIVE_TAB);

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
                    player.openGui(JSG.instance, GuiIdEnum.GUI_RINGS.id, world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, @Nonnull BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(pos);

        if (!world.isRemote) {
            if (ringsTile != null) {
                ringsTile.updateLinkStatus();
            }
        }
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
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
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public abstract TransportRingsAbstractTile createTileEntity(@Nonnull World world, @Nonnull IBlockState state);
}
