package tauri.dev.jsg.renderer.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.state.State;

import java.util.ArrayList;
import java.util.List;

public class PCBFabricatorRendererState extends State {

    public int machineProgress;
    public boolean isWorking;
    public ItemStack craftingStack;
    public List<Float> colors = new ArrayList<>();

    public PCBFabricatorRendererState() {
    }

    public PCBFabricatorRendererState(int machineProgress, boolean isWorking, ItemStack workingOnItemStack, float[] colors) {
        this.machineProgress = machineProgress;
        this.isWorking = isWorking;
        this.craftingStack = workingOnItemStack;
        this.colors = new ArrayList<Float>() {{
            for (float color : colors) add(color);
        }};
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

        buf.writeInt(colors.size());
        for (float color : colors)
            buf.writeFloat(color);
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
        int s = buf.readInt();
        colors.clear();
        for (int i = 0; i < s; i++)
            colors.add(buf.readFloat());
    }
}
