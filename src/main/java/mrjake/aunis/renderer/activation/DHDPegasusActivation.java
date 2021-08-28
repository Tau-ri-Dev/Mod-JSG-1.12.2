package mrjake.aunis.renderer.activation;

import mrjake.aunis.stargate.network.SymbolPegasusEnum;

public class DHDPegasusActivation extends Activation<SymbolPegasusEnum> {

	public DHDPegasusActivation(SymbolPegasusEnum textureKey, long stateChange, boolean dim) {
		super(textureKey, stateChange, dim);
	}

	@Override
	protected float getMaxStage() {
		return 5;
	}
	
	@Override
	protected float getTickMultiplier() {
		return (textureKey == SymbolPegasusEnum.BRB && !dim) ? 1 : 2;
	}
}
