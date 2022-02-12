package mrjake.aunis.state.stargate;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.stargate.EnumSpinDirection;
import mrjake.aunis.stargate.network.SymbolInterface;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.state.State;

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
