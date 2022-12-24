package tauri.dev.jsg.datafixer;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class StargateNetworkReader18 {
	
	public static void readOldMap(NBTTagCompound compound, StargateNetwork network) {
		int size = compound.getInteger("size");
		
		for (int i=0; i<size; i++) {
			BlockPos pos = BlockPos.fromLong(compound.getLong("pos"+i));
			int dim = compound.getInteger("dim"+i);
			
			Random random = new Random(pos.hashCode() * 31 + dim);
			StargateAddress address = new StargateAddress(SymbolTypeEnum.MILKYWAY);
			address.generate(random);
			StargatePos stargatePos = new StargatePos(dim, pos, address);
			
			JSG.debug("Adding old gate: " + address);
			network.addStargate(address, stargatePos);
		}
	}
}
