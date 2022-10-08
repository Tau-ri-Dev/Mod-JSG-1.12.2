package tauri.dev.jsg.renderer.activation;

import tauri.dev.jsg.stargate.network.SymbolInterface;

public class DHDActivation extends Activation<SymbolInterface> {

	public DHDActivation(SymbolInterface textureKey, long stateChange, boolean dim) {
		super(textureKey, stateChange, dim);
	}

	@Override
	protected float getMaxStage() {
		return 5;
	}
	
	@Override
	protected float getTickMultiplier() {
		return (textureKey.origin() && !dim) ? 1 : 2;
	}
}
