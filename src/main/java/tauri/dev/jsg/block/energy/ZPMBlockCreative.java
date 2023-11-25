package tauri.dev.jsg.block.energy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.item.energy.ZPMItemBlockCreative;
import tauri.dev.jsg.tileentity.energy.ZPMCreativeTile;

import javax.annotation.ParametersAreNonnullByDefault;

public class ZPMBlockCreative extends ZPMBlock {
    public static final String BLOCK_NAME = "creative_zpm";

    public ZPMBlockCreative() {
        super(true);
        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);

        setHardness(-1);
        setHarvestLevel(null, -1);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity){
        if(entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative()) return false;
        return super.canEntityDestroy(state, world, pos, entity);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ZPMItemBlockCreative(this);
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return ZPMCreativeTile.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new ZPMCreativeTile();
    }
}
