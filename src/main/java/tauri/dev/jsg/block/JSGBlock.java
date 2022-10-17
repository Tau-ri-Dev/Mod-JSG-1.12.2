package tauri.dev.jsg.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class JSGBlock extends Block {
    public JSGBlock(Material materialIn) {
        super(materialIn);
    }

    public Class<? extends TileEntity> getTileEntityClass() {
        return null;
    }

    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return null;
    }
}
