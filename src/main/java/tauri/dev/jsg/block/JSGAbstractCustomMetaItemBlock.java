package tauri.dev.jsg.block;

import net.minecraft.block.material.Material;

import java.util.Map;

public abstract class JSGAbstractCustomMetaItemBlock extends JSGAbstractCustomItemBlock {
    public JSGAbstractCustomMetaItemBlock(Material materialIn) {
        super(materialIn);
    }

    public abstract Map<Integer, String> getAllMetaTypes();
}
