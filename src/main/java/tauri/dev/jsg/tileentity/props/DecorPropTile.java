package tauri.dev.jsg.tileentity.props;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import tauri.dev.jsg.item.props.DecorPropItem;
import tauri.dev.jsg.util.main.JSGProps;

public class DecorPropTile extends TileEntity implements ITickable {
    @Override
    public void update() {
        DecorPropItem.PropVariants variant = DecorPropItem.PropVariants.byId(world.getBlockState(pos).getValue(JSGProps.PROP_VARIANT));
        if(variant.runnableWhileRendering != null) {
            if (world.isRemote) {
                variant.runnableWhileRendering.runOnClient(world, variant, this);
            } else {
                variant.runnableWhileRendering.runOnServer(world, variant, this);
            }
        }
    }
}
