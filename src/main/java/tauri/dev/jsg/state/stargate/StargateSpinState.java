package tauri.dev.jsg.state.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.stargate.EnumSpinDirection;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.state.State;

public class StargateSpinState extends State {
	public StargateSpinState() {}
	
	public SymbolInterface targetSymbol;
	public EnumSpinDirection direction;
	public boolean setOnly;
	public int plusRounds;
	
	public StargateSpinState(SymbolInterface targetSymbol, EnumSpinDirection direction, boolean setOnly, int plusRounds) {
		this.targetSymbol = targetSymbol;
		this.direction = direction;
		this.setOnly = setOnly;
		this.plusRounds = plusRounds;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(targetSymbol.getSymbolType().id);
		buf.writeInt(targetSymbol.getId());
		buf.writeInt(direction.id);
		buf.writeBoolean(setOnly);
		buf.writeInt(plusRounds);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		SymbolTypeEnum symbolType = SymbolTypeEnum.valueOf(buf.readInt());
		targetSymbol = symbolType.valueOfSymbol(buf.readInt());
		direction = EnumSpinDirection.valueOf(buf.readInt());
		setOnly = buf.readBoolean();
		plusRounds = buf.readInt();
	}
}
