package tauri.dev.jsg.block;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

public abstract class JSGAbstractCustomItemBlock extends JSGBlock {
    public JSGAbstractCustomItemBlock(Material blockMaterialIn) {
        super(blockMaterialIn);
    }

    public abstract ItemBlock getItemBlock();
}
