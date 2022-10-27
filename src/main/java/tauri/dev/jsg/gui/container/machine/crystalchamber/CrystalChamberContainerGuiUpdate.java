package tauri.dev.jsg.gui.container.machine.crystalchamber;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.state.State;

import java.nio.charset.StandardCharsets;

public class CrystalChamberContainerGuiUpdate extends State {
    public CrystalChamberContainerGuiUpdate() {}

    public int energyStored;
    public int energyTransferedLastTick;
    public long machineStart;
    public long machineEnd;
    public FluidStack fluidStack;

    public CrystalChamberContainerGuiUpdate(int energyStored, FluidStack fluidStack, int energyTransferedLastTick, long machineStart, long machineEnd) {
        this.energyStored = energyStored;
        this.energyTransferedLastTick = energyTransferedLastTick;
        this.machineStart = machineStart;
        this.machineEnd = machineEnd;
        this.fluidStack = fluidStack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(energyStored);
        buf.writeInt(energyTransferedLastTick);
        buf.writeLong(machineStart);
        buf.writeLong(machineEnd);

        if (fluidStack != null && fluidStack.getFluid() != null) {
            buf.writeBoolean(true);

            String name = FluidRegistry.getFluidName(fluidStack);
            buf.writeInt(name.length());
            buf.writeCharSequence(name, StandardCharsets.UTF_8);
            buf.writeInt(fluidStack.amount);
        }
        else
            buf.writeBoolean(false);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        energyStored = buf.readInt();
        energyTransferedLastTick = buf.readInt();
        machineStart = buf.readLong();
        machineEnd = buf.readLong();

        if (buf.readBoolean()) {
            int size = buf.readInt();
            fluidStack = FluidRegistry.getFluidStack(buf.readCharSequence(size, StandardCharsets.UTF_8).toString(), buf.readInt());
        }
    }
}
