package tauri.dev.jsg.power.general;

import tauri.dev.jsg.config.JSGConfig;

public class EnergyRequiredToOperate {
	
	public int energyToOpen;
	public int keepAlive;

	public EnergyRequiredToOperate(int energyToOpen, int keepAlive) {
		this.energyToOpen = energyToOpen;
		this.keepAlive = keepAlive;
	}

	public static EnergyRequiredToOperate stargate(){
		return new EnergyRequiredToOperate(JSGConfig.Stargate.power.openingBlockToEnergyRatio, JSGConfig.Stargate.power.keepAliveBlockToEnergyRatioPerTick);
	}

	public static EnergyRequiredToOperate free(){
		return new EnergyRequiredToOperate(0, 0);
	}
	
	public EnergyRequiredToOperate(double energyToOpen, double keepAlive) {
		this((int)energyToOpen, (int)keepAlive);
	}

	@Override
	public String toString() {
		return "[open="+energyToOpen+", keepAlive="+keepAlive+"]";
	}

	public EnergyRequiredToOperate mul(double mul) {
		return new EnergyRequiredToOperate(Math.min(Integer.MAX_VALUE, (long) energyToOpen*mul), Math.min(Integer.MAX_VALUE, (long) keepAlive*mul));
	}

	public EnergyRequiredToOperate add(EnergyRequiredToOperate add) {
		return new EnergyRequiredToOperate(Math.min(Integer.MAX_VALUE, (long) energyToOpen+add.energyToOpen), Math.min(Integer.MAX_VALUE, (long) keepAlive+add.keepAlive));
	}

	public EnergyRequiredToOperate cap(int max) {
		return new EnergyRequiredToOperate(Math.min(energyToOpen, max), keepAlive);
	}
}
