package mrjake.aunis.block;

import net.minecraft.block.material.Material;

import java.util.Map;

public abstract class AunisAbstractCustomMetaItemBlock extends AunisAbstractCustomItemBlock{
    public AunisAbstractCustomMetaItemBlock(Material materialIn) {
        super(materialIn);
    }

    public abstract Map<Integer, String> getAllMetaTypes();
}
