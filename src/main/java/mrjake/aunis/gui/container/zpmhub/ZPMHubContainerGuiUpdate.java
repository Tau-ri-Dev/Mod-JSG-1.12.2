package mrjake.aunis.gui.container.zpmhub;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.state.State;
import mrjake.aunis.tileentity.util.ReactorStateEnum;

public class ZPMHubContainerGuiUpdate extends State {
	public ZPMHubContainerGuiUpdate() {}

	public int zpmsCount;

	public ZPMHubContainerGuiUpdate(int zpmsCount) {
		this.zpmsCount = zpmsCount;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(zpmsCount);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		zpmsCount = buf.readInt();
	}

}
