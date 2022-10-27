package tauri.dev.jsg.renderer.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.state.State;

public class CrystalChamberRendererState extends State {

    public int machineProgress;
    public boolean isWorking;
    public ItemStack craftingStack;

    public CrystalChamberRendererState() {
    }

    public CrystalChamberRendererState(int machineProgress, boolean isWorking, ItemStack workingOnItemStack) {
        this.machineProgress = machineProgress;
        this.isWorking = isWorking;
        this.craftingStack = workingOnItemStack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(machineProgress);
        buf.writeBoolean(isWorking);

        buf.writeBoolean(craftingStack != null && !craftingStack.isEmpty());
        if (craftingStack != null && !craftingStack.isEmpty()) {
            buf.writeInt(craftingStack.getCount());
            buf.writeInt(Item.getIdFromItem(craftingStack.getItem()));
            buf.writeInt(craftingStack.getMetadata());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        machineProgress = buf.readInt();
        isWorking = buf.readBoolean();

        if (buf.readBoolean()) {
            int size = buf.readInt();
            int id = buf.readInt();
            int meta = buf.readInt();
            craftingStack = new ItemStack(Item.getItemById(id), size, meta);
        }
    }
}
