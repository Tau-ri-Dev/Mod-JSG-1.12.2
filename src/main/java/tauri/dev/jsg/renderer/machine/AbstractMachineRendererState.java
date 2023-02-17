package tauri.dev.jsg.renderer.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.state.State;

public abstract class AbstractMachineRendererState extends State {

    public int machineProgress;
    public long workStateChanged;
    public boolean isWorking;
    public ItemStack craftingStack;

    public AbstractMachineRendererState() {
    }

    public AbstractMachineRendererState(long workStateChanged, int machineProgress, boolean isWorking, ItemStack workingOnItemStack) {
        this.workStateChanged = workStateChanged;
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

        buf.writeLong(workStateChanged);
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

        workStateChanged = buf.readLong();
    }
}
