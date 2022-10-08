package tauri.dev.jsg.renderer.activation;

import tauri.dev.jsg.stargate.network.SymbolUniverseEnum;

public class UniverseActivation extends Activation<SymbolUniverseEnum> {

	public UniverseActivation(SymbolUniverseEnum textureKey, long stateChange, boolean dim) {
		super(textureKey, stateChange, dim);
	}

	@Override
	protected float getMaxStage() {
		return 0.75f;
	}

	@Override
	protected float getTickMultiplier() {
		return 0.2f;
	}

}
