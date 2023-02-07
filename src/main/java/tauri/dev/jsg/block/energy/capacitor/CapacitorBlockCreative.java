package tauri.dev.jsg.block.energy.capacitor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.item.energy.CapacitorItemBlockCreative;
import tauri.dev.jsg.tileentity.energy.CapacitorCreativeTile;

public class CapacitorBlockCreative extends CapacitorBlock {

    public static final String BLOCK_NAME = "capacitor_block_creative";

    public CapacitorBlockCreative() {
        super(true);

        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);

        setHardness(3.0f);
        setHarvestLevel("pickaxe", 10);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new CapacitorItemBlockCreative(this);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return !player.isSneaking();
    }

    // ------------------------------------------------------------------------
    // Tile Entity

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return CapacitorCreativeTile.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new CapacitorCreativeTile();
    }
}
