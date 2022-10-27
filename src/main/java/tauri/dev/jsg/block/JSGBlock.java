package tauri.dev.jsg.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class JSGBlock extends Block {
    public JSGBlock(Material materialIn) {
        super(materialIn);
    }

    /**
     * Get class of tile entity - used for registry in client proxy
     * @return class of tile entity
     */
    public Class<? extends TileEntity> getTileEntityClass() {
        return null;
    }

    /**
     * Get TESR of tile entity to register in client proxy
     * @return TESR
     */
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return null;
    }

    /**
     * Should block have highlighting edges?
     * @param blockState - current block state
     * @return - should render highlights
     */
    public boolean renderHighlight(IBlockState blockState){
        return true;
    }
}
