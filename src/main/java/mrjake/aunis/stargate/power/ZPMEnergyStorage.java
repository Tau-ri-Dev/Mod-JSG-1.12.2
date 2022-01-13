package mrjake.aunis.stargate.power;

import mrjake.aunis.config.AunisConfig;

public class ZPMEnergyStorage extends StargateAbstractEnergyStorage {
	// Yeah, dumb, i know...
	public ZPMEnergyStorage() {
		super(AunisConfig.powerConfig.zpmEnergyStorage);
	}
}
