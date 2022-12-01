package tauri.dev.jsg.gui.container.machine.orewashing;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.gui.container.machine.AbstractMachineContainerGuiUpdate;

import java.nio.charset.StandardCharsets;

public class OreWashingContainerGuiUpdate extends AbstractMachineContainerGuiUpdate {
    public OreWashingContainerGuiUpdate() {
        super();
    }

    public FluidStack fluidStack;

    public OreWashingContainerGuiUpdate(int energyStored, FluidStack fluidStack, int energyTransferedLastTick, long machineStart, long machineEnd) {
        super(energyStored, energyTransferedLastTick, machineStart, machineEnd);
        this.fluidStack = fluidStack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        if (fluidStack != null && fluidStack.getFluid() != null) {
            buf.writeBoolean(true);

            String name = FluidRegistry.getFluidName(fluidStack);
            buf.writeInt(name.length());
            buf.writeCharSequence(name, StandardCharsets.UTF_8);
            buf.writeInt(fluidStack.amount);
        } else
            buf.writeBoolean(false);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        if (buf.readBoolean()) {
            int size = buf.readInt();
            fluidStack = FluidRegistry.getFluidStack(buf.readCharSequence(size, StandardCharsets.UTF_8).toString(), buf.readInt());
        }
    }
}
