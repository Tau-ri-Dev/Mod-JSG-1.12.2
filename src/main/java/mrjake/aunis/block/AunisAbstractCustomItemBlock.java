package mrjake.aunis.block;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

public abstract class AunisAbstractCustomItemBlock extends AunisBlock {
    public AunisAbstractCustomItemBlock(Material blockMaterialIn) {
        super(blockMaterialIn);
    }

    public abstract ItemBlock getItemBlock();
}
