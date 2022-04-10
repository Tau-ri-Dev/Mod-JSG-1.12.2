package mrjake.aunis.state.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.Aunis;
import mrjake.aunis.state.State;
import mrjake.aunis.transportrings.SymbolTypeTransportRingsEnum;
import mrjake.aunis.transportrings.TransportRings;
import mrjake.aunis.transportrings.TransportRingsAddress;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransportRingsGuiState extends State {

	private boolean inGrid;
	public boolean isInGrid() { return inGrid; }
	
	private TransportRingsAddress address;
	public TransportRingsAddress getAddress() { return address; }
	
	private String name;
	public String getName() { return name != null ? name : ""; }

	private int distance;
	public int getDistance() { return distance; }
	
	private List<TransportRings> ringsList = new ArrayList<>();
	public List<TransportRings> getRings() { return ringsList; }
	
	public TransportRingsGuiState() {}
	
	public TransportRingsGuiState(TransportRings rings, Collection<TransportRings> ringsList) {
		inGrid = rings.isInGrid();
		
		if (inGrid) {
			this.address = rings.getAddress();
			this.name = rings.getName();
			this.distance = rings.getRingsDistance();
			
//			this.ringsList.add(rings);
			this.ringsList.addAll(ringsList);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {		
		buf.writeBoolean(inGrid);
		
		if (inGrid) {
			buf.writeInt(name.length());
			buf.writeCharSequence(name, StandardCharsets.UTF_8);
			address.toBytes(buf);
			
			buf.writeInt(ringsList.size());
			
			for (TransportRings rings : ringsList) {
				rings.getAddress().toBytes(buf);
				buf.writeInt(rings.getName().length());
				buf.writeCharSequence(rings.getName(), StandardCharsets.UTF_8);
			}

			buf.writeInt(distance);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		address = new TransportRingsAddress(SymbolTypeTransportRingsEnum.GOAULD);
		inGrid = buf.readBoolean();
		
		if (inGrid) {
			int length = buf.readInt();
			name = buf.readCharSequence(length, StandardCharsets.UTF_8).toString();
			address.fromBytes(buf);

			int size = buf.readInt();
			for (int i = 0; i < size; i++) {
				TransportRingsAddress address = new TransportRingsAddress(SymbolTypeTransportRingsEnum.GOAULD);
				address.fromBytes(buf);

				length = buf.readInt();
				String name = buf.readCharSequence(length, StandardCharsets.UTF_8).toString();

				ringsList.add(new TransportRings(address, name));
			}

			distance = buf.readInt();
		}
	}
}
