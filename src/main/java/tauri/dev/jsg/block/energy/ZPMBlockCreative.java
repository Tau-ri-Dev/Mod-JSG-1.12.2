package tauri.dev.jsg.block.energy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.item.energy.ZPMItemBlockCreative;
import tauri.dev.jsg.tileentity.energy.ZPMCreativeTile;

public class ZPMBlockCreative extends ZPMBlock {
    public static final String BLOCK_NAME = "creative_zpm";

    public ZPMBlockCreative() {
        super(true);
        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);
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
