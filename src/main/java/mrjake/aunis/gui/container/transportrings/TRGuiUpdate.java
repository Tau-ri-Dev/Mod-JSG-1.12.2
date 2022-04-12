package mrjake.aunis.gui.container.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.state.State;
import mrjake.aunis.transportrings.SymbolTypeTransportRingsEnum;
import mrjake.aunis.transportrings.TransportRingsAddress;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TRGuiUpdate extends State {
	public TRGuiUpdate() {}
	
	public int energyStored;
	public int transferedLastTick;
	public int ringsDistance;
	public String ringsName;
	public Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> trAdddressMap;

	public TRGuiUpdate(int energyStored, int transferedLastTick, int ringsDistance, String ringsName, Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> adddressMap) {
		this.energyStored = energyStored;
		this.transferedLastTick = transferedLastTick;
		this.ringsDistance = ringsDistance;
		this.ringsName = ringsName;
		this.trAdddressMap = adddressMap;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energyStored);
		buf.writeInt(transferedLastTick);
		buf.writeInt(ringsDistance);
		buf.writeInt(ringsName.length());
		buf.writeCharSequence(ringsName, StandardCharsets.UTF_8);
		for (SymbolTypeTransportRingsEnum symbolType : SymbolTypeTransportRingsEnum.values()) {
			trAdddressMap.get(symbolType).toBytes(buf);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energyStored = buf.readInt();
		transferedLastTick = buf.readInt();
		ringsDistance = buf.readInt();
		int size = buf.readInt();
		ringsName = buf.readCharSequence(size, StandardCharsets.UTF_8).toString();
		trAdddressMap = new HashMap<>();

		for (SymbolTypeTransportRingsEnum symbolType : SymbolTypeTransportRingsEnum.values()) {
			TransportRingsAddress address = new TransportRingsAddress(symbolType);
			address.fromBytes(buf);
			trAdddressMap.put(symbolType, address);
		}
	}
}
