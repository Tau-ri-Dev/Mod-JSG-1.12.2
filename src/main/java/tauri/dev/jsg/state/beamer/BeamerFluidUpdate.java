package tauri.dev.jsg.state.beamer;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.nio.charset.StandardCharsets;

public class BeamerFluidUpdate extends State {
	public BeamerFluidUpdate() {}
	
	public Fluid fluidContained;

	public BeamerFluidUpdate(Fluid fluid) {
		fluidContained = fluid;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(fluidContained != null);
		
		if (fluidContained != null) { 
			String string = FluidRegistry.getFluidName(fluidContained);
			buf.writeInt(string.length());
			buf.writeCharSequence(string, StandardCharsets.UTF_8);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {		
		if (buf.readBoolean()) {
			// Had fluid stack
			
			int size = buf.readInt();
			fluidContained = FluidRegistry.getFluid(buf.readCharSequence(size, StandardCharsets.UTF_8).toString());
		}
	}

}
