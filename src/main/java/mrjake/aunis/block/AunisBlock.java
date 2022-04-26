package mrjake.aunis.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

public class AunisBlock extends Block {
    public AunisBlock(Material materialIn) {
        super(materialIn);
    }

    public Class<? extends TileEntity> getTileEntityClass(){
        return null;
    }
}
